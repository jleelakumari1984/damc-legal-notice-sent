package com.notices.worker.service.schedule.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.notices.domain.config.LocationProperties;
import com.notices.domain.dao.excel.NoticeExcelDao;
import com.notices.domain.dao.excel.ScheduledNoticeItemDao;
import com.notices.domain.dao.notice.NoticeExcelMappingDao;
import com.notices.domain.dao.user.UserSmsCredentialDao;
import com.notices.domain.dao.user.UserWhatsAppCredentialDao;
import com.notices.domain.dto.notice.NoticeValidationFileDto;
import com.notices.domain.dto.notification.SmsDataDto;
import com.notices.domain.dto.notification.WhatsAppDataDto;
import com.notices.domain.entity.master.MasterProcessSmsConfigDetailEntity;
import com.notices.domain.entity.master.MasterProcessTemplateDetailEntity;
import com.notices.domain.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.notices.domain.entity.schedule.ScheduledNoticeEntity;
import com.notices.domain.entity.schedule.ScheduledNoticeItemEntity;
import com.notices.domain.enums.NoticeScheduleStatus;
import com.notices.domain.enums.TemplateApproveStatus;
import com.notices.domain.repository.master.MasterProcessSmsConfigDetailRepository;
import com.notices.domain.repository.master.MasterProcessTemplateDetailRepository;
import com.notices.domain.repository.master.MasterProcessWhatsappConfigDetailRepository;
import com.notices.domain.repository.schedule.ScheduledNoticeItemRepository;
import com.notices.domain.repository.schedule.ScheduledNoticeRepository;
import com.notices.domain.service.notification.SmsSenderService;
import com.notices.domain.service.notification.WhatsappSenderService;
import com.notices.domain.service.schedule.SaveScheduleService;
import com.notices.domain.util.EndpointUtil;
import com.notices.domain.util.ExcelParserUtil;
import com.notices.domain.util.NoticeScheduleUtil;
import com.notices.domain.util.converter.EntityDaoConverter;
import com.notices.worker.service.schedule.NoticeScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeScheduleServiceImpl implements NoticeScheduleService {

    private final MasterProcessTemplateDetailRepository noticeTemplateRepository;
    private final ScheduledNoticeRepository scheduledNoticeRepository;
    private final LocationProperties storageProperties;
    private final ScheduledNoticeItemRepository scheduledNoticeItemRepository;
    private final MasterProcessSmsConfigDetailRepository smsConfigRepository;
    private final MasterProcessWhatsappConfigDetailRepository whatsappConfigRepository;

    private final SmsSenderService smsSenderService;
    private final WhatsappSenderService whatsappSenderService;

    private final NoticeScheduleUtil noticeScheduleUtil;
    private final ExcelParserUtil excelParserUtil;
    private final EntityDaoConverter entityDaoConverter;
    private final EndpointUtil endpointUtil;

    private final SaveScheduleService saveScheduleService;

    @Override
    @Transactional
    public List<NoticeExcelDao> noticePendingExcelParsing() {
        long identifier = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        // Atomically claim up to 50 EXCELUPLOADED rows with this identifier
        int claimed = scheduledNoticeRepository.claimPendingForExcelParsing(identifier,
                NoticeScheduleStatus.EXCELPROCESSING.name(),
                NoticeScheduleStatus.EXCELUPLOADED.name());
        if (claimed == 0) {
            log.info("No notices pending Excel parsing");
            return Collections.emptyList();
        }
        log.info("Claimed {} notices for Excel parsing with identifier {}", claimed, identifier);

        List<ScheduledNoticeEntity> notices = scheduledNoticeRepository.findByIdentifier(identifier);
        List<NoticeExcelDao> result = new ArrayList<>();
        for (ScheduledNoticeEntity notice : notices) {
            NoticeExcelDao excelDao = NoticeExcelDao.builder()
                    .scheduledNoticeId(notice.getId())
                    .ExcelName(notice.getOriginalFileName()).build();
            try {

                MasterProcessTemplateDetailEntity template = noticeTemplateRepository
                        .findByIdWithExcelMappings(notice.getProcessSno())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid notice type"));

                if (template.getExcelMappings() == null || template.getExcelMappings().isEmpty()) {
                    throw new IllegalArgumentException("Notice type is not properly configured with Excel mappings");
                }
                Path extractedPath = excelParserUtil
                        .extractZip(Paths.get(storageProperties.getUploadDir(), notice.getExtractedFolderPath()));
                NoticeValidationFileDto validationRows = saveScheduleService.storeExcelData(extractedPath, null,
                        template,
                        notice);
                excelDao.setValidationRows(validationRows.getRows());
                notice.setFailureReason(null);
                notice.setStatus(NoticeScheduleStatus.EXCELCOMPLETED);
                log.info("Excel parsing completed for notice id {}", notice.getId());

            } catch (Exception ex) {
                log.error("Excel parsing failed for notice id {}", notice.getId(), ex);
                notice.setStatus(NoticeScheduleStatus.EXCELFAILED);
                notice.setFailureReason(ex.getMessage());
                excelDao.setFailureReason(ex.getMessage());
            } finally {
                result.add(excelDao);
                notice.setProcessedAt(LocalDateTime.now());
                scheduledNoticeRepository.save(notice);
            }
        }

        log.info("Excel parsing done. Noticeed {} items across {} notices", result.size(), notices.size());
        return result;
    }

    @Override
    @Transactional
    public List<ScheduledNoticeItemDao> noticePendingNoticeItems() {
        long identifier = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        int claimed = scheduledNoticeItemRepository.claimPendingItems(identifier,
                NoticeScheduleStatus.PROCESSING.name(),
                NoticeScheduleStatus.PENDING.name());
        if (claimed == 0) {
            log.info("No notices pending Excel parsing");
            return Collections.emptyList();
        }
        List<ScheduledNoticeItemEntity> notices = scheduledNoticeItemRepository.findByIdentifier(identifier);
        List<ScheduledNoticeItemDao> result = new ArrayList<>();

        Map<ScheduledNoticeEntity, List<ScheduledNoticeItemEntity>> itemsByNotice = notices.stream()
                .collect(Collectors.groupingBy(ScheduledNoticeItemEntity::getScheduledNotice));

        for (Map.Entry<ScheduledNoticeEntity, List<ScheduledNoticeItemEntity>> entry : itemsByNotice.entrySet()) {
            ScheduledNoticeEntity notice = entry.getKey();
            Path zipPath = Paths.get(storageProperties.getUploadDir(), notice.getExtractedFolderPath());
            List<ScheduledNoticeItemEntity> items = entry.getValue();
            try {
                MasterProcessTemplateDetailEntity template = noticeTemplateRepository
                        .findByIdWithExcelMappings(notice.getProcessSno())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid notice type"));
                List<NoticeExcelMappingDao> mobileColumns = template.getExcelMappings().stream()
                        .filter(mapping -> mapping.getIsMobile() == 1)
                        .map(entityDaoConverter::toNoticeExcelMappingDao)
                        .toList();
                List<NoticeExcelMappingDao> attachmentColumns = template.getExcelMappings().stream()
                        .filter(mapping -> mapping.getIsAttachment() == 1)
                        .map(entityDaoConverter::toNoticeExcelMappingDao)
                        .toList();
                List<MasterProcessSmsConfigDetailEntity> smsConfigList = null;
                if (Boolean.TRUE.equals(notice.getSendSms())) {
                    smsConfigList = smsConfigRepository
                            .findByProcessIdAndStatusAndApprovedStatus(notice.getProcessSno(), 1,
                                    TemplateApproveStatus.APPROVED.getValue());
                    if (smsConfigList.isEmpty()) {
                        throw new IllegalArgumentException("Active SMS config not found");
                    }
                }
                List<MasterProcessWhatsappConfigDetailEntity> waConfigList = null;
                if (Boolean.TRUE.equals(notice.getSendWhatsapp())) {
                    waConfigList = whatsappConfigRepository
                            .findByProcessIdAndStatusAndApprovedStatus(notice.getProcessSno(), 1,
                                    TemplateApproveStatus.APPROVED.getValue());
                    if (waConfigList.isEmpty()) {
                        throw new IllegalArgumentException("Active WhatsApp config not found");
                    }
                }
                for (ScheduledNoticeItemEntity item : items) {
                    UserSmsCredentialDao smsCredential = Boolean.TRUE.equals(notice.getSendSms())
                            ? endpointUtil.getSmsCredential(notice.getCreatedBy())
                            : null;
                    UserWhatsAppCredentialDao waCredential = Boolean.TRUE.equals(notice.getSendWhatsapp())
                            ? endpointUtil.getWhatsAppCredential(notice.getCreatedBy())
                            : null;
                    try {
                        String mobileNumber = noticeScheduleUtil.getMobileNumber(item, mobileColumns);
                        Map<String, Object> props = noticeScheduleUtil.getMapProperties(item,
                                template.getExcelMappings());
                        List<String> attachments = noticeScheduleUtil.getAttchementFiles(item, attachmentColumns,
                                zipPath);
                        if (smsConfigList != null) {
                            try {
                                for (MasterProcessSmsConfigDetailEntity config : smsConfigList) {
                                    SmsDataDto smsData = SmsDataDto.builder()
                                            .agreementNumber(item.getAgreementNumber())
                                            .scheduleId(notice.getId())
                                            .scheduleItemId(item.getId())
                                            .mobileNumber(mobileNumber)
                                            .props(props)
                                            .config(config)
                                            .build();
                                    smsSenderService.send(smsData, template, smsCredential);
                                }
                            } catch (Exception e) {
                                log.error("Error sending SMS for item id {}", item.getId(), e);
                            }
                        }

                        if (waConfigList != null) {
                            try {
                                for (MasterProcessWhatsappConfigDetailEntity config : waConfigList) {
                                    WhatsAppDataDto waData = WhatsAppDataDto.builder()
                                            .agreementNumber(item.getAgreementNumber())
                                            .scheduleId(notice.getId())
                                            .scheduleItemId(item.getId())
                                            .mobileNumber(mobileNumber)
                                            .props(props)
                                            .config(config)
                                            .build();
                                    whatsappSenderService.send(waData, template, attachments, waCredential);
                                }
                            } catch (Exception e) {
                                log.error("Error building WhatsApp data for item id {}", item.getId(), e);
                            }

                        }

                        item.setStatus(NoticeScheduleStatus.COMPLETED);
                        item.setProcessedAt(LocalDateTime.now());
                        item.setFailureReason(null);
                    } catch (Exception ex) {
                        log.error("Failed processing agreement {}", item.getAgreementNumber(), ex);
                        item.setStatus(NoticeScheduleStatus.FAILED);
                        item.setFailureReason(ex.getMessage());
                        item.setProcessedAt(LocalDateTime.now());
                    }
                    ScheduledNoticeItemDao noticeItemDao = ScheduledNoticeItemDao.builder()
                            .scheduledNoticeId(notice.getId())
                            .excelData(noticeScheduleUtil.getExcelData(item))
                            .failureReason(item.getFailureReason())
                            .status(item.getStatus().toString())
                            .agreementNumber(item.getAgreementNumber()).build();
                    result.add(noticeItemDao);

                }

            } catch (Exception ex) {
                log.error("PendingNoticeItems failed for notice id {}", notice.getId(), ex);

            } finally {
                items.stream()
                        .filter(item -> NoticeScheduleStatus.PROCESSING.equals(item.getStatus()))
                        .forEach(item -> {
                            item.setStatus(NoticeScheduleStatus.PENDING);
                            item.setIdentifier(0L);
                            scheduledNoticeItemRepository.save(item);
                        });

                long failureCount = scheduledNoticeItemRepository
                        .countByScheduledNoticeIdAndStatus(notice.getId(), NoticeScheduleStatus.FAILED);
                if (failureCount > 0) {
                    notice.setStatus(NoticeScheduleStatus.FAILED);
                    notice.setProcessedAt(LocalDateTime.now());
                    scheduledNoticeRepository.save(notice);
                    log.info("ScheduledNotice id={} marked as FAILED ",
                            notice.getId());
                } else {
                    long pendingCount = scheduledNoticeItemRepository
                            .countByScheduledNoticeIdAndStatus(notice.getId(), NoticeScheduleStatus.PENDING);
                    if (pendingCount == 0) {
                        notice.setStatus(NoticeScheduleStatus.COMPLETED);
                        notice.setProcessedAt(LocalDateTime.now());
                        scheduledNoticeRepository.save(notice);
                        log.info("ScheduledNotice id={} marked as COMPLETED (no pending/processing items remaining)",
                                notice.getId());
                    }
                }

            }

        }
        return result;
    }

}

package com.damc.legalnotices.service.schedule.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.excel.ExcelPreviewDao;
import com.damc.legalnotices.dao.excel.ExcelPreviewRowDao;
import com.damc.legalnotices.dao.excel.NoticeExcelDao;
import com.damc.legalnotices.dao.excel.ScheduledNoticeItemDao;
import com.damc.legalnotices.dao.notice.NoticeExcelMappingDao;
import com.damc.legalnotices.dao.notice.NoticeValidationDao;
import com.damc.legalnotices.dao.schedule.ScheduledNoticeDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dao.user.UserSmsCredentialDao;
import com.damc.legalnotices.dao.user.UserWhatsAppCredentialDao;
import com.damc.legalnotices.dto.notice.NoticeScheduleRequestDto;
import com.damc.legalnotices.dto.notice.NoticeValidationFileDto;
import com.damc.legalnotices.dto.notice.NoticeValidationRowDto;
import com.damc.legalnotices.dto.notice.SendSampleNoticeDto;
import com.damc.legalnotices.dto.notification.SmsDataDto;
import com.damc.legalnotices.dto.notification.WhatsAppDataDto;
import com.damc.legalnotices.entity.master.MasterProcessExcelMappingEntity;
import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.damc.legalnotices.entity.schedule.ScheduledNoticeEntity;
import com.damc.legalnotices.entity.schedule.ScheduledNoticeItemEntity;
import com.damc.legalnotices.enums.NoticeScheduleStatus;
import com.damc.legalnotices.enums.TemplateApproveStatus;
import com.damc.legalnotices.repository.master.MasterProcessSmsConfigDetailRepository;
import com.damc.legalnotices.repository.master.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.repository.master.MasterProcessWhatsappConfigDetailRepository;
import com.damc.legalnotices.repository.schedule.ScheduledNoticeItemRepository;
import com.damc.legalnotices.repository.schedule.ScheduledNoticeRepository;
import com.damc.legalnotices.service.notification.SmsSenderService;
import com.damc.legalnotices.service.notification.WhatsappSenderService;
import com.damc.legalnotices.service.schedule.NoticeScheduleService;
import com.damc.legalnotices.util.EndpointUtil;
import com.damc.legalnotices.util.ExcelParserUtil;
import com.damc.legalnotices.util.NoticeScheduleUtil;
import com.damc.legalnotices.util.converter.EntityDaoConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeScheduleServiceImpl implements NoticeScheduleService {
    private final ObjectMapper objectMapper;

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

    @Override
    @Transactional
    public NoticeValidationDao scheduleNotice(LoginUserDao sessionUser, NoticeScheduleRequestDto request) {
        Long noticeSno = request.getNoticeSno();
        Boolean sendSms = request.getSendSms();
        Boolean sendWhatsapp = request.getSendWhatsapp();
        MultipartFile zipFile = request.getZipFile();

        if (zipFile == null || zipFile.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        String originalName = zipFile.getOriginalFilename();
        if (!excelParserUtil.checkIsValidFileFormat(originalName)) {
            throw new IllegalArgumentException("Only ZIP (.zip) or Excel (.xlsx, .xls) files are allowed");
        }

        if (!Boolean.TRUE.equals(sendSms) && !Boolean.TRUE.equals(sendWhatsapp)) {
            throw new IllegalArgumentException("Please select at least one channel (SMS or WhatsApp)");
        }

        MasterProcessTemplateDetailEntity template = noticeTemplateRepository.findByIdWithExcelMappings(noticeSno)
                .orElseThrow(() -> new IllegalArgumentException("Invalid notice type"));

        if (template.getExcelMappings() == null || template.getExcelMappings().isEmpty()) {
            throw new IllegalArgumentException("Notice type is not properly configured with Excel mappings");
        }

        Path savedFilePath;
        Path extractedPath;
        Path directExcelPath = null;
        Path uploadDir = Path.of(storageProperties.getUploadDir()).toAbsolutePath().normalize();
        if (excelParserUtil.isZipFile(originalName)) {
            savedFilePath = excelParserUtil.saveZipFile(sessionUser, zipFile);
            extractedPath = excelParserUtil.extractZip(savedFilePath);
            extractedPath = excelParserUtil.findExcel(extractedPath).getParent();
        } else {
            savedFilePath = excelParserUtil.saveExcelFile(sessionUser, zipFile);
            extractedPath = savedFilePath.getParent();
            directExcelPath = savedFilePath;
        }
        String removedUploadPath = savedFilePath.toAbsolutePath().toString().replace(uploadDir.toString(), "");
        String removedExtractedPath = extractedPath.toAbsolutePath().toString().replace(uploadDir.toString(), "");
        ScheduledNoticeEntity scheduledNotice = new ScheduledNoticeEntity();
        scheduledNotice.setProcess(template);
        scheduledNotice.setOriginalFileName(zipFile.getOriginalFilename());
        scheduledNotice.setZipFilePath(removedUploadPath);
        scheduledNotice.setExtractedFolderPath(removedExtractedPath);
        scheduledNotice.setSendSms(Boolean.TRUE.equals(sendSms));
        scheduledNotice.setSendWhatsapp(Boolean.TRUE.equals(sendWhatsapp));
        scheduledNotice.setCreatedBy(sessionUser.getId());
        scheduledNotice.setCreatedAt(LocalDateTime.now());
        scheduledNotice.setStatus(NoticeScheduleStatus.EXCELPROCESSING);
        scheduledNotice = scheduledNoticeRepository.save(scheduledNotice);
        log.info("ScheduledNotice created with id: {}", scheduledNotice.getId());
        NoticeScheduleStatus excelNotice = NoticeScheduleStatus.EXCELUPLOADED;
        try {
            NoticeValidationFileDto validationRows = storeExcelData(extractedPath, directExcelPath, template,
                    scheduledNotice);
            excelNotice = NoticeScheduleStatus.EXCELCOMPLETED;
            validationRows.getRows().sort(Comparator.comparing(NoticeValidationRowDto::getAgreementNumber));

            return NoticeValidationDao.builder()
                    .scheduleId(scheduledNotice.getId())
                    .originalZipFile(scheduledNotice.getOriginalFileName())
                    .extractedFolder(scheduledNotice.getExtractedFolderPath())
                    .status(scheduledNotice.getStatus().name())
                    .fileData(validationRows)
                    .build();
        } finally {
            scheduledNotice.setStatus(excelNotice);
            scheduledNotice = scheduledNoticeRepository.save(scheduledNotice);
        }
    }

    private NoticeValidationFileDto storeExcelData(Path extractedPath, Path directExcelPath,
            MasterProcessTemplateDetailEntity template, ScheduledNoticeEntity scheduledNotice) {
        NoticeExcelMappingDao keyColumns = template.getExcelMappings().stream()
                .filter(mapping -> mapping.getIsAgreement() == 1)
                .map(entityDaoConverter::toNoticeExcelMappingDao)
                .findFirst()
                .orElse(null);
        NoticeExcelMappingDao customerNameColumns = template.getExcelMappings().stream()
                .filter(mapping -> mapping.getIsCustomerName() == 1)
                .map(entityDaoConverter::toNoticeExcelMappingDao)
                .findFirst()
                .orElse(null);
        List<NoticeExcelMappingDao> attachmentColumns = template.getExcelMappings().stream()
                .filter(mapping -> mapping.getIsAttachment() == 1)
                .map(entityDaoConverter::toNoticeExcelMappingDao)
                .toList();
        Path excelPath = directExcelPath != null ? directExcelPath : excelParserUtil.findExcel(extractedPath);
        ExcelPreviewDao excelPreview;
        try {
            excelPreview = excelParserUtil.parseAsPreview(excelPath);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to parse excel from ZIP: " + ex.getMessage());
        }
        if (excelPreview.getRows().isEmpty()) {
            throw new IllegalArgumentException("Excel has no valid agreement rows");
        }
        List<NoticeValidationRowDto> validationRows = new ArrayList<>();
        for (ExcelPreviewRowDao row : excelPreview.getRows()) {
            String agreementNumber = row.getKeyColumnData(List.of(keyColumns), null);
            String customerName = row.getKeyColumnData(List.of(customerNameColumns), null);
            String excelData;
            try {
                excelData = objectMapper.writeValueAsString(row.getData());
            } catch (Exception ex) {
                excelData = row.toString();
            }

            ScheduledNoticeItemEntity item = new ScheduledNoticeItemEntity();
            item.setScheduledNotice(scheduledNotice);
            item.setAgreementNumber(agreementNumber);
            item.setCustomerName(customerName);
            item.setAttachments(row.getKeyColumnData(attachmentColumns, ";"));
            item.setExcelData(excelData);
            item.setStatus(NoticeScheduleStatus.UPLOADED);
            scheduledNoticeItemRepository.save(item);

            validationRows.add(NoticeValidationRowDto.builder()
                    .agreementNumber(agreementNumber)
                    .customerName(customerName)
                    .excelData(excelData)
                    .build());
        }
        return NoticeValidationFileDto.builder()
                .columnNames(excelPreview.getColumnNames())
                .rows(validationRows)
                .build();
    }

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
                NoticeValidationFileDto validationRows = storeExcelData(extractedPath, null, template,
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

    @Override
    public void sendSampleNotice(LoginUserDao sessionUser, SendSampleNoticeDto request) {
        if (request.getNoticeSno() == null) {
            throw new IllegalArgumentException("Notice type is required");
        }
        if (!Boolean.TRUE.equals(request.getSendSms()) && !Boolean.TRUE.equals(request.getSendWhatsapp())) {
            throw new IllegalArgumentException("Please select at least one channel (SMS or WhatsApp)");
        }

        MasterProcessTemplateDetailEntity template = noticeTemplateRepository
                .findByIdWithExcelMappings(request.getNoticeSno())
                .orElseThrow(() -> new IllegalArgumentException("Invalid notice type"));

        Map<String, Object> rowData = request.getRowData() != null ? request.getRowData() : Collections.emptyMap();
        Map<String, Object> props = buildSampleProps(rowData, template.getExcelMappings());

        if (Boolean.TRUE.equals(request.getSendSms())) {
            try {
                UserSmsCredentialDao smsCredential = endpointUtil.getSmsCredential(sessionUser.getId());
                List<MasterProcessSmsConfigDetailEntity> smsConfigList = smsConfigRepository
                        .findByProcessIdAndStatusAndApprovedStatus(request.getNoticeSno(), 1,
                                TemplateApproveStatus.APPROVED.getValue());
                if (smsConfigList.isEmpty()) {
                    throw new IllegalArgumentException("Active SMS config not found for this notice type");
                }
                for (MasterProcessSmsConfigDetailEntity smsConfig : smsConfigList) {
                    SmsDataDto smsData = SmsDataDto.builder()
                            .mobileNumber(request.getMobileNumber())
                            .props(props)
                            .config(smsConfig)
                            .build();
                    smsSenderService.send(smsData, template, smsCredential);
                }

            } catch (Exception e) {
                log.error("Error sending sample SMS notice for noticeSno {}", request.getNoticeSno(), e);
            }
        }

        if (Boolean.TRUE.equals(request.getSendWhatsapp())) {
            try {
                UserWhatsAppCredentialDao waCredential = endpointUtil.getWhatsAppCredential(sessionUser.getId());
                List<MasterProcessWhatsappConfigDetailEntity> waConfigList = whatsappConfigRepository
                        .findByProcessIdAndStatusAndApprovedStatus(request.getNoticeSno(), 1,
                                TemplateApproveStatus.APPROVED.getValue());
                if (waConfigList.isEmpty()) {
                    throw new IllegalArgumentException("Active WhatsApp config not found for this notice type");
                }
                for (MasterProcessWhatsappConfigDetailEntity waConfig : waConfigList) {
                    WhatsAppDataDto waData = WhatsAppDataDto.builder()
                            .mobileNumber(request.getMobileNumber())
                            .props(props)
                            .config(waConfig)
                            .build();
                    whatsappSenderService.send(waData, template, Collections.emptyList(), waCredential);
                }
            } catch (Exception e) {
                log.error("Error sending sample WhatsApp notice for noticeSno {}", request.getNoticeSno(), e);
            }
        }
    }

    public Map<String, Object> buildSampleProps(Map<String, Object> rowData,
            List<MasterProcessExcelMappingEntity> excelMappings) {
        if (excelMappings == null || excelMappings.isEmpty()) {
            return rowData;
        }
        return rowData.entrySet().stream()
                .filter(entry -> excelMappings.stream()
                        .anyMatch(mapping -> mapping.getExcelFieldName().equals(entry.getKey())))
                .collect(Collectors.toMap(
                        entry -> excelMappings.stream()
                                .filter(mapping -> mapping.getExcelFieldName().equals(entry.getKey()))
                                .map(MasterProcessExcelMappingEntity::getDbFieldName)
                                .findFirst()
                                .orElse(entry.getKey()),
                        Map.Entry::getValue));
    }

    @Transactional
    @Override
    public ScheduledNoticeDao stopSchedule(LoginUserDao sessionUser, Long noticeId) {
        ScheduledNoticeEntity notice = scheduledNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + noticeId));
        if (notice.getStatus() == null || NoticeScheduleStatus.EXCELUPLOADED == notice.getStatus()
                || NoticeScheduleStatus.STOP == notice.getStatus()) {
            throw new IllegalStateException("Schedule is already stopped: " + noticeId);
        }
        scheduledNoticeItemRepository.updateStatusByScheduledNoticeId(noticeId, NoticeScheduleStatus.STOP.name());
        notice.setStatus(NoticeScheduleStatus.STOP);
        notice = scheduledNoticeRepository.save(notice);

        return entityDaoConverter.toScheduledNoticeDao(notice);
    }

    @Transactional
    @Override
    public ScheduledNoticeDao startSchedule(LoginUserDao sessionUser, Long noticeId) {
        ScheduledNoticeEntity notice = scheduledNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + noticeId));

        if (notice.getStatus() == null
                || NoticeScheduleStatus.START == notice.getStatus()) {
            throw new IllegalStateException("Schedule is already started: " + noticeId);
        }
        scheduledNoticeItemRepository.updateStatusByScheduledNoticeId(noticeId, NoticeScheduleStatus.PENDING.name());
        notice.setStatus(NoticeScheduleStatus.START);
        notice = scheduledNoticeRepository.save(notice);
        return entityDaoConverter.toScheduledNoticeDao(notice);
    }

}

package com.damc.legalnotices.service.impl.schedule;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.ProcessExcelMappingDao;
import com.damc.legalnotices.dao.ProcessedExcelDao;
import com.damc.legalnotices.dao.ProcessedNoticeItemDao;
import com.damc.legalnotices.dto.NoticeValidationFileDto;
import com.damc.legalnotices.dto.SendSampleNoticeRequestDto;
import com.damc.legalnotices.dto.SmsDataDto;
import com.damc.legalnotices.dto.WhatsAppDataDto;
import com.damc.legalnotices.entity.MasterProcessSmsConfigDetailEntity;
import com.damc.legalnotices.entity.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.MasterProcessWhatsappConfigDetailEntity;
import com.damc.legalnotices.entity.ProcessExcelMappingEntity;
import com.damc.legalnotices.entity.ScheduledNoticeEntity;
import com.damc.legalnotices.entity.ScheduledNoticeItemEntity;
import com.damc.legalnotices.enums.ProcessingStatus;
import com.damc.legalnotices.repository.MasterProcessSmsConfigDetailRepository;
import com.damc.legalnotices.repository.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.repository.MasterProcessWhatsappConfigDetailRepository;
import com.damc.legalnotices.repository.ScheduledNoticeItemRepository;
import com.damc.legalnotices.repository.ScheduledNoticeRepository;
import com.damc.legalnotices.service.NoticeProcessingService;
import com.damc.legalnotices.service.NoticeService;
import com.damc.legalnotices.service.SmsSenderService;
import com.damc.legalnotices.service.WhatsappSenderService;
import com.damc.legalnotices.util.EntityDaoConverter;
import com.damc.legalnotices.util.ExcelParserUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeProcessingServiceImpl implements NoticeProcessingService {

    private final MasterProcessTemplateDetailRepository processTemplateRepository;
    private final ScheduledNoticeRepository scheduledNoticeRepository;
    private final ScheduledNoticeItemRepository scheduledNoticeItemRepository;
    private final MasterProcessSmsConfigDetailRepository smsConfigRepository;
    private final MasterProcessWhatsappConfigDetailRepository whatsappConfigRepository;
    private final SmsSenderService smsSenderService;
    private final WhatsappSenderService whatsappSenderService;
    private final NoticeService noticeService;
    private final ExcelParserUtil excelParserUtil;
    private final EntityDaoConverter entityDaoConverter;
    private final ObjectMapper objectMapper;
    private final LocationProperties storageProperties;

    @Override
    @Transactional
    public List<ProcessedExcelDao> processPendingExcelParsing() {
        long identifier = System.currentTimeMillis();

        // Atomically claim up to 50 EXCELUPLOADED rows with this identifier
        int claimed = scheduledNoticeRepository.claimPendingForExcelParsing(identifier,
                ProcessingStatus.EXCELPROCESSING.name(),
                ProcessingStatus.EXCELUPLOADED.name());
        if (claimed == 0) {
            log.info("No notices pending Excel parsing");
            return Collections.emptyList();
        }
        log.info("Claimed {} notices for Excel parsing with identifier {}", claimed, identifier);

        List<ScheduledNoticeEntity> notices = scheduledNoticeRepository.findByIdentifier(identifier);
        List<ProcessedExcelDao> result = new ArrayList<>();
        for (ScheduledNoticeEntity notice : notices) {
            ProcessedExcelDao excelDao = ProcessedExcelDao.builder()
                    .scheduledNoticeId(notice.getId())
                    .ExcelName(notice.getOriginalFileName()).build();
            try {
                MasterProcessTemplateDetailEntity template = processTemplateRepository
                        .findByIdWithExcelMappings(notice.getProcessSno())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid notice type"));

                if (template.getExcelMappings() == null || template.getExcelMappings().isEmpty()) {
                    throw new IllegalArgumentException("Notice type is not properly configured with Excel mappings");
                }
                Path extractedPath = excelParserUtil
                        .extractZip(Paths.get(storageProperties.getUploadDir(), notice.getExtractedFolderPath()));
                NoticeValidationFileDto validationRows = noticeService.storeExcelData(extractedPath, template,
                        notice);
                excelDao.setValidationRows(validationRows.getRows());
                notice.setFailureReason(null);
                notice.setStatus(ProcessingStatus.EXCELCOMPLETED);
                log.info("Excel parsing completed for notice id {}", notice.getId());

            } catch (Exception ex) {
                log.error("Excel parsing failed for notice id {}", notice.getId(), ex);
                notice.setStatus(ProcessingStatus.EXCELFAILED);
                notice.setFailureReason(ex.getMessage());
                excelDao.setFailureReason(ex.getMessage());
            } finally {
                result.add(excelDao);
                notice.setProcessedAt(LocalDateTime.now());
                scheduledNoticeRepository.save(notice);
            }
        }

        log.info("Excel parsing done. Processed {} items across {} notices", result.size(), notices.size());
        return result;
    }

    @Override
    @Transactional
    public List<ProcessedNoticeItemDao> processPendingNoticeItems() {
        long identifier = System.currentTimeMillis();
        int claimed = scheduledNoticeItemRepository.claimPendingItems(identifier,
                ProcessingStatus.PROCESSING.name(),
                ProcessingStatus.PENDING.name());
        if (claimed == 0) {
            log.info("No notices pending Excel parsing");
            return Collections.emptyList();
        }
        List<ScheduledNoticeItemEntity> notices = scheduledNoticeItemRepository.findByIdentifier(identifier);
        List<ProcessedNoticeItemDao> result = new ArrayList<>();

        Map<ScheduledNoticeEntity, List<ScheduledNoticeItemEntity>> itemsByNotice = notices.stream()
                .collect(Collectors.groupingBy(ScheduledNoticeItemEntity::getScheduledNotice));

        for (Map.Entry<ScheduledNoticeEntity, List<ScheduledNoticeItemEntity>> entry : itemsByNotice.entrySet()) {
            ScheduledNoticeEntity notice = entry.getKey();
            Path zipPath = Paths.get(storageProperties.getUploadDir(), notice.getExtractedFolderPath());
            List<ScheduledNoticeItemEntity> items = entry.getValue();
            try {
                MasterProcessTemplateDetailEntity template = processTemplateRepository
                        .findByIdWithExcelMappings(notice.getProcessSno())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid notice type"));
                List<ProcessExcelMappingDao> mobileColumns = template.getExcelMappings().stream()
                        .filter(mapping -> mapping.getIsMobile() == 1)
                        .map(entityDaoConverter::toProcessExcelMappingDao)
                        .toList();
                List<ProcessExcelMappingDao> attachmentColumns = template.getExcelMappings().stream()
                        .filter(mapping -> mapping.getIsAttachment() == 1)
                        .map(entityDaoConverter::toProcessExcelMappingDao)
                        .toList();
                MasterProcessSmsConfigDetailEntity smsConfig = null;
                if (Boolean.TRUE.equals(notice.getSendSms())) {
                    smsConfig = smsConfigRepository
                            .findFirstByProcessIdAndStatus(notice.getProcessSno(), 1)
                            .orElseThrow(() -> new IllegalArgumentException("Active SMS config not found"));
                }
                MasterProcessWhatsappConfigDetailEntity waConfig = null;
                if (Boolean.TRUE.equals(notice.getSendWhatsapp())) {
                    waConfig = whatsappConfigRepository
                            .findFirstByProcessIdAndStatus(notice.getProcessSno(), 1)
                            .orElseThrow(() -> new IllegalArgumentException("Active WhatsApp config not found"));
                }
                for (ScheduledNoticeItemEntity item : items) {
                    try {
                        String mobileNumber = getMobileNumber(item, mobileColumns);
                        Map<String, Object> props = getMapProperties(item, template.getExcelMappings());
                        List<String> attachments = getAttchementFiles(item, attachmentColumns, zipPath);
                        if (smsConfig != null) {
                            try {
                                SmsDataDto smsData = SmsDataDto.builder()
                                        .agreementNumber(item.getAgreementNumber())
                                        .scheduleId(notice.getId())
                                        .scheduleItemId(item.getId())
                                        .mobileNumber(mobileNumber)
                                        .props(props)
                                        .config(smsConfig)
                                        .build();
                                smsSenderService.send(smsData, template);
                            } catch (Exception e) {
                                log.error("Error sending SMS for item id {}", item.getId(), e);
                            }
                        }

                        if (waConfig != null) {
                            try {
                                WhatsAppDataDto waData = WhatsAppDataDto.builder()
                                        .agreementNumber(item.getAgreementNumber())
                                        .scheduleId(notice.getId())
                                        .scheduleItemId(item.getId())
                                        .mobileNumber(mobileNumber)
                                        .props(props)
                                        .config(waConfig)
                                        .build();
                                whatsappSenderService.send(waData, template, attachments);
                            } catch (Exception e) {
                                log.error("Error building WhatsApp data for item id {}", item.getId(), e);
                            }

                        }

                        item.setStatus(ProcessingStatus.COMPLETED);
                        item.setProcessedAt(LocalDateTime.now());
                        item.setFailureReason(null);
                    } catch (Exception ex) {
                        log.error("Failed processing agreement {}", item.getAgreementNumber(), ex);
                        item.setStatus(ProcessingStatus.FAILED);
                        item.setFailureReason(ex.getMessage());
                        item.setProcessedAt(LocalDateTime.now());
                    }
                    ProcessedNoticeItemDao noticeItemDao = ProcessedNoticeItemDao.builder()
                            .scheduledNoticeId(notice.getId())
                            .excelData(getExcelData(item))
                            .failureReason(item.getFailureReason())
                            .status(item.getStatus().toString())
                            .agreementNumber(item.getAgreementNumber()).build();
                    result.add(noticeItemDao);

                }

            } catch (Exception ex) {
                log.error("PendingNoticeItems failed for notice id {}", notice.getId(), ex);

            } finally {
                items.stream()
                        .filter(item -> ProcessingStatus.PROCESSING.equals(item.getStatus()))
                        .forEach(item -> {
                            item.setStatus(ProcessingStatus.PENDING);
                            item.setIdentifier(0L);
                            scheduledNoticeItemRepository.save(item);
                        });
            }

        }
        return result;
    }

    private List<String> getAttchementFiles(ScheduledNoticeItemEntity item,
            List<ProcessExcelMappingDao> attachmentColumns,
            Path zipExtractPath) {
        Map<String, Object> row = getExcelData(item);
        if (row == null) {
            return Collections.emptyList();
        }
        List<String> attachments = new ArrayList<>();
        for (ProcessExcelMappingDao attachmentColumn : attachmentColumns) {
            Object attachObj = row.get(attachmentColumn.getExcelFieldName());
            String attachFileName = attachObj != null ? attachObj.toString() : null;
            if (attachFileName != null && !attachFileName.isBlank()) {
                Path attachPath = zipExtractPath.resolve(attachFileName);
                if (attachPath.toFile().exists()) {
                    attachments.add(attachPath.toString());
                } else {
                    log.warn("Attachment file {} not found for item id {}", attachPath, item.getId());
                }
            }
        }
        return attachments;
    }

    private Map<String, Object> getMapProperties(ScheduledNoticeItemEntity item,
            List<ProcessExcelMappingEntity> excelMappings) {
        Map<String, Object> row = getExcelData(item);
        if (row == null) {
            return Collections.emptyMap();
        }
        return row.entrySet().stream()
                .filter(entry -> excelMappings.stream()
                        .anyMatch(mapping -> mapping.getExcelFieldName().equals(entry.getKey())))
                .collect(Collectors.toMap(
                        entry -> excelMappings.stream()
                                .filter(mapping -> mapping.getExcelFieldName().equals(entry.getKey()))
                                .map(ProcessExcelMappingEntity::getDbFieldName)
                                .findFirst()
                                .orElse(entry.getKey()),
                        Map.Entry::getValue));
    }

    private Map<String, Object> getExcelData(ScheduledNoticeItemEntity item) {
        Map<String, Object> row;
        try {
            row = objectMapper.readValue(item.getExcelData(), new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            log.warn("Failed to parse excelData for item id {}", item.getId(), ex);
            return null;
        }
        if (row == null) {
            return Collections.emptyMap();
        }
        return row;
    }

    private String getMobileNumber(ScheduledNoticeItemEntity item, List<ProcessExcelMappingDao> mobileColumns) {
        Map<String, Object> row = getExcelData(item);
        if (row == null) {
            return "";
        }
        for (ProcessExcelMappingDao mobileColumn : mobileColumns) {
            Object mobileObj = row.get(mobileColumn.getExcelFieldName());
            String mobile = mobileObj != null ? mobileObj.toString() : null;
            if (mobile != null && !mobile.isBlank()) {
                return mobile;
            }
        }
        return null;
    }

    @Override
    public void sendSampleNotice(SendSampleNoticeRequestDto request) {
        if (request.getProcessSno() == null) {
            throw new IllegalArgumentException("Notice type is required");
        }
        if (!Boolean.TRUE.equals(request.getSendSms()) && !Boolean.TRUE.equals(request.getSendWhatsapp())) {
            throw new IllegalArgumentException("Please select at least one channel (SMS or WhatsApp)");
        }

        MasterProcessTemplateDetailEntity template = processTemplateRepository
                .findByIdWithExcelMappings(request.getProcessSno())
                .orElseThrow(() -> new IllegalArgumentException("Invalid notice type"));

        Map<String, Object> rowData = request.getRowData() != null ? request.getRowData() : Collections.emptyMap();
        Map<String, Object> props = buildSampleProps(rowData, template.getExcelMappings());

        if (Boolean.TRUE.equals(request.getSendSms())) {
            try {
                MasterProcessSmsConfigDetailEntity smsConfig = smsConfigRepository
                        .findFirstByProcessIdAndStatus(request.getProcessSno(), 1)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Active SMS config not found for this notice type"));
                SmsDataDto smsData = SmsDataDto.builder()
                        .mobileNumber(request.getMobileNumber())
                        .props(props)
                        .config(smsConfig)
                        .build();
                smsSenderService.send(smsData, template);
            } catch (Exception e) {
                log.error("Error sending sample SMS notice for processSno {}", request.getProcessSno(), e);
            }
        }

        if (Boolean.TRUE.equals(request.getSendWhatsapp())) {
            try {
                MasterProcessWhatsappConfigDetailEntity waConfig = whatsappConfigRepository
                        .findFirstByProcessIdAndStatus(request.getProcessSno(), 1)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Active WhatsApp config not found for this notice type"));
                WhatsAppDataDto waData = WhatsAppDataDto.builder()
                        .mobileNumber(request.getMobileNumber())
                        .props(props)
                        .config(waConfig)
                        .build();
                whatsappSenderService.send(waData, template, Collections.emptyList());
            } catch (Exception e) {
                log.error("Error sending sample WhatsApp notice for processSno {}", request.getProcessSno(), e);
            }
        }
    }

    private Map<String, Object> buildSampleProps(Map<String, Object> rowData,
            List<ProcessExcelMappingEntity> excelMappings) {
        if (excelMappings == null || excelMappings.isEmpty()) {
            return rowData;
        }
        return rowData.entrySet().stream()
                .filter(entry -> excelMappings.stream()
                        .anyMatch(mapping -> mapping.getExcelFieldName().equals(entry.getKey())))
                .collect(Collectors.toMap(
                        entry -> excelMappings.stream()
                                .filter(mapping -> mapping.getExcelFieldName().equals(entry.getKey()))
                                .map(ProcessExcelMappingEntity::getDbFieldName)
                                .findFirst()
                                .orElse(entry.getKey()),
                        Map.Entry::getValue));
    }

}

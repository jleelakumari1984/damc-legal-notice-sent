package com.damc.legalnotices.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.ProcessExcelMappingDao;
import com.damc.legalnotices.dao.ProcessTemplateDao;
import com.damc.legalnotices.dao.ScheduledNoticeDao;
import com.damc.legalnotices.dao.ScheduledNoticeDetailDao;
import com.damc.legalnotices.dao.ScheduledNoticeItemDao;
import com.damc.legalnotices.dto.ExcelPreviewDto;
import com.damc.legalnotices.dto.ExcelPreviewRowDto;
import com.damc.legalnotices.dto.NoticeValidationFileDto;
import com.damc.legalnotices.dto.NoticeValidationResponseDto;
import com.damc.legalnotices.dto.NoticeValidationRowDto;
import com.damc.legalnotices.entity.LoginDetailEntity;
import com.damc.legalnotices.entity.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.ScheduledNoticeEntity;
import com.damc.legalnotices.entity.ScheduledNoticeItemEntity;
import com.damc.legalnotices.enums.ProcessingStatus;
import com.damc.legalnotices.repository.LoginDetailRepository;
import com.damc.legalnotices.repository.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.repository.ScheduledNoticeItemRepository;
import com.damc.legalnotices.repository.ScheduledNoticeRepository;
import com.damc.legalnotices.service.NoticeService;
import com.damc.legalnotices.util.EntityDaoConverter;
import com.damc.legalnotices.util.ExcelParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final ObjectMapper objectMapper;

    private final MasterProcessTemplateDetailRepository processTemplateRepository;
    private final ScheduledNoticeRepository scheduledNoticeRepository;
    private final ScheduledNoticeItemRepository scheduledNoticeItemRepository;
    private final LoginDetailRepository loginDetailRepository;
    private final ExcelParserUtil excelParserUtil;
    private final EntityDaoConverter entityDaoConverter;
    private final LocationProperties storageProperties;

    @Override
    @Transactional
    public NoticeValidationResponseDto scheduleNotice(Long processSno,
            Boolean sendSms,
            Boolean sendWhatsapp,
            MultipartFile zipFile,
            String loginName) {
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

        MasterProcessTemplateDetailEntity template = processTemplateRepository.findByIdWithExcelMappings(processSno)
                .orElseThrow(() -> new IllegalArgumentException("Invalid notice type"));

        if (template.getExcelMappings() == null || template.getExcelMappings().isEmpty()) {
            throw new IllegalArgumentException("Notice type is not properly configured with Excel mappings");
        }

        LoginDetailEntity user = loginDetailRepository.findByLoginNameAndEnabledTrue(loginName)
                .orElseThrow(() -> new IllegalArgumentException("Logged in user not found"));

        Path savedFilePath;
        Path extractedPath;
        Path directExcelPath = null;
        Path uploadDir = Path.of(storageProperties.getUploadDir()).toAbsolutePath().normalize();
        if (excelParserUtil.isZipFile(originalName)) {
            savedFilePath = excelParserUtil.saveZipFile(zipFile);
            extractedPath = excelParserUtil.extractZip(savedFilePath);
            extractedPath = excelParserUtil.findExcel(extractedPath).getParent();
        } else {
            savedFilePath = excelParserUtil.saveExcelFile(zipFile);
            extractedPath = savedFilePath.getParent();
            directExcelPath = savedFilePath;
        }
        String removedUploadPath = savedFilePath.toAbsolutePath().toString().replace(uploadDir.toString(), "");
        String removedExtractedPath = extractedPath.toAbsolutePath().toString().replace(uploadDir.toString(), "");
        ScheduledNoticeEntity scheduledNotice = new ScheduledNoticeEntity();
        scheduledNotice.setProcessSno(processSno);
        scheduledNotice.setOriginalFileName(zipFile.getOriginalFilename());
        scheduledNotice.setZipFilePath(removedUploadPath);
        scheduledNotice.setExtractedFolderPath(removedExtractedPath);
        scheduledNotice.setSendSms(Boolean.TRUE.equals(sendSms));
        scheduledNotice.setSendWhatsapp(Boolean.TRUE.equals(sendWhatsapp));
        scheduledNotice.setCreatedBy(user.getId());
        scheduledNotice.setCreatedAt(LocalDateTime.now());
        scheduledNotice.setStatus(ProcessingStatus.EXCELPROCESSING);
        scheduledNotice = scheduledNoticeRepository.save(scheduledNotice);
        log.info("ScheduledNotice created with id: {}", scheduledNotice.getId());
        ProcessingStatus excelProcess = ProcessingStatus.EXCELUPLOADED;
        try {
            NoticeValidationFileDto validationRows = storeExcelData(extractedPath, directExcelPath, template,
                    scheduledNotice);
            excelProcess = ProcessingStatus.EXCELCOMPLETED;
            validationRows.getRows().sort(Comparator.comparing(NoticeValidationRowDto::getAgreementNumber));

            return NoticeValidationResponseDto.builder()
                    .scheduleId(scheduledNotice.getId())
                    .originalZipFile(scheduledNotice.getOriginalFileName())
                    .extractedFolder(scheduledNotice.getExtractedFolderPath())
                    .status(scheduledNotice.getStatus().name())
                    .fileData(validationRows)
                    .build();
        } finally {
            scheduledNotice.setStatus(excelProcess);
            scheduledNotice = scheduledNoticeRepository.save(scheduledNotice);
        }
    }

    @Override
    public NoticeValidationFileDto storeExcelData(Path extractedPath, MasterProcessTemplateDetailEntity template,
            ScheduledNoticeEntity scheduledNotice) {
        return storeExcelData(extractedPath, null, template, scheduledNotice);
    }

    private NoticeValidationFileDto storeExcelData(Path extractedPath, Path directExcelPath,
            MasterProcessTemplateDetailEntity template, ScheduledNoticeEntity scheduledNotice) {
        List<ProcessExcelMappingDao> keyColumns = template.getExcelMappings().stream()
                .filter(mapping -> mapping.getIsKey() == 1)
                .map(entityDaoConverter::toProcessExcelMappingDao)
                .toList();

        List<ProcessExcelMappingDao> attachmentColumns = template.getExcelMappings().stream()
                .filter(mapping -> mapping.getIsAttachment() == 1)
                .map(entityDaoConverter::toProcessExcelMappingDao)
                .toList();
        Path excelPath = directExcelPath != null ? directExcelPath : excelParserUtil.findExcel(extractedPath);
        ExcelPreviewDto excelPreview;
        try {
            excelPreview = excelParserUtil.parseAsPreview(excelPath);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to parse excel from ZIP: " + ex.getMessage());
        }
        if (excelPreview.getRows().isEmpty()) {
            throw new IllegalArgumentException("Excel has no valid agreement rows");
        }
        List<NoticeValidationRowDto> validationRows = new ArrayList<>();
        for (ExcelPreviewRowDto row : excelPreview.getRows()) {
            String excelData;
            try {
                excelData = objectMapper.writeValueAsString(row.getData());
            } catch (Exception ex) {
                excelData = row.toString();
            }

            ScheduledNoticeItemEntity item = new ScheduledNoticeItemEntity();
            item.setScheduledNotice(scheduledNotice);
            item.setAgreementNumber(row.getKeyColumnData(keyColumns, null));
            item.setAttachements(row.getKeyColumnData(attachmentColumns, ";"));
            item.setExcelData(excelData);
            item.setStatus(ProcessingStatus.PENDING);
            scheduledNoticeItemRepository.save(item);

            validationRows.add(NoticeValidationRowDto.builder()
                    .agreementNumber(row.getKeyColumnData(keyColumns, null))
                    .excelData(excelData)
                    .build());
        }
        return NoticeValidationFileDto.builder()
                .columnNames(excelPreview.getColumnNames())
                .rows(validationRows)
                .build();
    }

    @Override
    public List<ScheduledNoticeDao> getScheduledNotices() {
        return scheduledNoticeRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(entityDaoConverter::toScheduledNoticeDao)
                .toList();
    }

    @Override
    public ScheduledNoticeDetailDao getScheduledNoticeDetail(Long id) {
        ScheduledNoticeEntity notice = scheduledNoticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + id));
        List<ScheduledNoticeItemDao> items = scheduledNoticeItemRepository.findByScheduledNoticeId(id).stream()
                .map(entityDaoConverter::toScheduledNoticeItemDao)
                .toList();
        return ScheduledNoticeDetailDao.builder()
                .id(notice.getId())
                .processSno(notice.getProcessSno())
                .originalFileName(notice.getOriginalFileName())
                .sendSms(notice.getSendSms())
                .sendWhatsapp(notice.getSendWhatsapp())
                .status(notice.getStatus().name())
                .createdAt(notice.getCreatedAt())
                .processedAt(notice.getProcessedAt())
                .failureReason(notice.getFailureReason())
                .items(items)
                .build();
    }

    @Override
    public List<ProcessTemplateDao> getNoticeTypes() {
        return processTemplateRepository.findAllWithExcelMappings().stream()
                .map(process -> ProcessTemplateDao.builder()
                        .id(process.getId())
                        .name(process.getStepName())
                        .excelMap(process.getExcelMappings() == null || process.getExcelMappings().isEmpty() ? null
                                : process.getExcelMappings().stream()
                                        .map(entityDaoConverter::toProcessExcelMappingDao)
                                        .toList())
                        .build())
                .toList();
    }

}

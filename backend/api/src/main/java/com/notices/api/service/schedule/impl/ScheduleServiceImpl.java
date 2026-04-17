package com.notices.api.service.schedule.impl;

import java.util.stream.Collectors;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.notices.api.dto.notice.NoticeScheduleRequestDto;
import com.notices.api.service.schedule.ScheduleService;
import com.notices.domain.config.LocationProperties;
import com.notices.domain.dao.notice.NoticeValidationDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dao.user.UserSmsCredentialDao;
import com.notices.domain.dao.user.UserWhatsAppCredentialDao;
import com.notices.domain.dto.notice.NoticeValidationFileDto;
import com.notices.domain.dto.notice.NoticeValidationRowDto;
import com.notices.domain.dto.notice.SendSampleNoticeDto;
import com.notices.domain.dto.notification.SmsDataDto;
import com.notices.domain.dto.notification.WhatsAppDataDto;
import com.notices.domain.entity.master.MasterProcessExcelMappingEntity;
import com.notices.domain.entity.master.MasterProcessSmsConfigDetailEntity;
import com.notices.domain.entity.master.MasterProcessTemplateDetailEntity;
import com.notices.domain.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.notices.domain.entity.schedule.ScheduledNoticeEntity;
import com.notices.domain.enums.NoticeScheduleStatus;
import com.notices.domain.enums.TemplateApproveStatus;
import com.notices.domain.repository.master.MasterProcessSmsConfigDetailRepository;
import com.notices.domain.repository.master.MasterProcessTemplateDetailRepository;
import com.notices.domain.repository.master.MasterProcessWhatsappConfigDetailRepository;
import com.notices.domain.repository.schedule.ScheduledNoticeRepository;
import com.notices.domain.service.notification.SmsSenderService;
import com.notices.domain.service.notification.WhatsappSenderService;
import com.notices.domain.service.schedule.SaveScheduleService;
import com.notices.domain.util.EndpointUtil;
import com.notices.domain.util.ExcelParserUtil;
import com.notices.domain.util.SaveExcelUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final MasterProcessTemplateDetailRepository noticeTemplateRepository;
    private final MasterProcessSmsConfigDetailRepository smsConfigRepository;
    private final MasterProcessWhatsappConfigDetailRepository whatsappConfigRepository;

    private final SmsSenderService smsSenderService;
    private final WhatsappSenderService whatsappSenderService;

    private final EndpointUtil endpointUtil;

    private final ScheduledNoticeRepository scheduledNoticeRepository;
    private final LocationProperties storageProperties;
    private final ExcelParserUtil excelParserUtil;
    private final SaveExcelUtil saveExcelUtil;

    private final SaveScheduleService saveScheduleService;

    @Override
    @Transactional
    public NoticeValidationDao saveScheduleNotice(LoginUserDao sessionUser, NoticeScheduleRequestDto request) {
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
            savedFilePath = saveExcelUtil.saveZipFile(sessionUser, zipFile);
            extractedPath = excelParserUtil.extractZip(savedFilePath);
            extractedPath = excelParserUtil.findExcel(extractedPath).getParent();
        } else {
            savedFilePath = saveExcelUtil.saveExcelFile(sessionUser, zipFile);
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
            NoticeValidationFileDto validationRows = saveScheduleService.storeExcelData(extractedPath, directExcelPath,
                    template,
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

}

package com.damc.legalnotices.util;

import com.damc.legalnotices.dao.LoginUserDao;
import com.damc.legalnotices.dao.ProcessExcelMappingDao;
import com.damc.legalnotices.dao.ScheduledNoticeDao;
import com.damc.legalnotices.dao.ScheduledNoticeItemDao;
import com.damc.legalnotices.entity.LoginDetailEntity;
import com.damc.legalnotices.entity.ProcessExcelMappingEntity;
import com.damc.legalnotices.entity.ScheduledNoticeEntity;
import com.damc.legalnotices.entity.ScheduledNoticeItemEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntityDaoConverter {

    private final ObjectMapper objectMapper;

    public LoginUserDao toLoginUserDao(LoginDetailEntity loginDetail) {
        return LoginUserDao.builder()
                .id(loginDetail.getId())
                .displayName(loginDetail.getDisplayName())
                .loginName(loginDetail.getLoginName())
                .userEmail(loginDetail.getUserEmail())
                .mobileSms(loginDetail.getUserMobileSms())
                .mobileWhatsapp(loginDetail.getUserMobileWhatsapp())
                .accessLevel(loginDetail.getAccessLevel())
                .build();
    }

    public ScheduledNoticeDao toScheduledNoticeDao(ScheduledNoticeEntity notice) {
        return ScheduledNoticeDao.builder()
                .id(notice.getId())
                .processSno(notice.getProcessSno())
                .originalFileName(notice.getOriginalFileName())
                .zipFilePath(notice.getZipFilePath())
                .extractedFolderPath(notice.getExtractedFolderPath())
                .sendSms(notice.getSendSms())
                .sendWhatsapp(notice.getSendWhatsapp())
                .status(notice.getStatus().name())
                .createdAt(notice.getCreatedAt())
                .build();
    }

    public ProcessExcelMappingDao toProcessExcelMappingDao(ProcessExcelMappingEntity processexcelmapping1) {
        return ProcessExcelMappingDao.builder()
                .id(processexcelmapping1.getId())
                .excelFieldName(processexcelmapping1.getExcelFieldName())
                .dbFieldName(processexcelmapping1.getDbFieldName())
                .isMandatory(processexcelmapping1.getIsMandatory())
                .isAttachment(processexcelmapping1.getIsAttachment())
                .isKey(processexcelmapping1.getIsKey())
                .build();
    }

    public ScheduledNoticeItemDao toScheduledNoticeItemDao(ScheduledNoticeItemEntity item) {
        Map<String, Object> excelDataMap = null;
        if (item.getExcelData() != null && !item.getExcelData().isBlank()) {
            try {
                excelDataMap = objectMapper.readValue(item.getExcelData(), new TypeReference<>() {});
            } catch (Exception e) {
                log.warn("Failed to parse excelData for item id={}", item.getId(), e);
            }
        }
        return ScheduledNoticeItemDao.builder()
                .id(item.getId())
                .agreementNumber(item.getAgreementNumber())
                .excelData(excelDataMap)
                .status(item.getStatus().name())
                .failureReason(item.getFailureReason())
                .processedAt(item.getProcessedAt())
                .attachements(item.getAttachements())
                .build();
    }
}

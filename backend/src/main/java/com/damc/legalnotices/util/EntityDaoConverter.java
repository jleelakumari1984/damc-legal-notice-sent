package com.damc.legalnotices.util;

import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.notice.NoticeExcelMappingDao;
import com.damc.legalnotices.dao.notice.ProcessTemplateReportDao;
import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.dao.schedule.ScheduledNoticeDao;
import com.damc.legalnotices.entity.user.LoginDetailEntity;
import com.damc.legalnotices.entity.view.ProcessConfigReportViewEntity;
import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity; 
import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.damc.legalnotices.entity.schedule.ScheduledNoticeEntity;
import com.damc.legalnotices.entity.excel.ProcessExcelMappingEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntityDaoConverter {
    private final LocationProperties appConfig;

    public LoginUserDao toLoginUserDao(LoginDetailEntity loginDetail) {
        return LoginUserDao.builder()
                .id(loginDetail.getId())
                .displayName(loginDetail.getDisplayName())
                .loginName(loginDetail.getLoginName())
                .password(loginDetail.getPassword())
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
                .processName(notice.getProcess() != null ? notice.getProcess().getStepName() : null)
                .originalFileName(notice.getOriginalFileName())
                .zipFilePath(notice.getZipFilePath())
                .extractedFolderPath(notice.getExtractedFolderPath())
                .sendSms(notice.getSendSms())
                .sendWhatsapp(notice.getSendWhatsapp())
                .status(notice.getStatus().name())
                .createdAt(notice.getCreatedAt())
                .build();
    }

    public ProcessTemplateReportDao toProcessTemplateReportDao(
            ProcessConfigReportViewEntity processConfigReportViewEntity) {
        return ProcessTemplateReportDao.builder()
                .id(processConfigReportViewEntity.getSno())
                .name(processConfigReportViewEntity.getStepName())
                .createdAt(processConfigReportViewEntity.getCreatedAt())
                .excelMapCount(processConfigReportViewEntity.getExcelMapCount() == null ? 0 : processConfigReportViewEntity.getExcelMapCount())
                .smsMapCount(processConfigReportViewEntity.getSmsMapCount() == null ? 0 : processConfigReportViewEntity.getSmsMapCount())
                .whatsappMapCount(processConfigReportViewEntity.getWhatsappMapCount() == null ? 0 : processConfigReportViewEntity.getWhatsappMapCount())
                .mailMapCount(processConfigReportViewEntity.getMailMapCount() == null ? 0 : processConfigReportViewEntity.getMailMapCount())
                .build();
    }

    public NoticeExcelMappingDao toProcessExcelMappingDao(ProcessExcelMappingEntity processexcelmapping1) {
        return NoticeExcelMappingDao.builder()
                .id(processexcelmapping1.getId())
                .excelFieldName(processexcelmapping1.getExcelFieldName())
                .dbFieldName(processexcelmapping1.getDbFieldName())
                .isMandatory(processexcelmapping1.getIsMandatory())
                .isAttachment(processexcelmapping1.getIsAttachment())
                .isKey(processexcelmapping1.getIsKey())
                .build();
    }

    public SmsTemplateDao toSmsTemplateDao(MasterProcessSmsConfigDetailEntity e) {
        String templateContent = readTemplateContent(e.getTemplatePath());
        return SmsTemplateDao.builder()
                .id(e.getId())
                .processId(e.getProcess() != null ? e.getProcess().getId() : null)
                .hearingStageId(e.getHearingStage() != null ? e.getHearingStage().getId() : null)
                .hearingStageTitle(e.getHearingStage() != null ? e.getHearingStage().getStageTitle() : null)
                .sentLevel(e.getSentLevel())
                .peid(e.getPeid())
                .senderId(e.getSenderId())
                .routeId(e.getRouteId())
                .templateText(templateContent)
                .templateId(e.getTemplateId())
                .channel(e.getChannel())
                .dcs(e.getDcs())
                .flashSms(e.getFlashSms())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public WhatsAppTemplateDao toWhatsAppTemplateDao(MasterProcessWhatsappConfigDetailEntity e) {
        String templateContent = readTemplateContent(e.getTemplatePath());
        return WhatsAppTemplateDao.builder()
                .id(e.getId())
                .processId(e.getProcess() != null ? e.getProcess().getId() : null)
                .hearingStageId(e.getHearingStage() != null ? e.getHearingStage().getId() : null)
                .hearingStageTitle(e.getHearingStage() != null ? e.getHearingStage().getStageTitle() : null)
                .sentLevel(e.getSentLevel())
                .templateName(e.getTemplateName())
                .templatePath(e.getTemplatePath())
                .templateContent(templateContent)
                .templateLang(e.getTemplateLang())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private String readTemplateContent(String relativePath) {
        if (relativePath == null || relativePath.isBlank())
            return null;
        try {
            Path path = Path.of(appConfig.getTemplateLocation(), relativePath);
            return java.nio.file.Files.readString(path);
        } catch (Exception ex) {
            log.warn("Could not read template file '{}': {}", relativePath, ex.getMessage());
            return null;
        }
    }

}

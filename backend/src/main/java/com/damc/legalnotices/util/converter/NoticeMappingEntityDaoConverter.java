package com.damc.legalnotices.util.converter;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.notice.SmsPendingTemplateDao;
import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.notice.SmsUserTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppPendingTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppUserTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeMappingEntityDaoConverter {
    private final LocationProperties appConfig;

    public SmsUserTemplateDao toSmsUserTemplateDao(MasterProcessSmsConfigDetailEntity e) {
        String templateContent = readTemplateContent(e.getUserTemplatePath());
        return SmsUserTemplateDao.builder()
                .id(e.getId())
                .processId(e.getProcess() != null ? e.getProcess().getId() : null)
                .userTemplateContent(templateContent)
                .status(e.getStatus())
                .approveStatus(e.getApproveStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public SmsTemplateDao toSmsTemplateDao(MasterProcessSmsConfigDetailEntity e) {
        String templateContent = readTemplateContent(e.getTemplatePath());
        String userTemplateContent = readTemplateContent(e.getUserTemplatePath());

        return SmsTemplateDao.builder()
                .id(e.getId())
                .processId(e.getProcess() != null ? e.getProcess().getId() : null)
                .peid(e.getPeid())
                .senderId(e.getSenderId())
                .routeId(e.getRouteId())
                .templateContent(templateContent)
                .userTemplateContent(userTemplateContent)
                .templateId(e.getTemplateId())
                .channel(e.getChannel())
                .dcs(e.getDcs())
                .flashSms(e.getFlashSms())
                .status(e.getStatus())
                .approveStatus(e.getApproveStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public SmsPendingTemplateDao toSmsPendingTemplateDao(
            MasterProcessSmsConfigDetailEntity e) {
        String userTemplateContent = readTemplateContent(e.getUserTemplatePath());
        return SmsPendingTemplateDao.builder()
                .id(e.getId())
                .processName(e.getProcess() != null ? e.getProcess().getName() : null)
                .userName(e.getCreatedUser() != null ? e.getCreatedUser().getDisplayName() : null)
                .userTemplateContent(userTemplateContent)
                .build();
    }

    public WhatsAppUserTemplateDao toWhatsAppUserTemplateDao(MasterProcessWhatsappConfigDetailEntity e) {
        String templateContent = readTemplateContent(e.getUserTemplatePath());
        return WhatsAppUserTemplateDao.builder()
                .id(e.getId())
                .processId(e.getProcess() != null ? e.getProcess().getId() : null)
                .userTemplateContent(templateContent)
                .status(e.getStatus())
                .approveStatus(e.getApproveStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public WhatsAppTemplateDao toWhatsAppTemplateDao(MasterProcessWhatsappConfigDetailEntity e) {
        String templateContent = readTemplateContent(e.getTemplatePath());
        String userTemplateContent = readTemplateContent(e.getUserTemplatePath());
        return WhatsAppTemplateDao.builder()
                .id(e.getId())
                .processId(e.getProcess() != null ? e.getProcess().getId() : null)
                .templateName(e.getTemplateName())
                .userTemplateContent(userTemplateContent)
                .templateContent(templateContent)
                .templateLang(e.getTemplateLang())
                .status(e.getStatus())
                .approveStatus(e.getApproveStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public WhatsAppPendingTemplateDao toWhatsAppPendingTemplateDao(
            MasterProcessWhatsappConfigDetailEntity e) {
        String userTemplateContent = readTemplateContent(e.getUserTemplatePath());
        return WhatsAppPendingTemplateDao.builder()
                .id(e.getId())
                .processName(e.getProcess() != null ? e.getProcess().getName() : null)
                .userName(e.getCreatedUser() != null ? e.getCreatedUser().getDisplayName() : null)
                .userTemplateContent(userTemplateContent)
                .build();
    }

    private String readTemplateContent(String relativePath) {
        if (relativePath == null || relativePath.isBlank())
            return null;
        // Strip leading slashes so Path.of() does not treat it as absolute
        String cleanedPath = relativePath.replaceAll("^[/\\\\]+", "");
        try {
            // Fallback: try appending .html if extension is missing
            Path htmlPath = Path.of(appConfig.getTemplateLocation(), cleanedPath + ".html");
            if (java.nio.file.Files.exists(htmlPath)) {
                return java.nio.file.Files.readString(htmlPath);
            }
            log.warn("Could not read template file '{}': file not found at {} or {}", relativePath, htmlPath);
            return null;
        } catch (Exception ex) {
            log.warn("Could not read template file '{}': {}", relativePath, ex.getMessage());
            return null;
        }
    }

    public String getUserNoticePath(MasterProcessTemplateDetailEntity process, LoginUserDao sessionUser) {
        return "notices/" + process.getName().replaceAll("\\s+", "_") + "/" + sessionUser.getId();
    }

}

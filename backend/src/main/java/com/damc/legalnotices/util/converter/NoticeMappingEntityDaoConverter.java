package com.damc.legalnotices.util.converter;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.notice.SmsUserTemplateDao;
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
        String templateContent = readTemplateContent(e.getTemplatePath());
        return SmsUserTemplateDao.builder()
                .id(e.getId())
                .processId(e.getProcess() != null ? e.getProcess().getId() : null)
                .userTemplateText(templateContent)
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public SmsTemplateDao toSmsTemplateDao(MasterProcessSmsConfigDetailEntity e) {
        String templateContent = readTemplateContent(e.getTemplatePath());

        return SmsTemplateDao.builder()
                .id(e.getId())
                .processId(e.getProcess() != null ? e.getProcess().getId() : null)
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

    public WhatsAppUserTemplateDao toWhatsAppUserTemplateDao(MasterProcessWhatsappConfigDetailEntity e) {
        String templateContent = readTemplateContent(e.getUserTemplatePath());
        return WhatsAppUserTemplateDao.builder()
                .id(e.getId())
                .processId(e.getProcess() != null ? e.getProcess().getId() : null)
                .userTemplateContent(templateContent)
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public WhatsAppTemplateDao toWhatsAppTemplateDao(MasterProcessWhatsappConfigDetailEntity e) {
        String templateContent = readTemplateContent(e.getTemplatePath());
        return WhatsAppTemplateDao.builder()
                .id(e.getId())
                .processId(e.getProcess() != null ? e.getProcess().getId() : null)
                .templateName(e.getTemplateName())
                .userTemplateContent(templateContent)
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

    public String getUserNoticePath(MasterProcessTemplateDetailEntity process, LoginUserDao sessionUser) {
        return "notices/" + process.getName().replaceAll("\\s+", "_") + "/" + sessionUser.getId();
    }

}

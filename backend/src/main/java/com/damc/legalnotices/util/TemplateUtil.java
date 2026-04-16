package com.damc.legalnotices.util;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notification.SmsApiRequestDto;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateUtil {
    private final LocationProperties appConfig;
    private final ObjectMapper objectMapper;

    public String readTemplateContent(String relativePath) {
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

    public String getUserNoticePath(MasterProcessTemplateDetailEntity notice, LoginUserDao sessionUser,
            String noticeType) {
        long timestamp = System.currentTimeMillis();

        return "notices/" + sessionUser.getId() + "/" + notice.getName().replaceAll("\\s+", "_") + "/" + noticeType
                + "/" + timestamp;
    }

    public String getWhatsAppLogMessage(String message) {
        SmsApiRequestDto smsData = null;
        if (message != null && !message.isBlank()) {
            try {
                objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
                smsData = objectMapper.readValue(message, SmsApiRequestDto.class);
            } catch (Exception ex) {
                log.warn("Failed to parse requestData for sms error log message='{}'", message, ex);
            }
        }
        return smsData != null ? smsData.getText() : "";
    }

    public String getSmsLogMessage(String message) {
        String text = "";
        SmsApiRequestDto smsData = null;
        if (message != null && !message.isBlank()) {
            try {
                objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
                smsData = objectMapper.readValue(message, SmsApiRequestDto.class);
                text = smsData.getText();
            } catch (Exception ex) {
                log.warn("Failed to parse requestData for sms log message='{}'", message, ex);
            }
        }
        return text;
    }
}

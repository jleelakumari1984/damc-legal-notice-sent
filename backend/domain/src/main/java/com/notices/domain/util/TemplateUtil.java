package com.notices.domain.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.notices.domain.config.LocationProperties;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.notice.TemplateMessageCountDto;
import com.notices.domain.dto.notification.SmsApiRequestDto;
import com.notices.domain.dto.notification.WhatsAppApiRequestDto.WhatsAppComponentDto;
import com.notices.domain.dto.notification.WhatsAppApiRequestDto.WhatsAppParameterDto;
import com.notices.domain.entity.master.MasterProcessTemplateDetailEntity;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateUtil {
    private static final Pattern EXCEL_FIELD_PATTERN = Pattern.compile("\\{\\{([^{}]+)\\}\\}");
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
        WhatsAppComponentDto smsData = null;
        if (message != null && !message.isBlank()) {
            try {
                objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
                smsData = objectMapper.readValue(message, WhatsAppComponentDto.class);
            } catch (Exception ex) {
                log.warn("Failed to parse requestData for sms error log message='{}'", message, ex);
            }
        }
        return smsData != null ? "" : "";
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

    public int calculateNoOfSmsMessages(String message) {
        if (message == null || message.isEmpty()) {
            return 0;
        }
        int length = message.length();
        if (length <= 160) {
            return 1;
        }
        return (int) Math.ceil((double) length / 153);
    }

    public int calculateNoOfWhatsAppMessages(String message) {
        if (message == null || message.isEmpty()) {
            return 0;
        }
        int length = message.length();
        if (length <= 300) {
            return 1;
        }
        return (int) Math.ceil((double) length / 297);
    }

    public String replaceExcelFields(String userTemplateContent) {
        if (userTemplateContent == null || userTemplateContent.isBlank()) {
            return userTemplateContent;
        }
        return EXCEL_FIELD_PATTERN.matcher(userTemplateContent).replaceAll("");
    }

    public List<String> findExcelFields(String userTemplateContent) {
        List<String> fields = new ArrayList<>();
        if (userTemplateContent == null || userTemplateContent.isBlank()) {
            return fields;
        }
        try {
            Matcher matcher = EXCEL_FIELD_PATTERN.matcher(userTemplateContent);
            while (matcher.find()) {
                fields.add(matcher.group(1).trim());

            }
        } catch (Exception e) {
            log.warn("Failed to find excel fields in template content '{}'", userTemplateContent, e);
        }
        log.debug("Found {} excel field(s) in template: {}", fields.size(), fields);
        return fields;
    }

    public TemplateMessageCountDto findNoOfSmsMessages(String userTemplateContent) {
        TemplateMessageCountDto msgCountDto = TemplateMessageCountDto.builder().build();
        if (userTemplateContent == null || userTemplateContent.isBlank()) {
            return msgCountDto;
        }
        List<String> fields = findExcelFields(userTemplateContent);
        String replacedExcelContent = replaceExcelFields(userTemplateContent);
        msgCountDto.setFields(fields);
        msgCountDto.setMessageLengths(replacedExcelContent.length());
        msgCountDto.setNoOfMessages(calculateNoOfSmsMessages(replacedExcelContent));
        log.debug("Found {} SMS message(s) in template", msgCountDto.getNoOfMessages());
        return msgCountDto;
    }

    public TemplateMessageCountDto findNoOfWhatsAppMessages(String userTemplateContent) {
        TemplateMessageCountDto msgCountDto = TemplateMessageCountDto.builder().build();
        if (userTemplateContent == null || userTemplateContent.isBlank()) {
            return msgCountDto;
        }
        List<String> fields = findExcelFields(userTemplateContent);
        String replacedExcelContent = replaceExcelFields(userTemplateContent);
        msgCountDto.setFields(fields);
        msgCountDto.setMessageLengths(replacedExcelContent.length());
        msgCountDto.setNoOfMessages(calculateNoOfWhatsAppMessages(replacedExcelContent));
        log.debug("Found {} WhatsApp message(s) in template", msgCountDto.getNoOfMessages());
        return msgCountDto;
    }

    public String getWhatsAppApprovedTemplateMessage(List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return "";
        }
        if (fields == null || fields.isEmpty()) {
            fields = new ArrayList<>();
        }
        // [[${customer_name}]]
        WhatsAppComponentDto component = WhatsAppComponentDto.builder()
                .type("body")
                .parameters(fields.stream()
                        .map((String field) -> WhatsAppParameterDto.builder()
                                .type("text")
                                .text("[[${" + field + "}]]")
                                .build())
                        .toList())
                .build();

        try {
            objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(List.of(component));
        } catch (Exception e) {
            log.error("Failed to convert WhatsAppComponentDto to JSON", e);
            return "";
        }
    }

    public String getSmsAppApprovedTemplateMessage(String message, List<String> fields) {
        if (message == null) {
            return "";
        }
        try {
            if (fields == null || fields.isEmpty()) {
                return message;
            }
            // Escape $ in the message to prevent issues with replacement
            message = message.replace("$", "\\$");
            return EXCEL_FIELD_PATTERN.matcher(message)
                    .replaceAll(mr -> Matcher.quoteReplacement("[[${" + mr.group(1).trim() + "}]]"));
        } catch (Exception e) {
            log.error("Failed to replace fields in SMS template message {} {}", message, e);
            return message;
        }
    }

}

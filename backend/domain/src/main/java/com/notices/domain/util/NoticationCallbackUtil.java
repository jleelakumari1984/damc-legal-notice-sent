package com.notices.domain.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.notices.domain.dao.notification.StatusReportSmsDao;
import com.notices.domain.dao.notification.StatusReportWhatsappDao;
import com.notices.domain.entity.notification.StatusReportSmsEntity;
import com.notices.domain.entity.notification.StatusReportWhatsappEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticationCallbackUtil {
    private final ObjectMapper objectMapper;

    // Common delivtime formats used by SMS gateways
    private static final List<DateTimeFormatter> DELIVTIME_FORMATS = List.of(
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss"),
            DateTimeFormatter.ofPattern("yyMMddHHmmss"),
            DateTimeFormatter.ofPattern("yyMMddHHmm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

    public Instant parseDelivTime(String delivtime) {
        if (!StringUtils.hasText(delivtime))
            return Instant.now();
        for (DateTimeFormatter fmt : DELIVTIME_FORMATS) {
            try {
                return LocalDateTime.parse(delivtime, fmt)
                        .atZone(ZoneId.systemDefault())
                        .toInstant();
            } catch (Exception ignored) {
            }
        }
        log.warn("SMS DLR: could not parse delivtime '{}', using current time", delivtime);
        return Instant.now();
    }

    public Instant parseEpochTimestamp(String timestampStr) {
        if (!StringUtils.hasText(timestampStr))
            return Instant.now();
        try {
            return Instant.ofEpochSecond(Long.parseLong(timestampStr));
        } catch (NumberFormatException ex) {
            log.warn("WhatsApp callback: could not parse timestamp '{}', using current time", timestampStr);
            return Instant.now();
        }
    }

    public String parseSmsAckId(String response) {
        if (!StringUtils.hasText(response))
            return null;
        try {
            JsonNode root = objectMapper.readTree(response);
            String ackId = root.path("data").path("ack_id").asText(null);
            return StringUtils.hasText(ackId) ? ackId : null;
        } catch (Exception ex) {
            log.warn("Could not parse SMS ack_id from response: {}", ex.getMessage());
            return null;
        }
    }

    public String parseWhatsAppAckId(String response) {
        if (!StringUtils.hasText(response))
            return null;
        try {
            JsonNode root = objectMapper.readTree(response);
            String wamid = root.path("message_wamid").asText(null);
            if (StringUtils.hasText(wamid) && !"null".equals(wamid))
                return wamid;
            long messageId = root.path("message_id").asLong(0);
            return messageId > 0 ? String.valueOf(messageId) : null;
        } catch (Exception ex) {
            log.warn("Could not parse WhatsApp ack_id from response: {}", ex.getMessage());
            return null;
        }
    }

    public StatusReportSmsDao toDao(StatusReportSmsEntity entity) {
        if (entity == null) return null;
        return StatusReportSmsDao.builder()
                .id(entity.getId())
                .requestParams(entity.getRequestParams())
                .requestBody(entity.getRequestBody())
                .status(entity.getStatus())
                .createdDate(entity.getCreatedDate())
                .noticeDate(entity.getProcessDate())
                .completeDate(entity.getCompleteDate())
                .description(entity.getDescription())
                .build();
    }

    public StatusReportWhatsappDao toDao(StatusReportWhatsappEntity entity) {
        if (entity == null) return null;
        return StatusReportWhatsappDao.builder()
                .id(entity.getId())
                .requestParams(entity.getRequestParams())
                .requestBody(entity.getRequestBody())
                .status(entity.getStatus())
                .createdDate(entity.getCreatedDate())
                .noticeDate(entity.getProcessDate())
                .completeDate(entity.getCompleteDate())
                .description(entity.getDescription())
                .build();
    }

}

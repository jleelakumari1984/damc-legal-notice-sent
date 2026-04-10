package com.damc.legalnotices.service.impl;

import com.damc.legalnotices.repository.SendLoanSmsDetailRepository;
import com.damc.legalnotices.repository.SendLoanWhatsappDetailRepository;
import com.damc.legalnotices.repository.SendNonLoanSmsDetailRepository;
import com.damc.legalnotices.repository.SendNonLoanWhatsappDetailRepository;
import com.damc.legalnotices.service.NotificationCallbackService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCallbackServiceImpl implements NotificationCallbackService {

    private final SendLoanSmsDetailRepository loanSmsRepo;
    private final SendNonLoanSmsDetailRepository nonLoanSmsRepo;
    private final SendLoanWhatsappDetailRepository loanWhatsappRepo;
    private final SendNonLoanWhatsappDetailRepository nonLoanWhatsappRepo;
    private final ObjectMapper objectMapper;

    // Common delivtime formats used by SMS gateways
    private static final List<DateTimeFormatter> DELIVTIME_FORMATS = List.of(
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss"),
            DateTimeFormatter.ofPattern("yyMMddHHmmss"),
            DateTimeFormatter.ofPattern("yyMMddHHmm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    );

    @Override
    public void processSmsDeliveryReport(String requestBody) {
        if (!StringUtils.hasText(requestBody)) {
            log.warn("SMS DLR: empty request body");
            return;
        }
        try {
            JsonNode root = objectMapper.readTree(requestBody);
            String messageId = root.path("messageid").asText(null);
            String delivtime = root.path("delivtime").asText(null);

            if (!StringUtils.hasText(messageId)) {
                log.warn("SMS DLR: messageid is missing in payload: {}", requestBody);
                return;
            }

            Instant receivedAt = parseDelivTime(delivtime);

            loanSmsRepo.findByAckId(messageId).ifPresent(entity -> {
                entity.setReceivedStatus("RECEIVED");
                entity.setReceivedAt(receivedAt);
                loanSmsRepo.save(entity);
                log.info("SMS DLR: updated loan SMS record id={} ackId={}", entity.getId(), messageId);
            });

            nonLoanSmsRepo.findByAckId(messageId).ifPresent(entity -> {
                entity.setReceivedStatus("RECEIVED");
                entity.setReceivedAt(receivedAt);
                nonLoanSmsRepo.save(entity);
                log.info("SMS DLR: updated non-loan SMS record id={} ackId={}", entity.getId(), messageId);
            });

        } catch (Exception ex) {
            log.error("SMS DLR: error processing delivery report: {}", ex.getMessage(), ex);
        }
    }

    @Override
    public void processWhatsAppStatusCallback(String requestBody) {
        if (!StringUtils.hasText(requestBody)) {
            log.warn("WhatsApp callback: empty request body");
            return;
        }
        try {
            JsonNode root = objectMapper.readTree(requestBody);
            JsonNode entries = root.path("entry");
            if (!entries.isArray()) {
                log.warn("WhatsApp callback: no entry array in payload");
                return;
            }
            for (JsonNode entry : entries) {
                for (JsonNode change : entry.path("changes")) {
                    JsonNode statuses = change.path("value").path("statuses");
                    if (!statuses.isArray()) continue;
                    for (JsonNode status : statuses) {
                        String wamid = status.path("id").asText(null);
                        String statusVal = status.path("status").asText(null);
                        String timestampStr = status.path("timestamp").asText(null);

                        if (!StringUtils.hasText(wamid)) continue;

                        Instant receivedAt = parseEpochTimestamp(timestampStr);

                        loanWhatsappRepo.findByAckId(wamid).ifPresent(entity -> {
                            entity.setReceivedStatus(statusVal);
                            entity.setReceivedAt(receivedAt);
                            loanWhatsappRepo.save(entity);
                            log.info("WhatsApp callback: updated loan WhatsApp record id={} wamid={} status={}",
                                    entity.getId(), wamid, statusVal);
                        });

                        nonLoanWhatsappRepo.findByAckId(wamid).ifPresent(entity -> {
                            entity.setReceivedStatus(statusVal);
                            entity.setReceivedAt(receivedAt);
                            nonLoanWhatsappRepo.save(entity);
                            log.info("WhatsApp callback: updated non-loan WhatsApp record id={} wamid={} status={}",
                                    entity.getId(), wamid, statusVal);
                        });
                    }
                }
            }
        } catch (Exception ex) {
            log.error("WhatsApp callback: error processing status update: {}", ex.getMessage(), ex);
        }
    }

    private Instant parseDelivTime(String delivtime) {
        if (!StringUtils.hasText(delivtime)) return Instant.now();
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

    private Instant parseEpochTimestamp(String timestampStr) {
        if (!StringUtils.hasText(timestampStr)) return Instant.now();
        try {
            return Instant.ofEpochSecond(Long.parseLong(timestampStr));
        } catch (NumberFormatException ex) {
            log.warn("WhatsApp callback: could not parse timestamp '{}', using current time", timestampStr);
            return Instant.now();
        }
    }

    @Override
    public int reParseSmsMissingAckIds() {
        int count = 0;

        for (var entity : loanSmsRepo.findAllByAckIdIsNullAndSendResponseIsNotNull()) {
            String ackId = parseSmsAckId(entity.getSendResponse());
            if (StringUtils.hasText(ackId)) {
                entity.setAckId(ackId);
                loanSmsRepo.save(entity);
                count++;
                log.info("Re-parsed loan SMS ack_id={} for record id={}", ackId, entity.getId());
            }
        }

        for (var entity : nonLoanSmsRepo.findAllByAckIdIsNullAndSendResponseIsNotNull()) {
            String ackId = parseSmsAckId(entity.getSendResponse());
            if (StringUtils.hasText(ackId)) {
                entity.setAckId(ackId);
                nonLoanSmsRepo.save(entity);
                count++;
                log.info("Re-parsed non-loan SMS ack_id={} for record id={}", ackId, entity.getId());
            }
        }

        log.info("reParseSmsMissingAckIds: updated {} records", count);
        return count;
    }

    @Override
    public int reParseWhatsAppMissingAckIds() {
        int count = 0;

        for (var entity : loanWhatsappRepo.findAllByAckIdIsNullAndSendResponseIsNotNull()) {
            String ackId = parseWhatsAppAckId(entity.getSendResponse());
            if (StringUtils.hasText(ackId)) {
                entity.setAckId(ackId);
                loanWhatsappRepo.save(entity);
                count++;
                log.info("Re-parsed loan WhatsApp ack_id={} for record id={}", ackId, entity.getId());
            }
        }

        for (var entity : nonLoanWhatsappRepo.findAllByAckIdIsNullAndSendResponseIsNotNull()) {
            String ackId = parseWhatsAppAckId(entity.getSendResponse());
            if (StringUtils.hasText(ackId)) {
                entity.setAckId(ackId);
                nonLoanWhatsappRepo.save(entity);
                count++;
                log.info("Re-parsed non-loan WhatsApp ack_id={} for record id={}", ackId, entity.getId());
            }
        }

        log.info("reParseWhatsAppMissingAckIds: updated {} records", count);
        return count;
    }

    private String parseSmsAckId(String response) {
        if (!StringUtils.hasText(response)) return null;
        try {
            JsonNode root = objectMapper.readTree(response);
            String ackId = root.path("data").path("ack_id").asText(null);
            return StringUtils.hasText(ackId) ? ackId : null;
        } catch (Exception ex) {
            log.warn("Could not parse SMS ack_id from response: {}", ex.getMessage());
            return null;
        }
    }

    private String parseWhatsAppAckId(String response) {
        if (!StringUtils.hasText(response)) return null;
        try {
            JsonNode root = objectMapper.readTree(response);
            String wamid = root.path("message_wamid").asText(null);
            if (StringUtils.hasText(wamid) && !"null".equals(wamid)) return wamid;
            long messageId = root.path("message_id").asLong(0);
            return messageId > 0 ? String.valueOf(messageId) : null;
        } catch (Exception ex) {
            log.warn("Could not parse WhatsApp ack_id from response: {}", ex.getMessage());
            return null;
        }
    }
}

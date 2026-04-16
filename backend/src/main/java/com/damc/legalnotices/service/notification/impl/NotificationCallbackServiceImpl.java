package com.damc.legalnotices.service.notification.impl;

import com.damc.legalnotices.dao.notification.StatusReportSmsDao;
import com.damc.legalnotices.dao.notification.StatusReportWhatsappDao;
import com.damc.legalnotices.dto.notification.SmsDlrCallbackDto;
import com.damc.legalnotices.dto.notification.WhatsAppCallbackDto;
import com.damc.legalnotices.entity.notification.StatusReportSmsEntity;
import com.damc.legalnotices.entity.notification.StatusReportWhatsappEntity;
import com.damc.legalnotices.repository.notification.SendLoanSmsDetailRepository;
import com.damc.legalnotices.repository.notification.SendLoanWhatsappDetailRepository;
import com.damc.legalnotices.repository.notification.SendNonLoanSmsDetailRepository;
import com.damc.legalnotices.repository.notification.SendNonLoanWhatsappDetailRepository;
import com.damc.legalnotices.repository.notification.StatusReportSmsRepository;
import com.damc.legalnotices.repository.notification.StatusReportWhatsappRepository;
import com.damc.legalnotices.service.notification.NotificationCallbackService;
import com.damc.legalnotices.util.NoticationCallbackUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCallbackServiceImpl implements NotificationCallbackService {
    private final ObjectMapper objectMapper;
    private final NoticationCallbackUtil callbackUtil;

    private final SendLoanSmsDetailRepository loanSmsRepo;
    private final SendNonLoanSmsDetailRepository nonLoanSmsRepo;
    private final SendLoanWhatsappDetailRepository loanWhatsappRepo;
    private final SendNonLoanWhatsappDetailRepository nonLoanWhatsappRepo;

    private final StatusReportSmsRepository smsRepository;
    private final StatusReportWhatsappRepository whatsappRepository;

    @Override
    public boolean noticeSmsDeliveryReport(String requestBody) {
        if (!StringUtils.hasText(requestBody)) {
            log.warn("SMS DLR: empty request body");
            return false;
        }
        boolean updated = false;
        try {
            SmsDlrCallbackDto dlr = objectMapper.readValue(requestBody, SmsDlrCallbackDto.class);
            String messageId = dlr.getMessageId();

            if (!StringUtils.hasText(messageId)) {
                log.warn("SMS DLR: messageid is missing in payload: {}", requestBody);
                return false;
            }

            Instant receivedAt = callbackUtil.parseDelivTime(dlr.getDelivTime());

            var loanOpt = loanSmsRepo.findByAckId(messageId);
            if (loanOpt.isPresent()) {
                var entity = loanOpt.get();
                entity.setReceivedStatus(dlr.getDlrStatus());
                entity.setReceivedAt(receivedAt);
                loanSmsRepo.save(entity);
                updated = true;
                log.info("SMS DLR: updated loan SMS record id={} ackId={}", entity.getId(), messageId);
            } else {
                var nonLoanOpt = nonLoanSmsRepo.findByAckId(messageId);
                if (nonLoanOpt.isPresent()) {
                    var entity = nonLoanOpt.get();
                    entity.setReceivedStatus(dlr.getDlrStatus());
                    entity.setReceivedAt(receivedAt);
                    nonLoanSmsRepo.save(entity);
                    updated = true;
                    log.info("SMS DLR: updated non-loan SMS record id={} ackId={}", entity.getId(), messageId);
                }
            }

        } catch (Exception ex) {
            log.error("SMS DLR: error noticeing delivery report: {}", ex.getMessage(), ex);
        }
        return updated;
    }

    @Override
    public boolean noticeWhatsAppDeliveryReport(String requestBody) {
        if (!StringUtils.hasText(requestBody)) {
            log.warn("WhatsApp callback: empty request body");
            return false;
        }
        boolean updated = false;
        try {
            WhatsAppCallbackDto callback = objectMapper.readValue(requestBody, WhatsAppCallbackDto.class);
            if (callback.getEntry() == null) {
                log.warn("WhatsApp callback: no entry array in payload");
                return false;
            }
            for (WhatsAppCallbackDto.Entry entry : callback.getEntry()) {
                if (entry.getChanges() == null)
                    continue;
                for (WhatsAppCallbackDto.Change change : entry.getChanges()) {
                    WhatsAppCallbackDto.Value value = change.getValue();
                    if (value == null || value.getStatuses() == null)
                        continue;
                    for (WhatsAppCallbackDto.Status status : value.getStatuses()) {
                        String wamid = status.getId();
                        String statusVal = status.getStatus();

                        if (!StringUtils.hasText(wamid))
                            continue;

                        Instant receivedAt = callbackUtil.parseEpochTimestamp(status.getTimestamp());

                        var loanOpt = loanWhatsappRepo.findByAckId(wamid);
                        if (loanOpt.isPresent()) {
                            var entity = loanOpt.get();
                            entity.setReceivedStatus(statusVal);
                            entity.setReceivedAt(receivedAt);
                            loanWhatsappRepo.save(entity);
                            updated = true;
                            log.info("WhatsApp callback: updated loan WhatsApp record id={} wamid={} status={}",
                                    entity.getId(), wamid, statusVal);
                        } else {
                            var nonLoanOpt = nonLoanWhatsappRepo.findByAckId(wamid);
                            if (nonLoanOpt.isPresent()) {
                                var entity = nonLoanOpt.get();
                                entity.setReceivedStatus(statusVal);
                                entity.setReceivedAt(receivedAt);
                                nonLoanWhatsappRepo.save(entity);
                                updated = true;
                                log.info("WhatsApp callback: updated non-loan WhatsApp record id={} wamid={} status={}",
                                        entity.getId(), wamid, statusVal);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("WhatsApp callback: error noticeing status update: {}", ex.getMessage(), ex);
        }
        return updated;
    }

    @Override
    public int reParseSmsMissingAckIds() {
        int count = 0;

        for (var entity : loanSmsRepo.findAllByAckIdIsNullAndSendResponseIsNotNull()) {
            String ackId = callbackUtil.parseSmsAckId(entity.getSendResponse());
            if (StringUtils.hasText(ackId)) {
                entity.setAckId(ackId);
                loanSmsRepo.save(entity);
                count++;
                log.info("Re-parsed loan SMS ack_id={} for record id={}", ackId, entity.getId());
            }
        }

        for (var entity : nonLoanSmsRepo.findAllByAckIdIsNullAndSendResponseIsNotNull()) {
            String ackId = callbackUtil.parseSmsAckId(entity.getSendResponse());
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
            String ackId = callbackUtil.parseWhatsAppAckId(entity.getSendResponse());
            if (StringUtils.hasText(ackId)) {
                entity.setAckId(ackId);
                loanWhatsappRepo.save(entity);
                count++;
                log.info("Re-parsed loan WhatsApp ack_id={} for record id={}", ackId, entity.getId());
            }
        }

        for (var entity : nonLoanWhatsappRepo.findAllByAckIdIsNullAndSendResponseIsNotNull()) {
            String ackId = callbackUtil.parseWhatsAppAckId(entity.getSendResponse());
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

    @Override
    public List<StatusReportSmsDao> noticePendingSmsParsing() {
        List<StatusReportSmsEntity> pendingRecords = smsRepository.findByProcessDateIsNull();

        List<StatusReportSmsDao> updatedRecords = new java.util.ArrayList<>();
        for (StatusReportSmsEntity record : pendingRecords) {

            boolean updated = noticeSmsDeliveryReport(record.getRequestBody());
            if (updated) {
                record.setCompleteDate(LocalDateTime.now());
                record.setProcessDate(LocalDateTime.now());
                record.setStatus(updated ? "complete" : "noupdate");
                smsRepository.save(record);
                updatedRecords.add(callbackUtil.toDao(record));
            }
        }
        return updatedRecords;
    }

    @Override
    public List<StatusReportWhatsappDao> noticePendingWhatsAppParsing() {
        List<StatusReportWhatsappEntity> pendingRecords = whatsappRepository.findByProcessDateIsNull();
        List<StatusReportWhatsappDao> updatedRecords = new java.util.ArrayList<>();
        for (StatusReportWhatsappEntity record : pendingRecords) {
            // Implement the logic to parse requestBody and extract ack_id
            // For example, if requestBody is JSON, you can use a JSON parser to extract
            // ack_id

            boolean updated = noticeWhatsAppDeliveryReport(record.getRequestBody());

            record.setCompleteDate(LocalDateTime.now());
            record.setProcessDate(LocalDateTime.now());
            record.setStatus(updated ? "complete" : "noupdate");
            whatsappRepository.save(record);
            updatedRecords.add(callbackUtil.toDao(record));

        }
        return updatedRecords;
    }
}

package com.damc.legalnotices.service.notification.impl;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.damc.legalnotices.dto.notification.SmsDataDto;
import com.damc.legalnotices.dto.notification.WhatsAppDataDto;
import com.damc.legalnotices.entity.notification.SendErrorSmsDetailEntity;
import com.damc.legalnotices.entity.notification.SendErrorWhatsappDetailEntity;
import com.damc.legalnotices.entity.notification.SendLoanSmsDetailEntity;
import com.damc.legalnotices.entity.notification.SendLoanWhatsappDetailEntity;
import com.damc.legalnotices.entity.notification.SendNonLoanSmsDetailEntity;
import com.damc.legalnotices.entity.notification.SendNonLoanWhatsappDetailEntity;
import com.damc.legalnotices.repository.notification.SendErrorSmsDetailRepository;
import com.damc.legalnotices.repository.notification.SendErrorWhatsappDetailRepository;
import com.damc.legalnotices.repository.notification.SendLoanSmsDetailRepository;
import com.damc.legalnotices.repository.notification.SendLoanWhatsappDetailRepository;
import com.damc.legalnotices.repository.notification.SendNonLoanSmsDetailRepository;
import com.damc.legalnotices.repository.notification.SendNonLoanWhatsappDetailRepository;
import com.damc.legalnotices.service.notification.NotificationsSaveService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationsSaveServiceImpl implements NotificationsSaveService {
        private final SendErrorSmsDetailRepository sendErrorSmsDetailRepository;
        private final SendErrorWhatsappDetailRepository sendErrorWhatsappDetailRepository;
        private final SendNonLoanSmsDetailRepository sendNonLoanSmsDetailRepository;
        private final SendNonLoanWhatsappDetailRepository sendNonLoanWhatsappDetailRepository;
        private final SendLoanSmsDetailRepository sendLoanSmsDetailRepository;
        private final SendLoanWhatsappDetailRepository sendLoanWhatsappDetailRepository;
        private final ObjectMapper objectMapper;

        @Override
        public void StoreSmsDetails(String sendType, SmsDataDto smsData, boolean success, String response,
                        boolean schedule,
                        Exception ex) {
                if (ex != null) {
                        log.error("Error while sending {} sms for noticeSno: {}, message: {}, mobile: {}, error: {}",
                                        sendType, smsData.getConfig().getId(), smsData.getMessage(),
                                        smsData.getMobileNumber(), ex.getMessage());
                        SendErrorSmsDetailEntity errorEntity = new SendErrorSmsDetailEntity();
                        errorEntity.setType(sendType);
                        errorEntity.setScheduleSno(smsData.getScheduleId());
                        errorEntity.setProcessSno(smsData.getConfig().getProcess().getId());
                        errorEntity.setSmsTemplateSno(smsData.getConfig().getId());
                        errorEntity.setLoanNo(smsData.getScheduleItemId());
                        errorEntity.setSendTo(smsData.getMobileNumber());
                        errorEntity.setMessage(smsData.getPostMessage());
                        errorEntity.setSendAt(Instant.now());
                        errorEntity.setErrorMessage(ex.getMessage());
                        sendErrorSmsDetailRepository.save(errorEntity);
                } else {
                        log.info("{} sms sent for noticeSno: {}, message: {}, mobile: {}, response: {}",
                                        sendType, smsData.getConfig().getId(), smsData.getMessage(),
                                        smsData.getMobileNumber(), response);

                        if (smsData.getScheduleItemId() != null) {
                                SendLoanSmsDetailEntity entity = new SendLoanSmsDetailEntity();
                                entity.setScheduleSno(smsData.getScheduleId());
                                entity.setProcessSno(smsData.getConfig().getProcess().getId());
                                entity.setSmsTemplateSno(smsData.getConfig().getId());
                                entity.setLoanNo(smsData.getScheduleItemId());
                                entity.setSendTo(smsData.getMobileNumber());
                                entity.setMessage(smsData.getPostMessage());
                                entity.setSendAt(Instant.now());
                                entity.setSendStatus(success ? (schedule ? 2 : 1) : -2);
                                entity.setSendResponse(response);
                                entity.setAckId(parseSmsAckId(response));
                                sendLoanSmsDetailRepository.save(entity);
                        } else {
                                SendNonLoanSmsDetailEntity entity = new SendNonLoanSmsDetailEntity();
                                entity.setScheduleSno(smsData.getScheduleId());
                                entity.setProcessSno(smsData.getConfig().getProcess().getId());
                                entity.setSmsTemplateSno(smsData.getConfig().getId());
                                entity.setSendTo(smsData.getMobileNumber());
                                entity.setMessage(smsData.getPostMessage());
                                entity.setSendAt(Instant.now());
                                entity.setSendStatus(success ? (schedule ? 2 : 1) : -2);
                                entity.setSendResponse(response);
                                entity.setAckId(parseSmsAckId(response));
                                sendNonLoanSmsDetailRepository.save(entity);
                        }
                }
        }

        @Override
        public void StoreWhatsAppDetails(String sendType, WhatsAppDataDto whatsappData, boolean success,
                        String response,
                        boolean schedule, Exception ex) {
                if (ex != null) {
                        log.error("Error while sending {} whatsapp for noticeSno: {}, message: {}, mobile: {}, error: {}",
                                        sendType, whatsappData.getConfig().getId(), whatsappData.getMessage(),
                                        whatsappData.getMobileNumber(), ex.getMessage());
                        SendErrorWhatsappDetailEntity errorEntity = new SendErrorWhatsappDetailEntity();
                        errorEntity.setType(sendType);
                        errorEntity.setScheduleSno(whatsappData.getScheduleId());
                        errorEntity.setProcessSno(whatsappData.getConfig().getProcess().getId());
                        errorEntity.setWhatsappTemplateSno(whatsappData.getConfig().getId());
                        errorEntity.setLoanNo(whatsappData.getScheduleItemId());
                        errorEntity.setSendTo(whatsappData.getMobileNumber());
                        errorEntity.setMessage(whatsappData.getPostMessage());
                        errorEntity.setSendAt(Instant.now());
                        errorEntity.setErrorMessage(ex.getMessage());
                        sendErrorWhatsappDetailRepository.save(errorEntity);
                } else {
                        log.info("{} whatsapp sent for noticeSno: {}, message: {}, mobile: {}, response: {}",
                                        sendType, whatsappData.getConfig().getId(), whatsappData.getMessage(),
                                        whatsappData.getMobileNumber(), response);

                        if (whatsappData.getScheduleItemId() != null) {
                                SendLoanWhatsappDetailEntity entity = new SendLoanWhatsappDetailEntity();
                                entity.setScheduleSno(whatsappData.getScheduleId());
                                entity.setProcessSno(whatsappData.getConfig().getProcess().getId());
                                entity.setWhatsappTemplateSno(whatsappData.getConfig().getId());
                                entity.setLoanNo(whatsappData.getScheduleItemId());
                                entity.setSendTo(whatsappData.getMobileNumber());
                                entity.setMessage(whatsappData.getPostMessage());
                                entity.setSendAt(Instant.now());
                                entity.setSendStatus(success ? (schedule ? 2 : 1) : -2);
                                entity.setSendResponse(response);
                                entity.setAckId(parseWhatsAppAckId(response));
                                sendLoanWhatsappDetailRepository.save(entity);
                        } else {
                                SendNonLoanWhatsappDetailEntity entity = new SendNonLoanWhatsappDetailEntity();
                                entity.setScheduleSno(whatsappData.getScheduleId());
                                entity.setProcessSno(whatsappData.getConfig().getProcess().getId());
                                entity.setWhatsappTemplateSno(whatsappData.getConfig().getId());
                                entity.setSendTo(whatsappData.getMobileNumber());
                                entity.setMessage(whatsappData.getPostMessage());
                                entity.setSendAt(Instant.now());
                                entity.setSendStatus(success ? (schedule ? 2 : 1) : -2);
                                entity.setSendResponse(response);
                                entity.setAckId(parseWhatsAppAckId(response));
                                sendNonLoanWhatsappDetailRepository.save(entity);
                        }
                }
        }

        /**
         * Parses SMS send response: {"data":{"ack_id":"...","msgid":"..."}}
         */
        private String parseSmsAckId(String response) {
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

        /**
         * Parses WhatsApp send response:
         * {"status":"success","message_id":47880,"message_wamid":"..."}
         */
        private String parseWhatsAppAckId(String response) {
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
}

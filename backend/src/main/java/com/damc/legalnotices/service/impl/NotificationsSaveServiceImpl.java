package com.damc.legalnotices.service.impl;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.damc.legalnotices.dto.SmsDataDto;
import com.damc.legalnotices.dto.WhatsAppDataDto;
import com.damc.legalnotices.entity.SendErrorSmsDetailEntity;
import com.damc.legalnotices.entity.SendErrorWhatsappDetailEntity;
import com.damc.legalnotices.entity.SendLoanSmsDetailEntity;
import com.damc.legalnotices.entity.SendLoanWhatsappDetailEntity;
import com.damc.legalnotices.entity.SendNonLoanSmsDetailEntity;
import com.damc.legalnotices.entity.SendNonLoanWhatsappDetailEntity;
import com.damc.legalnotices.repository.SendErrorSmsDetailRepository;
import com.damc.legalnotices.repository.SendErrorWhatsappDetailRepository;
import com.damc.legalnotices.repository.SendLoanSmsDetailRepository;
import com.damc.legalnotices.repository.SendLoanWhatsappDetailRepository;
import com.damc.legalnotices.repository.SendNonLoanSmsDetailRepository;
import com.damc.legalnotices.repository.SendNonLoanWhatsappDetailRepository;
import com.damc.legalnotices.service.NotificationsSaveService;

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

        @Override
        public void StoreSmsDetails(String sendType, SmsDataDto smsData, boolean success, String response,
                        boolean schedule,
                        Exception ex) {
                if (ex != null) {
                        log.error("Error while sending {} sms for processSno: {}, message: {}, mobile: {}, error: {}",
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
                        log.info("{} sms sent for processSno: {}, message: {}, mobile: {}, response: {}",
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
                                sendNonLoanSmsDetailRepository.save(entity);
                        }
                }
        }

        @Override
        public void StoreWhatsAppDetails(String sendType, WhatsAppDataDto whatsappData, boolean success,
                        String response,
                        boolean schedule, Exception ex) {
                if (ex != null) {
                        log.error("Error while sending {} whatsapp for processSno: {}, message: {}, mobile: {}, error: {}",
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
                        log.info("{} whatsapp sent for processSno: {}, message: {}, mobile: {}, response: {}",
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
                                sendNonLoanWhatsappDetailRepository.save(entity);
                        }
                }
        }

}

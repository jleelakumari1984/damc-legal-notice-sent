package com.damc.legalnotices.service.notification.impl;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.config.SmsCredential;
import com.damc.legalnotices.dto.notification.SmsDataDto;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.errors.SmsSendException;
import com.damc.legalnotices.service.notification.NotificationsSaveService;
import com.damc.legalnotices.service.notification.SmsSenderService;
import com.damc.legalnotices.util.generator.HtmlTemplateGenerator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@AllArgsConstructor
public class SmsSenderServiceImpl implements SmsSenderService {

    private final LocationProperties appConfig;
    private final RestTemplateBuilder restTemplateBuilder;
    private final NotificationsSaveService notificationsSave;

    @Override
    public void send(SmsDataDto smsData, MasterProcessTemplateDetailEntity template, SmsCredential credential) {
        try {
            if (smsData.getConfig() == null) {
                throw new SmsSendException("Invalid Sms configuration details ");
            }
            if (!StringUtils.hasText(smsData.getConfig().getTemplatePath())) {
                throw new SmsSendException("Sms template path is empty");
            }
            log.info("Sending SMS for agreement {} using template {}", smsData.getAgreementNumber(),
                    smsData.getConfig().getTemplateId());
            HtmlTemplateGenerator htmlGenerator = new HtmlTemplateGenerator(appConfig);
            String smsBody = htmlGenerator.generate(smsData.getProps(), smsData.getConfig().getTemplatePath());
            smsData.setMessage(smsBody);
            sendMessage(template.getName(), smsData, credential);
        } catch (SmsSendException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new SmsSendException("Error while sending sms " + ex.getMessage(), ex);
        }
    }

    private void sendMessage(String sendType, SmsDataDto smsData, SmsCredential credential) {
        try {
            checkValidData(smsData, credential);
            String smsPostData = smsData.getPostData(credential);
            String messageUrl = credential.getUrl();
            ResponseEntity<String> response = restTemplateBuilder.build().postForEntity(messageUrl, smsPostData,
                    String.class);
            log.info("SMS Sent: {} : {} : {}", messageUrl, smsPostData, response.getBody());
            notificationsSave.StoreSmsDetails(sendType, smsData, true, response.getBody(), false, null);
        } catch (SmsSendException ex) {
            notificationsSave.StoreSmsDetails(sendType, smsData, false, ex.getMessage(), false, ex);
            throw ex;
        } catch (Exception ex) {
            notificationsSave.StoreSmsDetails(sendType, smsData, false, ex.getMessage(), false, ex);
            throw new SmsSendException("Error while sending sms " + ex.getMessage(), ex);
        }
    }

    private void checkValidData(SmsDataDto sms, SmsCredential credential) {
        if (!StringUtils.hasText(sms.getMessage())) {
            throw new SmsSendException("no SMS Body ");
        }
        if (!credential.isLive()) {
            if (!StringUtils.hasText(credential.getTestMobileNumber())) {
                throw new SmsSendException("Please configure test mobile number");
            }
            sms.setMobileNumber(credential.getTestMobileNumber());
        }
        sms.setMobileNumber(sms.getMobileNumber() == null ? "" : sms.getMobileNumber().replaceAll("[^0-9]", ""));
        if (!StringUtils.hasText(sms.getMobileNumber())) {
            throw new SmsSendException("Sms mobile number is empty");
        }
    }
}

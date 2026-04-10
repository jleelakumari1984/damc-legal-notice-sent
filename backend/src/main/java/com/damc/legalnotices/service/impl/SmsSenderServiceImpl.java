package com.damc.legalnotices.service.impl;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.config.SmsProperties;
import com.damc.legalnotices.dto.SmsDataDto;
import com.damc.legalnotices.entity.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.errors.SmsSendException;
import com.damc.legalnotices.service.NotificationsSaveService;
import com.damc.legalnotices.service.SmsSenderService;
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

    private final SmsProperties smsProperties;
    private final LocationProperties appConfig;
    private final SmsProperties smsConfig;
    private final RestTemplateBuilder restTemplateBuilder;
    private final NotificationsSaveService notificationsSave;

    @Override
    public void send(SmsDataDto smsData, MasterProcessTemplateDetailEntity template) {
        try {

            if (smsData.getConfig() == null) {
                throw new SmsSendException("Invalid Sms configuration details ");
            }
            if (!StringUtils.hasText(smsData.getConfig().getTemplatePath())) {
                throw new SmsSendException("Sms template path is empty");
            }
            log.info("Sending SMS for agreement {} using template {} attachment {}",
                    smsData.getAgreementNumber(), smsData.getConfig().getTemplateId());
            HtmlTemplateGenerator htmlGenerator = new HtmlTemplateGenerator(appConfig);
            String smsBody = htmlGenerator.generate(smsData.getProps(), smsData.getConfig().getTemplatePath());
            smsData.setMessage(smsBody);
            sendMessage(template.getName(), smsData);
        } catch (SmsSendException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new SmsSendException("Error while sending sms " + ex.getMessage(), ex);
        } finally {
        }
    }

    private void sendMessage(String sendType, SmsDataDto smsData) {
        try {
            checkValidData(smsData);
            String smsPostData = smsData.getPostData(smsConfig);
            if (smsData.isSendEnabled()) {
                String messageUrl = smsConfig.getUrl();
                ResponseEntity<String> response = restTemplateBuilder.build().postForEntity(messageUrl, smsPostData,
                        String.class);
                log.info("SMS Sent:" + messageUrl + ":" + smsPostData + ":" + response.getBody());

                notificationsSave.StoreSmsDetails(sendType, smsData, true, response.getBody(), false, null);
            } else {
                throw new SmsSendException("Send Disabled");
            }

        } catch (SmsSendException ex) {
            notificationsSave.StoreSmsDetails(sendType, smsData, false, ex.getMessage(), false, ex);
            throw ex;
        } catch (Exception ex) {
            notificationsSave.StoreSmsDetails(sendType, smsData, false, ex.getMessage(), false, ex);
            throw new SmsSendException("Error while sending sms " + ex.getMessage(), ex);
        }
    }

    private void checkValidData(SmsDataDto sms) {
        if (!StringUtils.hasText(sms.getMessage())) {
            throw new SmsSendException("no SMS Body ");
        }
        if (!smsProperties.isLive()) {
            if (!StringUtils.hasText(smsProperties.getTestMobileNumber())) {
                throw new SmsSendException("Please configure test mobile number");
            }
            sms.setMobileNumber(smsProperties.getTestMobileNumber());
        }
        sms.setMobileNumber(sms.getMobileNumber() == null ? "" : sms.getMobileNumber().replaceAll("[^0-9]", ""));
        if (!StringUtils.hasText(sms.getMobileNumber())) {
            throw new SmsSendException("Sms mobile number is empty");
        }
    }
}

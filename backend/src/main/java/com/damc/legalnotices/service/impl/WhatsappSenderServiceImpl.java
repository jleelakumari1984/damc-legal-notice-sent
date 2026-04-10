package com.damc.legalnotices.service.impl;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.config.WhatsAppProperties;
import com.damc.legalnotices.dto.WhatsAppAttachmentDto;
import com.damc.legalnotices.dto.WhatsAppDataDto;
import com.damc.legalnotices.entity.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.errors.WhatsAppSendException;
import com.damc.legalnotices.service.NotificationsSaveService;
import com.damc.legalnotices.service.WhatsappSenderService;
import com.damc.legalnotices.util.generator.HtmlTemplateGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@AllArgsConstructor
public class WhatsappSenderServiceImpl implements WhatsappSenderService {

    private final LocationProperties appConfig;
    private final WhatsAppProperties whapAppConfig;
    private final RestTemplateBuilder restTemplateBuilder;
    private final NotificationsSaveService notificationsSave;

    private void validateData(WhatsAppDataDto bean) throws WhatsAppSendException {
        if (!whapAppConfig.isLive()) {
            if (!StringUtils.hasText(whapAppConfig.getTestMobileNumber())) {
                throw new WhatsAppSendException("Please configure test mobile number");
            }
            bean.setMobileNumber(whapAppConfig.getTestMobileNumber());
        }
        bean.setMobileNumber(bean.getMobileNumber() == null ? "" : bean.getMobileNumber().replaceAll("[^0-9]", ""));
        if (!StringUtils.hasText(bean.getMobileNumber()) || bean.getMobileNumber().length() < 10
                || bean.getMobileNumber().length() > 12) {
            throw new WhatsAppSendException("Invalid mobile number");
        }

        if (bean.getMobileNumber().length() == 10) {
            bean.setMobileNumber("91" + bean.getMobileNumber());
        }
    }

    @Override
    public void send(WhatsAppDataDto whatsAppData, MasterProcessTemplateDetailEntity template,
            List<String> attachments) {

        if (whatsAppData.getConfig() == null) {
            throw new WhatsAppSendException("Invalid WhatsApp configuration details ");
        }
        if (!org.springframework.util.StringUtils.hasText(whatsAppData.getConfig().getTemplatePath())) {
            throw new WhatsAppSendException("WhatsApp template path is empty");
        }
        log.info("Sending WhatsApp for agreement {} using template {}  ",
                whatsAppData.getAgreementNumber(), whatsAppData.getConfig().getTemplateName());
        try {
            HtmlTemplateGenerator htmlGenerator = new HtmlTemplateGenerator(appConfig);
            String whatsAppParams = htmlGenerator.generate(whatsAppData.getProps(),
                    whatsAppData.getConfig().getTemplatePath());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            List<Object> components = objectMapper.readValue(whatsAppParams, new TypeReference<List<Object>>() {
            });
            whatsAppData.setMessage(whatsAppParams);
            whatsAppData.setComponents(components);
            sendMessage(template.getName(), whatsAppData, attachments);
        } catch (

        WhatsAppSendException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new WhatsAppSendException("Error while sending whatsApp " + ex.getMessage(), ex);
        } finally {
        }

    }

    private void sendMessage(String sendType, WhatsAppDataDto whatsAppData, List<String> attachments) {
        try {
            validateData(whatsAppData);
            if (!whapAppConfig.isLive()) {
                if (!StringUtils.hasText(whapAppConfig.getTestMobileNumber())) {
                    throw new WhatsAppSendException("Please configure test mobile number");
                }
                whatsAppData.setMobileNumber(whapAppConfig.getTestMobileNumber());
            }
            if (!StringUtils.hasText(whatsAppData.getMessage())
                    && (whatsAppData.getComponents() == null || whatsAppData.getComponents().isEmpty())) {
                throw new WhatsAppSendException("Message is empty or parameters empty");
            }
            if (!StringUtils.hasText(whatsAppData.getMobileNumber())) {
                throw new WhatsAppSendException("Whatsapp mobile number is empty");
            }
            if (whatsAppData.getScheduleItemId() != null && attachments != null && !attachments.isEmpty()) {
                String downloadUrl = whapAppConfig.getAttachmentDownloadUrl();
                for (String attachment : attachments) {
                    Path attachmentPath = Path.of(attachment);
                    String fileUrl = downloadUrl + whatsAppData.getScheduleItemId() + "/"
                            + attachmentPath.getFileName();
                    whatsAppData.getComponents().add(WhatsAppAttachmentDto.ofDocument(fileUrl));
                }
            }
            String whatsAppPostData = whatsAppData.getPostData(whapAppConfig);
            String messageUrl = whapAppConfig.getUrl();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(whatsAppPostData, headers);
            ResponseEntity<String> response = restTemplateBuilder.build().postForEntity(messageUrl, entity,
                    String.class);
            log.info("WhatsApp Sent:" + messageUrl + ":" + whatsAppPostData + ":" + response.getBody());
            if (StringUtils.hasText(response.getBody()) && response.getBody().contains("error")) {
                throw new WhatsAppSendException(response.getBody());
            }
            notificationsSave.StoreWhatsAppDetails(sendType, whatsAppData, true, response.getBody(),
                    false,
                    null);

        } catch (WhatsAppSendException ex) {
            notificationsSave.StoreWhatsAppDetails(sendType, whatsAppData, false, ex.getMessage(), false, ex);
            throw ex;
        } catch (Exception ex) {
            notificationsSave.StoreWhatsAppDetails(sendType, whatsAppData, false, ex.getMessage(), false, ex);
            throw new WhatsAppSendException("Error while sending whatsapp message" + ex.getMessage(), ex);
        }
    }
}

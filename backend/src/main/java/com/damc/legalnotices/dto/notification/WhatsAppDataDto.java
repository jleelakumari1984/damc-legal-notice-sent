package com.damc.legalnotices.dto.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.damc.legalnotices.config.WhatsAppProperties;
import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WhatsAppDataDto {
    private MasterProcessWhatsappConfigDetailEntity config;
    private String agreementNumber;

    private Long scheduleId;

    private Long scheduleItemId;

    private String message;

    private String postMessage;

    private String mobileNumber;

    @Builder.Default
    private Map<String, Object> props = new HashMap<String, Object>();

    @Builder.Default
    private List<Object> components = new ArrayList<Object>();;
    private boolean sendEnabled;

    public void addMessage(String s) {
        if (message == null) {
            message = s;
        } else {
            message += s;
        }
    }

    public String getPostData(WhatsAppProperties whatAppConfig) throws JsonProcessingException {
        Hashtable<String, Object> sendObject = new Hashtable<>();
        sendObject.put("token", "********************");
        sendObject.put("phone", getMobileNumber());
        sendObject.put("template_language", config.getTemplateLang());
        sendObject.put("template_name", config.getTemplateName());
        sendObject.put("components", getComponents());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        setPostMessage(ow.writeValueAsString(sendObject));
        sendObject.replace("token", whatAppConfig.getAccessToken());
        return ow.writeValueAsString(sendObject);
    }
}

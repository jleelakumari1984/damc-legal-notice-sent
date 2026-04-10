package com.damc.legalnotices.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.damc.legalnotices.config.SmsProperties;
import com.damc.legalnotices.entity.MasterProcessSmsConfigDetailEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SmsDataDto {
    private MasterProcessSmsConfigDetailEntity config;
    
    private String agreementNumber;

    private Long scheduleId;

    private Long scheduleItemId;

    private String message;

    private String postMessage;

    private String mobileNumber;

    @Builder.Default
    private Map<String, Object> props = new HashMap<String, Object>();
    private boolean sendEnabled;

    public String getPostData(SmsProperties smsConfig) throws JsonProcessingException {
        Hashtable<String, Object> sendObject = new Hashtable<>();
        sendObject.put("username", "********************");
        sendObject.put("password", "********************");

        sendObject.put("from", config.getSenderId());
        sendObject.put("pe_id", config.getPeid());
        sendObject.put("template_id", config.getTemplateId());
        sendObject.put("coding", config.getDcs() + "");
        sendObject.put("flash", config.getFlashSms() + "");
        sendObject.put("text", getMessage());
        List<String> mobileNumbers = new ArrayList<>();
        mobileNumbers.add(getMobileNumber());
        sendObject.put("to", mobileNumbers);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        postMessage = ow.writeValueAsString(sendObject);

        sendObject.replace("username", smsConfig.getUserName());
        sendObject.replace("password", smsConfig.getPassword());

        return ow.writeValueAsString(sendObject);
    }
}

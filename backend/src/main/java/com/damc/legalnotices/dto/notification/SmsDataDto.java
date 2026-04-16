package com.damc.legalnotices.dto.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.damc.legalnotices.dao.user.UserSmsCredentialDao;
import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity;
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

    public String getPostData(UserSmsCredentialDao credential) throws Exception {
        SmsApiRequestDto smsApiRequestDao = SmsApiRequestDto.builder()
                .username("********************")
                .password("********************")
                .from(config.getSenderId())
                .peId(config.getPeid())
                .templateId(config.getTemplateId())
                .coding(config.getDcs() + "")
                .flash(config.getFlashSms() + "")
                .text(getMessage())
                .to(List.of(getMobileNumber()))
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        postMessage = ow.writeValueAsString(smsApiRequestDao);
        smsApiRequestDao.setUsername(credential.getUserName());
        smsApiRequestDao.setPassword(credential.getPassword());

        return ow.writeValueAsString(smsApiRequestDao);
    }
}

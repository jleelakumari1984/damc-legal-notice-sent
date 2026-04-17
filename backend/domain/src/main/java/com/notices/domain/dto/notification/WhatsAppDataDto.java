package com.notices.domain.dto.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.notices.domain.dao.user.UserWhatsAppCredentialDao;
import com.notices.domain.dto.notification.WhatsAppApiRequestDto.WhatsAppComponentDto;
import com.notices.domain.entity.master.MasterProcessWhatsappConfigDetailEntity;
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
    private List<WhatsAppComponentDto> components = new ArrayList<>();
    private boolean sendEnabled;

    public void addMessage(String s) {
        if (message == null) {
            message = s;
        } else {
            message += s;
        }
    }

    public String getPostData(UserWhatsAppCredentialDao credential) throws Exception {
        WhatsAppApiRequestDto whatsappApiRequestDao = WhatsAppApiRequestDto.builder()
                .phone(getMobileNumber())
                .token("********************")
                .templateLanguage(config.getTemplateLang())
                .templateName(config.getTemplateName())
                .components(getComponents())
                .build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        setPostMessage(ow.writeValueAsString(whatsappApiRequestDao));
        whatsappApiRequestDao.setToken(credential.getAccessToken());
        return ow.writeValueAsString(whatsappApiRequestDao);
    }
}

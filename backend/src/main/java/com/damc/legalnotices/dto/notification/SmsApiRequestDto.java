package com.damc.legalnotices.dto.notification;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SmsApiRequestDto {

    private String username;

    private String flash;

    @JsonProperty("pe_id")
    private String peId;

    private String coding;

    @JsonProperty("template_id")
    private String templateId;

    private List<String> to;

    private String from;

    private String password;

    private String text;
}

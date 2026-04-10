package com.damc.legalnotices.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendSampleNoticeRequestDto {
    private Long processSno;
    private String mobileNumber;
    private Boolean sendSms;
    private Boolean sendWhatsapp;
    private Map<String, Object> rowData;
}

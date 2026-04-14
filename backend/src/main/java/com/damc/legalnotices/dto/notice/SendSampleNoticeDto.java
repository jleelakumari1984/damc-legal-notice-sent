package com.damc.legalnotices.dto.notice;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendSampleNoticeDto {
    private Long processSno;
    private String mobileNumber;
    private Boolean sendSms;
    private Boolean sendWhatsapp;
    private Map<String, Object> rowData;
}

package com.damc.legalnotices.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeValidationRowDto {
    private String agreementNumber;
    private String customerName;
    private String mobileSms;
    private String mobileWhatsapp;
    private String expectedPdfFile;
    private boolean documentPresent;
}

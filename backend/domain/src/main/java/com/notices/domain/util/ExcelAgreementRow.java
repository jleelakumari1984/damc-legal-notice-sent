package com.notices.domain.util;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExcelAgreementRow {
    private String agreementNumber;
    private String customerName;
    private String mobileSms;
    private String mobileWhatsapp;
    private String pdfFileName;
}

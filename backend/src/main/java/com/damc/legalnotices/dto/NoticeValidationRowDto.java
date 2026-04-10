package com.damc.legalnotices.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeValidationRowDto {
    private String agreementNumber;
    private String excelData;
}

package com.damc.legalnotices.dto.notice;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeValidationRowDto {
    private String agreementNumber;
    private String excelData;
}

package com.damc.legalnotices.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.damc.legalnotices.dto.NoticeValidationRowDto;

@Getter
@Setter
@Builder
public class ProcessedExcelDao {
    private Long scheduledNoticeId;
    private String ExcelName;
    private List<NoticeValidationRowDto> validationRows;
    private String status;
    private String failureReason;
}

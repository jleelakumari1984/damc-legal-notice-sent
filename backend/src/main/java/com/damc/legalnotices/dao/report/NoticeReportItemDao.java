package com.damc.legalnotices.dao.report;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class NoticeReportItemDao {
    private Long id;
    private Map<String, Object> excelData;
    private String agreementNumber;
    private String status;
    private String failureReason;
    private LocalDateTime processedAt;
}

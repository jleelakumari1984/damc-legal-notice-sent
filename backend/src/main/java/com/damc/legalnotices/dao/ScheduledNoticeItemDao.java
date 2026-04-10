package com.damc.legalnotices.dao;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ScheduledNoticeItemDao {
    private Long id;
    private String agreementNumber;
    private Map<String, Object> excelData;
    private String status;
    private String failureReason;
    private LocalDateTime processedAt;
    private String attachements;
}

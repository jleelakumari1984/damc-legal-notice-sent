package com.damc.legalnotices.dao;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ProcessedNoticeItemDao {
    private Long scheduledNoticeId;
    private Long scheduledNoticeItemId;
    private String agreementNumber;
    private Map<String, Object> excelData;
    private String status;
    private String failureReason;
}

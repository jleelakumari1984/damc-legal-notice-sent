package com.damc.legalnotices.dao.excel;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ScheduledNoticeItemDao {
    private Long scheduledNoticeId;
    private Long scheduledNoticeItemId;
    private String agreementNumber;
    private Map<String, Object> excelData;
    private String status;
    private String failureReason;
}

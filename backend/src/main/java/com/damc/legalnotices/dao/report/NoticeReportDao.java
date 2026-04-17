package com.damc.legalnotices.dao.report;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeReportDao {
    private Long id;
    private String noticeName;
    private String createdUserName;
    private String originalFileName;
    private Boolean sendSms;
    private Boolean sendWhatsapp;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime noticeedAt;
    private String failureReason;
    private long totalItems;
    private long pendingItems;
    private long processingItems;
    private long completedItems;
    private long failedItems;
}

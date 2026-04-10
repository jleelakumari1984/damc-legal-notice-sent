package com.damc.legalnotices.dao;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ScheduledNoticeDetailDao {
    private Long id;
    private Long processSno;
    private String originalFileName;
    private Boolean sendSms;
    private Boolean sendWhatsapp;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String failureReason;
    private List<ScheduledNoticeItemDao> items;
}

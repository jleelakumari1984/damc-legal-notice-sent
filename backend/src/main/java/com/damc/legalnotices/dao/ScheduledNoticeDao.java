package com.damc.legalnotices.dao;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduledNoticeDao {
    private Long id;
    private Long processSno;
    private String originalFileName;
    private String zipFilePath;
    private String extractedFolderPath;
    private Boolean sendSms;
    private Boolean sendWhatsapp;
    private String status;
    private LocalDateTime createdAt;
}

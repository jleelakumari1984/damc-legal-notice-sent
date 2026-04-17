package com.damc.legalnotices.dao.schedule;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Builder
public class ScheduledNoticeDao {
    private Long id;
    private Long noticeSno;
    private String noticeName;
    private String originalFileName;
    @JsonIgnore
    private String zipFilePath;
    @JsonIgnore
    private String extractedFolderPath;
    private Boolean sendSms;
    private Boolean sendWhatsapp;
    private String status;
    private LocalDateTime createdAt;
}

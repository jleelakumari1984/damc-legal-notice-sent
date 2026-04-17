package com.damc.legalnotices.dao.notice;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeTemplateReportDao {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String createdUserName;
    private Long excelMapCount;
    private Long smsActiveCount;
    private Long smsInactiveCount;
    private Long whatsappActiveCount;
    private Long whatsappInactiveCount;
    private Long mailActiveCount;
    private Long mailInactiveCount;
}

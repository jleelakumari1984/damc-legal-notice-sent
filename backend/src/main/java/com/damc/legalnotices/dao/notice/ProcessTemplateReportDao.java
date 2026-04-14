package com.damc.legalnotices.dao.notice;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessTemplateReportDao {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private Long excelMapCount;
    private Long smsMapCount;
    private Long whatsappMapCount;
    private Long mailMapCount;
}

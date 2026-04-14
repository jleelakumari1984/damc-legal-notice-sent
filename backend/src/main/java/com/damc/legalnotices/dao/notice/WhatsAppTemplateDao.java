package com.damc.legalnotices.dao.notice;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WhatsAppTemplateDao {
    private Long id;
    private Long processId;
    private Long hearingStageId;
    private String hearingStageTitle;
    private Integer sentLevel;
    private String templateName;
    private String templatePath;
    private String templateContent;
    private String templateLang;
    private Integer status;
    private LocalDateTime createdAt;
}

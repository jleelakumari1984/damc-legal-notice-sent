package com.damc.legalnotices.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeWhatsappConfigDto {

    @NotNull(message = "processId is required")
    private Long processId;

    private Integer sentLevel;
    private String templateName;
    private String templateContent;
    private String userTemplateContent;
    private String templateLang;
    private Integer status;
}

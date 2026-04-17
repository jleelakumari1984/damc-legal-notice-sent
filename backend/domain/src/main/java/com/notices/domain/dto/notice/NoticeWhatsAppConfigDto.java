package com.notices.domain.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeWhatsAppConfigDto {

    @NotNull(message = "noticeId is required")
    private Long noticeId;

    private String templateName;
    private String templateContent;
    private String userTemplateContent;
    private String templateLang;
    private Integer status;
}

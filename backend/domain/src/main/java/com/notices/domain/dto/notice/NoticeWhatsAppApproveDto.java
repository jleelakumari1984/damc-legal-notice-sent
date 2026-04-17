package com.notices.domain.dto.notice;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeWhatsAppApproveDto {

    @NotBlank
    private String templateName;

    @NotBlank
    private String templateLang;

    @NotBlank
    private String templateContent;
}

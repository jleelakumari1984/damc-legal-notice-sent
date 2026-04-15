package com.damc.legalnotices.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeWhatsappApproveDto {

    @NotNull
    private String templateName;

    @NotNull
    private String templateLang;

    @NotNull
    private Integer sentLevel;
}

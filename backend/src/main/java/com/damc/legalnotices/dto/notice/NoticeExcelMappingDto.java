package com.damc.legalnotices.dto.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeExcelMappingDto {

    @NotNull(message = "processId is required")
    private Long processId;

    @NotBlank(message = "excelFieldName is required")
    private String excelFieldName;

    @NotBlank(message = "dbFieldName is required")
    private String dbFieldName;

    private Integer isKey;

    private Integer isMobile;

    private Integer isMandatory;

    private Integer isAttachment;
}

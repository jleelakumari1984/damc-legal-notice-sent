package com.damc.legalnotices.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProcessExcelMappingResponseDto {

    private Long id;

    private Long processId;

    private String excelFieldName;

    private String dbFieldName;

    private Integer isKey;

    private Integer isMobile;

    private Integer isMandatory;

    private Integer isAttachment;

    private LocalDateTime createdAt;
}

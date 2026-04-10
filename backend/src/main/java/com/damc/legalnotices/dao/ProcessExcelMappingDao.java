package com.damc.legalnotices.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessExcelMappingDao {
    private Long id;

    private String excelFieldName;

    @JsonIgnore
    private String dbFieldName;

    private Integer isMandatory;

    private Integer isAttachment;
    
    private Integer isKey;
}

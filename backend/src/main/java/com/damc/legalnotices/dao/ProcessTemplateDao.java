package com.damc.legalnotices.dao;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessTemplateDao {
    private Long id;
    private String name;
    private List<ProcessExcelMappingDao> excelMap;
}

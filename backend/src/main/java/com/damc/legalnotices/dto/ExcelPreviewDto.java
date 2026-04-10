package com.damc.legalnotices.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExcelPreviewDto {
    private List<String> columnNames;
    private List<ExcelPreviewRowDto> rows;
    private int totalRows;
}

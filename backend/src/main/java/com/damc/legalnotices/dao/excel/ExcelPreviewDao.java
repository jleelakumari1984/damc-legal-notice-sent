package com.damc.legalnotices.dao.excel;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExcelPreviewDao {

    private List<String> columnNames;

    private List<ExcelPreviewRowDao> rows;
    
    private int totalRows;
}

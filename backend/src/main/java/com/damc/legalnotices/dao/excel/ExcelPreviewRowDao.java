package com.damc.legalnotices.dao.excel;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.damc.legalnotices.dao.notice.NoticeExcelMappingDao;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Builder
public class ExcelPreviewRowDao {
    private Map<String, Object> data;
    private int rowNum;

    @JsonIgnore
    public String getKeyColumnData(List<NoticeExcelMappingDao> keyColumns, String separator) {
        if (keyColumns == null || keyColumns.isEmpty() || data == null) {
            return "";
        }
        if (separator == null) {
            separator = "_";
        }
        return keyColumns.stream()
                .map(col -> data.getOrDefault(col.getExcelFieldName(), "").toString())
                .filter(val -> !val.isBlank())
                .collect(Collectors.joining(separator));
    }
}

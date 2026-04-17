package com.notices.domain.util;

import com.notices.domain.dao.excel.ExcelPreviewDao;
import com.notices.domain.dao.excel.ExcelPreviewRowDao;
import com.notices.domain.errors.StopParsingException;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StreamingSheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

    private final int maxRows;
    private final List<String> headers = new ArrayList<>();
    private final List<ExcelPreviewRowDao> rows = new ArrayList<>();
    private Map<String, Object> currentRow;
    private int currentRowNum = -1;
    private int totalRows = 0;

    public StreamingSheetHandler(int maxRows) {
        this.maxRows = maxRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    @Override
    public void startRow(int rowNum) {
        currentRowNum = rowNum;
        if (rowNum > 0) {
            currentRow = new LinkedHashMap<>();
        }
    }

    @Override
    public void endRow(int rowNum) {
        log.info("Finished row {}", rowNum);
        if (rowNum > 0 && currentRow != null && !currentRow.isEmpty()) {
            for (String header : headers) {
                currentRow.putIfAbsent(header, "");
            }
            rows.add(ExcelPreviewRowDao.builder().data(currentRow).rowNum(rowNum).build());

        }
        if (maxRows > 0 && rowNum >= maxRows) {
            throw new StopParsingException();
        }
        currentRow = null;
    }

    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        if (currentRowNum == 0) {
            headers.add(formattedValue != null ? formattedValue.trim() : "");
        } else if (currentRow != null && cellReference != null && !headers.isEmpty()) {
            int colIdx = columnIndexFromRef(cellReference);
            if (colIdx < headers.size()) {
                currentRow.put(headers.get(colIdx), formattedValue != null ? formattedValue : "");
            }
        }
    }

    /**
     * Parses column index from a cell reference like "A1", "BC42" without
     * allocating a CellAddress.
     */
    private static int columnIndexFromRef(String cellRef) {
        int col = 0;
        for (int i = 0; i < cellRef.length(); i++) {
            char c = cellRef.charAt(i);
            if (c < 'A' || c > 'Z')
                break;
            col = col * 26 + (c - 'A' + 1);
        }
        return col - 1;
    }

    public ExcelPreviewDao toDao() {
        return ExcelPreviewDao.builder()
                .columnNames(headers)
                .rows(rows)
                .totalRows(totalRows)
                .build();
    }
}

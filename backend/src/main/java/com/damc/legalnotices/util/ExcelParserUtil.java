package com.damc.legalnotices.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ExcelParserUtil {

    private static final String AGREEMENT = "agreementnumber";
    private static final String CUSTOMER = "customername";
    private static final String SMS = "mobilesms";
    private static final String WHATSAPP = "mobilewhatsapp";
    private static final String PDF = "pdffilename";

    private final DataFormatter dataFormatter = new DataFormatter();

    public List<ExcelAgreementRow> parse(Path excelPath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                throw new IllegalArgumentException("Excel is empty");
            }

            Row headerRow = sheet.getRow(0);
            Map<String, Integer> indexMap = resolveHeaders(headerRow);

            List<ExcelAgreementRow> rows = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String agreement = getCell(row, indexMap.get(AGREEMENT));
                if (agreement == null || agreement.isBlank()) {
                    continue;
                }

                rows.add(ExcelAgreementRow.builder()
                        .agreementNumber(agreement.trim())
                        .customerName(getCell(row, indexMap.get(CUSTOMER)))
                        .mobileSms(getCell(row, indexMap.get(SMS)))
                        .mobileWhatsapp(getCell(row, indexMap.get(WHATSAPP)))
                        .pdfFileName(getCell(row, indexMap.get(PDF)))
                        .build());
            }
            return rows;
        }
    }

    private Map<String, Integer> resolveHeaders(Row headerRow) {
        Map<String, Integer> indexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String key = dataFormatter.formatCellValue(cell).replaceAll("\\s+", "").toLowerCase(Locale.ENGLISH);
            indexMap.put(key, cell.getColumnIndex());
        }

        require(indexMap, AGREEMENT, "AgreementNumber");
        require(indexMap, CUSTOMER, "CustomerName");
        require(indexMap, SMS, "MobileSms");
        require(indexMap, WHATSAPP, "MobileWhatsapp");
        require(indexMap, PDF, "PdfFileName");
        return indexMap;
    }

    private void require(Map<String, Integer> indexMap, String key, String displayName) {
        if (!indexMap.containsKey(key)) {
            throw new IllegalArgumentException("Missing required excel column: " + displayName);
        }
    }

    private String getCell(Row row, Integer index) {
        if (index == null) {
            return null;
        }
        Cell cell = row.getCell(index);
        return cell == null ? null : dataFormatter.formatCellValue(cell);
    }
}

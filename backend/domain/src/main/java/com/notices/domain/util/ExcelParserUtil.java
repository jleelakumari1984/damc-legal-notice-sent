package com.notices.domain.util;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.notices.domain.config.LocationProperties;
import com.notices.domain.dao.excel.ExcelPreviewDao;
import com.notices.domain.dao.excel.ExcelPreviewRowDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.errors.StopParsingException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths; 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class ExcelParserUtil {

    private final DataFormatter dataFormatter = new DataFormatter();
    private final LocationProperties storageProperties;
    private final ZipExtractorUtil zipExtractorUtil;
    private final FileUtil fileUtil;

    public Path getUserBasePath(LoginUserDao sessionUser) {
        return Paths.get(storageProperties.getUploadDir(), sessionUser.getId() + "");
    }

    
    public Path extractZip(Path zipPath) {
        try {
            Path targetDir = Paths.get(zipPath.getParent().toString(), "extracted")
                    .resolve(UUID.randomUUID().toString());
            fileUtil.createDirectoriesIfNotExists(targetDir);
            zipExtractorUtil.extract(zipPath, targetDir);
            return targetDir;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to extract ZIP file: " + ex.getMessage());
        }
    }

    public Path findExcel(Path extractedPath) {
        try (var stream = Files.walk(extractedPath)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString().toLowerCase();
                        return name.endsWith(".xlsx") || name.endsWith(".xls");
                    })
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Excel file not found in ZIP"));
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to inspect extracted ZIP: " + ex.getMessage());
        }
    }

    public ExcelPreviewDao parseAsPreview(Path excelPath) throws IOException {
        return parseAsPreview(excelPath, storageProperties.getPreviewMaxRows());
    }

    private ExcelPreviewDao parseAsPreview(Path excelPath, int maxRows) throws IOException {
        log.info("Parsing Excel file: {} with max rows: {}", excelPath.getFileName().toString(), maxRows);
        String name = excelPath.getFileName().toString().toLowerCase();
        if (name.endsWith(".xls")) {
            return parseWithDom(excelPath, maxRows);
        }
        return parseXlsxStreaming(excelPath, maxRows);
    }

    private ExcelPreviewDao parseXlsxStreaming(Path excelPath, int maxRows) throws IOException {
        StreamingSheetHandler handler = new StreamingSheetHandler(maxRows);
        try (OPCPackage pkg = OPCPackage.open(excelPath.toFile(), PackageAccess.READ)) {
            XSSFReader xssfReader = new XSSFReader(pkg);
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            StylesTable styles = xssfReader.getStylesTable();
            XSSFSheetXMLHandler sheetHandler = new XSSFSheetXMLHandler(styles, strings, handler, dataFormatter, true);

            XMLReader parser = XMLHelper.newXMLReader();
            parser.setContentHandler(sheetHandler);

            Iterator<InputStream> sheets = xssfReader.getSheetsData();
            if (sheets.hasNext()) {
                try (InputStream sheetStream = sheets.next()) {
                    byte[] sheetBytes = sheetStream.readAllBytes();
                    int totalRows = readTotalRowsFromDimension(sheetBytes);
                    // handler.setTotalRows(totalRows);
                    log.info("Sheet dimension: {} total rows", totalRows);
                    parser.parse(new InputSource(new java.io.ByteArrayInputStream(sheetBytes)));
                }
            }
        } catch (StopParsingException ignored) {
            // row limit reached — normal for preview
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException("Failed to parse Excel file: " + ex.getMessage(), ex);
        }
        return handler.toDao();
    }

    /**
     * Reads the total row count from the {@code <dimension ref="A1:Z50000"/>}
     * element
     * in the raw sheet XML bytes without a full SAX parse.
     * Returns 0 if the dimension element is absent or unparseable.
     */
    private static int readTotalRowsFromDimension(byte[] sheetBytes) {
        // The <dimension> element is always in the first ~512 bytes
        int scanLimit = Math.min(sheetBytes.length, 512);
        String header = new String(sheetBytes, 0, scanLimit, java.nio.charset.StandardCharsets.UTF_8);
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("dimension\\s+ref=\"[A-Z]+(\\d+):[A-Z]+(\\d+)\"")
                .matcher(header);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(2)); // end row of the ref range
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    private ExcelPreviewDao parseWithDom(Path excelPath, int maxRows) throws IOException {
        try (InputStream is = Files.newInputStream(excelPath);
                Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                return ExcelPreviewDao.builder().columnNames(new ArrayList<>()).rows(new ArrayList<>()).build();
            }
            Row headerRow = sheet.getRow(0);
            List<String> columnNames = new ArrayList<>();
            for (Cell cell : headerRow) {
                columnNames.add(dataFormatter.formatCellValue(cell).trim());
            }
            List<ExcelPreviewRowDao> rows = new ArrayList<>();
            int lastRow = (maxRows > 0) ? Math.min(sheet.getLastRowNum(), maxRows) : sheet.getLastRowNum();
            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;
                Map<String, Object> data = new LinkedHashMap<>();
                for (int j = 0; j < columnNames.size(); j++) {
                    Cell cell = row.getCell(j);
                    data.put(columnNames.get(j), cell == null ? "" : dataFormatter.formatCellValue(cell));
                }
                rows.add(ExcelPreviewRowDao.builder().data(data).rowNum(i).build());
            }
            return ExcelPreviewDao.builder()
                    .columnNames(columnNames)
                    .rows(rows)
                    .build();
        }
    }

    public boolean checkIsValidFileFormat(String original) {
        String lower = original != null ? original.toLowerCase() : "";
        boolean isZip = lower.endsWith(".zip");
        boolean isExcel = lower.endsWith(".xlsx") || lower.endsWith(".xls");
        return isZip || isExcel;
    }

    public boolean isZipFile(String original) {
        String lower = original != null ? original.toLowerCase() : "";
        boolean isZip = lower.endsWith(".zip");
        return isZip;
    }

}

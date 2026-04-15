package com.damc.legalnotices.util;

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

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.excel.ExcelPreviewDto;
import com.damc.legalnotices.dto.excel.ExcelPreviewRowDto;
import com.damc.legalnotices.errors.StopParsingException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    private Path getUserBasePath(LoginUserDao  sessionUser) {
        return Paths.get(storageProperties.getUploadDir(), sessionUser.getId() + "");
    }

    public Path saveZipFile(LoginUserDao  sessionUser, MultipartFile zipFile) {
        try {
            String original = zipFile.getOriginalFilename();
            if (original == null || !original.toLowerCase().endsWith(".zip")) {
                throw new IllegalArgumentException("Only ZIP file upload is allowed");
            }
            String randomName = UUID.randomUUID().toString();
            Path uploadDir = Paths.get(getUserBasePath(sessionUser).toString(), randomName)
                    .toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            String fileName = randomName + "_" + original;
            Path targetPath = uploadDir.resolve(fileName);
            Files.copy(zipFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to save ZIP file: " + ex.getMessage());
        }
    }

    public Path saveExcelFile(LoginUserDao  sessionUser, MultipartFile excelFile) {
        try {
            String original = excelFile.getOriginalFilename();
            if (original == null) {
                throw new IllegalArgumentException("Excel file name is missing");
            }
            String lower = original.toLowerCase();
            if (!lower.endsWith(".xlsx") && !lower.endsWith(".xls")) {
                throw new IllegalArgumentException("Only Excel files (.xlsx, .xls) are allowed");
            }
            String randomName = UUID.randomUUID().toString();
            Path uploadDir = Paths.get(getUserBasePath(sessionUser).toString(), randomName).toAbsolutePath()
                    .normalize();
            Files.createDirectories(uploadDir);
            String fileName = randomName + "_" + original;
            Path targetPath = uploadDir.resolve(fileName);
            Files.copy(excelFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to save Excel file: " + ex.getMessage());
        }
    }

    public Path extractZip(Path zipPath) {
        try {
            Path targetDir = Paths.get(zipPath.getParent().toString(), "extracted")
                    .resolve(UUID.randomUUID().toString());
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

    public ExcelPreviewDto parseAsPreview(Path excelPath) throws IOException {
        return parseAsPreview(excelPath, storageProperties.getPreviewMaxRows());
    }

    private ExcelPreviewDto parseAsPreview(Path excelPath, int maxRows) throws IOException {
        log.info("Parsing Excel file: {} with max rows: {}", excelPath.getFileName().toString(), maxRows);
        String name = excelPath.getFileName().toString().toLowerCase();
        if (name.endsWith(".xls")) {
            return parseWithDom(excelPath, maxRows);
        }
        return parseXlsxStreaming(excelPath, maxRows);
    }

    private ExcelPreviewDto parseXlsxStreaming(Path excelPath, int maxRows) throws IOException {
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
        return handler.toDto();
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

    private ExcelPreviewDto parseWithDom(Path excelPath, int maxRows) throws IOException {
        try (InputStream is = Files.newInputStream(excelPath);
                Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                return ExcelPreviewDto.builder().columnNames(new ArrayList<>()).rows(new ArrayList<>()).build();
            }
            Row headerRow = sheet.getRow(0);
            List<String> columnNames = new ArrayList<>();
            for (Cell cell : headerRow) {
                columnNames.add(dataFormatter.formatCellValue(cell).trim());
            }
            List<ExcelPreviewRowDto> rows = new ArrayList<>();
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
                rows.add(ExcelPreviewRowDto.builder().data(data).rowNum(i).build());
            }
            return ExcelPreviewDto.builder()
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

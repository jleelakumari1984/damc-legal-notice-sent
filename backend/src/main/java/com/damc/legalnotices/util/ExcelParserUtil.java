package com.damc.legalnotices.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dto.ExcelPreviewDto;
import com.damc.legalnotices.dto.ExcelPreviewRowDto;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ExcelParserUtil {

    private final DataFormatter dataFormatter = new DataFormatter();
    private final LocationProperties storageProperties;
    private final ZipExtractorUtil zipExtractorUtil;

    public Path saveZipFile(MultipartFile zipFile) {
        try {
            String original = zipFile.getOriginalFilename();
            if (original == null || !original.toLowerCase().endsWith(".zip")) {
                throw new IllegalArgumentException("Only ZIP file upload is allowed");
            }
            String randomName = UUID.randomUUID().toString();
            Path uploadDir = Paths.get(storageProperties.getUploadDir(), randomName)
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

    public Path saveExcelFile(MultipartFile excelFile) {
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
            Path uploadDir = Paths.get(storageProperties.getUploadDir(), randomName).toAbsolutePath().normalize();
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
            Path targetDir = Paths.get(storageProperties.getUploadDir(), "extracted")
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
        try (InputStream is = Files.newInputStream(excelPath);
                Workbook workbook = new XSSFWorkbook(is)) {
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
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;
                Map<String, Object> data = new LinkedHashMap<>();
                for (int j = 0; j < columnNames.size(); j++) {
                    Cell cell = row.getCell(j);
                    data.put(columnNames.get(j), cell == null ? "" : dataFormatter.formatCellValue(cell));
                }
                rows.add(ExcelPreviewRowDto.builder().data(data).build());
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

package com.damc.legalnotices.service.impl;

import com.damc.legalnotices.dto.ExcelPreviewDto;
import com.damc.legalnotices.service.ExcelService;
import com.damc.legalnotices.util.ExcelParserUtil;
import com.damc.legalnotices.util.ZipExtractorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final ZipExtractorUtil zipExtractorUtil;
    private final ExcelParserUtil excelParserUtil;

    @Override
    public ExcelPreviewDto previewExcel(MultipartFile zipFile) {
        if (zipFile == null || zipFile.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        String original = zipFile.getOriginalFilename();
        if (!excelParserUtil.checkIsValidFileFormat(original)) {
            throw new IllegalArgumentException("Only ZIP (.zip) or Excel (.xlsx, .xls) files are allowed");
        }
        if (!excelParserUtil.isZipFile(original)) {
            Path tempExcel = null;
            try {
                tempExcel = Files.createTempFile("preview_", "_" + original);
                Files.copy(zipFile.getInputStream(), tempExcel, StandardCopyOption.REPLACE_EXISTING);
                return parseExcelPreview(tempExcel);
            } catch (IOException ex) {
                throw new IllegalArgumentException("Failed to preview Excel file: " + ex.getMessage());
            } finally {
                deleteSilently(tempExcel);
            }
        }

        Path tempZip = null;
        Path tempExtractDir = null;
        try {
            tempZip = Files.createTempFile("preview_", "_" + original);
            Files.copy(zipFile.getInputStream(), tempZip, StandardCopyOption.REPLACE_EXISTING);

            tempExtractDir = Files.createTempDirectory("preview_extract_");
            zipExtractorUtil.extract(tempZip, tempExtractDir);

            Path excelPath = findExcel(tempExtractDir);
            return parseExcelPreview(excelPath);

        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to preview Excel from ZIP: " + ex.getMessage());
        } finally {
            deleteSilently(tempZip);
            deleteSilently(tempExtractDir);
        }
    }

    @Override
    public ExcelPreviewDto parseExcelPreview(Path excelPath) {
        try {
            return excelParserUtil.parseAsPreview(excelPath);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to parse Excel file: " + ex.getMessage());
        }
    }

    private Path findExcel(Path extractedPath) {
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

    private void deleteSilently(Path path) {
        if (path == null)
            return;
        try {
            if (Files.isDirectory(path)) {
                try (var walk = Files.walk(path)) {
                    walk.sorted(java.util.Comparator.reverseOrder())
                            .forEach(p -> {
                                try {
                                    Files.deleteIfExists(p);
                                } catch (IOException ignored) {
                                }
                            });
                }
            } else {
                Files.deleteIfExists(path);
            }
        } catch (IOException ignored) {
        }
    }
}

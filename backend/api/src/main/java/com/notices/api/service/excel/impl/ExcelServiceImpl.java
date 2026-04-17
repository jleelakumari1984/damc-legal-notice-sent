package com.notices.api.service.excel.impl;

import com.notices.domain.dao.excel.ExcelPreviewDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.entity.master.MasterProcessTemplateDetailEntity;
import com.notices.domain.repository.master.MasterProcessTemplateDetailRepository;
import com.notices.api.dto.excel.ExcelPreviewDto;
import com.notices.api.service.excel.ExcelService;
import com.notices.domain.util.ExcelParserUtil;
import com.notices.domain.util.ZipExtractorUtil;
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

    private final MasterProcessTemplateDetailRepository noticeTemplateRepository;

    private final ZipExtractorUtil zipExtractorUtil;
    private final ExcelParserUtil excelParserUtil;

    @Override
    public ExcelPreviewDao previewExcel(LoginUserDao sessionUser, ExcelPreviewDto request) {

        MultipartFile zipFile = request.getZipFile();
        if (zipFile == null || zipFile.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        String original = zipFile.getOriginalFilename();
        if (!excelParserUtil.checkIsValidFileFormat(original)) {
            throw new IllegalArgumentException("Only ZIP (.zip) or Excel (.xlsx, .xls) files are allowed");
        }

        MasterProcessTemplateDetailEntity template = noticeTemplateRepository
                .findByIdWithExcelMappings(request.getNoticeSno())
                .orElseThrow(() -> new IllegalArgumentException("Invalid notice type"));

        if (template.getExcelMappings() == null || template.getExcelMappings().isEmpty()) {
            throw new IllegalArgumentException("Notice type is not properly configured with Excel mappings");
        }
        if (!excelParserUtil.isZipFile(original)) {
            Path tempExcel = null;
            try {
                tempExcel = Files.createTempFile("preview_" + sessionUser.getId() + "_", "_" + original);
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
            tempZip = Files.createTempFile("preview_" + sessionUser.getId() + "_", "_" + original);
            Files.copy(zipFile.getInputStream(), tempZip, StandardCopyOption.REPLACE_EXISTING);

            tempExtractDir = Files.createTempDirectory("preview_extract_" + sessionUser.getId() + "_");
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

    public ExcelPreviewDao parseExcelPreview(Path excelPath) {
        try {
            ExcelPreviewDao previewDao = excelParserUtil.parseAsPreview(excelPath);
            
            return previewDao;
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

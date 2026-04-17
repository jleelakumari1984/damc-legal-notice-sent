package com.notices.domain.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.notices.domain.dao.user.LoginUserDao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveExcelUtil {
    private final ExcelParserUtil excelParserUtil;
    private final FileUtil fileUtil;

    public Path saveZipFile(LoginUserDao sessionUser, MultipartFile zipFile) {
        try {
            String original = zipFile.getOriginalFilename();
            if (original == null || !original.toLowerCase().endsWith(".zip")) {
                throw new IllegalArgumentException("Only ZIP file upload is allowed");
            }
            String randomName = UUID.randomUUID().toString();
            Path uploadDir = Paths.get(excelParserUtil.getUserBasePath(sessionUser).toString(), randomName)
                    .toAbsolutePath().normalize();
            fileUtil.createDirectoriesIfNotExists(uploadDir);
            String fileName = randomName + "_" + original;
            Path targetPath = uploadDir.resolve(fileName);
            Files.copy(zipFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to save ZIP file: " + ex.getMessage());
        }
    }

    public Path saveExcelFile(LoginUserDao sessionUser, MultipartFile excelFile) {
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
            Path uploadDir = Paths.get(excelParserUtil.getUserBasePath(sessionUser).toString(), randomName)
                    .toAbsolutePath()
                    .normalize();
            fileUtil.createDirectoriesIfNotExists(uploadDir);
            String fileName = randomName + "_" + original;
            Path targetPath = uploadDir.resolve(fileName);
            Files.copy(excelFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to save Excel file: " + ex.getMessage());
        }
    }
}

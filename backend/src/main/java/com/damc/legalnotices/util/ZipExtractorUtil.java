package com.damc.legalnotices.util;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
@AllArgsConstructor
public class ZipExtractorUtil {

    private final FileUtil fileUtil;

    public void extract(Path zipFilePath, Path targetDir) throws IOException {
        fileUtil.createDirectoriesIfNotExists(targetDir);
        log.info("Extracting Zip File {}", zipFilePath.getFileName().toString());
        try (InputStream inputStream = Files.newInputStream(zipFilePath);
                ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path resolvedPath = targetDir.resolve(entry.getName()).normalize();
                if (!resolvedPath.startsWith(targetDir)) {
                    throw new IOException("Invalid zip entry: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    fileUtil.createDirectoriesIfNotExists(resolvedPath);
                } else {
                    fileUtil.createDirectoriesIfNotExists(resolvedPath.getParent());
                    Files.copy(zis, resolvedPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }
}

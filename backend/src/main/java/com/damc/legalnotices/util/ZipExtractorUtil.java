package com.damc.legalnotices.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ZipExtractorUtil {

    public void extract(Path zipFilePath, Path targetDir) throws IOException {
        Files.createDirectories(targetDir);

        try (InputStream inputStream = Files.newInputStream(zipFilePath);
             ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path resolvedPath = targetDir.resolve(entry.getName()).normalize();
                if (!resolvedPath.startsWith(targetDir)) {
                    throw new IOException("Invalid zip entry: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(resolvedPath);
                } else {
                    Files.createDirectories(resolvedPath.getParent());
                    Files.copy(zis, resolvedPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }
}

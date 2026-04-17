package com.damc.legalnotices.util;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileUtil {
    public void createDirectoriesIfNotExists(Path parent) {
        if (parent != null && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (Exception e) {
                log.error("Failed to create directories for path: {}", parent, e);
                throw new RuntimeException("Failed to create directories for path: " + parent, e);
            }
        }
    }

    public void writeString(Path userTemplatePath, String templateContent) {
        if (userTemplatePath != null && templateContent != null) {
            try {
                createDirectoriesIfNotExists(userTemplatePath.getParent());
                Files.writeString(userTemplatePath, templateContent);
            } catch (Exception e) {
                log.error("Failed to write string to file: {}", userTemplatePath, e);
                throw new RuntimeException("Failed to write string to file: " + userTemplatePath, e);
            }
        }
    }
}

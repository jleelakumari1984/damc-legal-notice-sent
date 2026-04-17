package com.damc.legalnotices.controller.notice;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.entity.schedule.ScheduledNoticeItemEntity;
import com.damc.legalnotices.repository.schedule.ScheduledNoticeItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/download")
@RequiredArgsConstructor
public class DownloadController {

    private final ScheduledNoticeItemRepository scheduledNoticeItemRepository;
    private final LocationProperties locationProperties;

    @GetMapping("/attachments/{scheduleItemId}/{fileName}")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long scheduleItemId,
            @PathVariable String fileName) {

        ScheduledNoticeItemEntity item = scheduledNoticeItemRepository.findById(scheduleItemId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule item not found: " + scheduleItemId));

        if (!isAttachmentInItem(item, fileName)) {
            log.warn("File '{}' not found in attachments for scheduleItemId={}", fileName, scheduleItemId);
            return ResponseEntity.notFound().build();
        }

        String extractedFolder = item.getScheduledNotice().getExtractedFolderPath();
        Path uploadDir = Paths.get(locationProperties.getUploadDir(), extractedFolder).toAbsolutePath().normalize();
        Path filePath = uploadDir.resolve(fileName).normalize();

        // Prevent path traversal - ensure resolved path is within upload dir
        Path basePath = uploadDir.toAbsolutePath().normalize();
        if (!filePath.toAbsolutePath().normalize().startsWith(basePath)) {
            log.warn("Path traversal attempt for scheduleItemId={}, fileName={}", scheduleItemId, fileName);
            return ResponseEntity.badRequest().build();
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                log.warn("File not found or not readable: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            String contentType = resolveContentType(fileName);
            log.info("Serving attachment scheduleItemId={}, file={}", scheduleItemId, fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);

        } catch (MalformedURLException ex) {
            log.error("Malformed file path for scheduleItemId={}, fileName={}", scheduleItemId, fileName, ex);
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean isAttachmentInItem(ScheduledNoticeItemEntity item, String fileName) {
        if (item.getAttachments() == null || item.getAttachments().isBlank()) {
            return false;
        }
        return java.util.Arrays.stream(item.getAttachments().split(";"))
                .map(String::trim)
                .anyMatch(fileName::equals);
    }

    private String resolveContentType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".pdf"))
            return "application/pdf";
        if (lower.endsWith(".png"))
            return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg"))
            return "image/jpeg";
        if (lower.endsWith(".xlsx"))
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (lower.endsWith(".xls"))
            return "application/vnd.ms-excel";
        return "application/octet-stream";
    }
}

package com.damc.legalnotices.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.damc.legalnotices.dao.notice.NoticeExcelMappingDao;
import com.damc.legalnotices.entity.master.MasterProcessExcelMappingEntity;
import com.damc.legalnotices.entity.schedule.ScheduledNoticeItemEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeScheduleUtil {
    private final ObjectMapper objectMapper;

    public List<String> getAttchementFiles(ScheduledNoticeItemEntity item,
            List<NoticeExcelMappingDao> attachmentColumns,
            Path zipExtractPath) {
        Map<String, Object> row = getExcelData(item);
        if (row == null) {
            return Collections.emptyList();
        }
        List<String> attachments = new ArrayList<>();
        for (NoticeExcelMappingDao attachmentColumn : attachmentColumns) {
            Object attachObj = row.get(attachmentColumn.getExcelFieldName());
            String attachFileName = attachObj != null ? attachObj.toString() : null;
            if (attachFileName != null && !attachFileName.isBlank()) {
                Path attachPath = zipExtractPath.resolve(attachFileName);
                if (attachPath.toFile().exists()) {
                    attachments.add(attachPath.toString());
                } else {
                    log.warn("Attachment file {} not found for item id {}", attachPath, item.getId());
                }
            }
        }
        return attachments;
    }

    public Map<String, Object> getMapProperties(ScheduledNoticeItemEntity item,
            List<MasterProcessExcelMappingEntity> excelMappings) {
        Map<String, Object> row = getExcelData(item);
        if (row == null) {
            return Collections.emptyMap();
        }
        return row.entrySet().stream()
                .filter(entry -> excelMappings.stream()
                        .anyMatch(mapping -> mapping.getExcelFieldName().equals(entry.getKey())))
                .collect(Collectors.toMap(
                        entry -> excelMappings.stream()
                                .filter(mapping -> mapping.getExcelFieldName().equals(entry.getKey()))
                                .map(MasterProcessExcelMappingEntity::getDbFieldName)
                                .findFirst()
                                .orElse(entry.getKey()),
                        Map.Entry::getValue));
    }

    public Map<String, Object> getExcelData(ScheduledNoticeItemEntity item) {
        Map<String, Object> row;
        try {
            row = objectMapper.readValue(item.getExcelData(), new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            log.warn("Failed to parse excelData for item id {}", item.getId(), ex);
            return null;
        }
        if (row == null) {
            return Collections.emptyMap();
        }
        return row;
    }

    public String getMobileNumber(ScheduledNoticeItemEntity item, List<NoticeExcelMappingDao> mobileColumns) {
        Map<String, Object> row = getExcelData(item);
        if (row == null) {
            return "";
        }
        for (NoticeExcelMappingDao mobileColumn : mobileColumns) {
            Object mobileObj = row.get(mobileColumn.getExcelFieldName());
            String mobile = mobileObj != null ? mobileObj.toString() : null;
            if (mobile != null && !mobile.isBlank()) {
                return mobile;
            }
        }
        return null;
    }

}

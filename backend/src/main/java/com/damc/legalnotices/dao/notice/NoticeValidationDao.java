package com.damc.legalnotices.dao.notice;

import com.damc.legalnotices.dto.notice.NoticeValidationFileDto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class NoticeValidationDao {
    private Long scheduleId;
    private String originalZipFile;
    private String extractedFolder;
    private String status;
    private NoticeValidationFileDto fileData;
}

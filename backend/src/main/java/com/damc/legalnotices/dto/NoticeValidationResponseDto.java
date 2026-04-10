package com.damc.legalnotices.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class NoticeValidationResponseDto {
    private Long scheduleId;
    private String originalZipFile;
    private String extractedFolder;
    private String status;
    private NoticeValidationFileDto fileData;
}

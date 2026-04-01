package com.damc.legalnotices.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NoticeValidationResponseDto {
    private Long scheduleId;
    private String originalZipFile;
    private String extractedFolder;
    private String status;
    private List<NoticeValidationRowDto> rows;
}

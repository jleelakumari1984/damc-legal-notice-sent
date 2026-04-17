package com.notices.domain.dto.notice;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeValidationFileDto {
    private List<String> columnNames;
    private List<NoticeValidationRowDto> rows;
}

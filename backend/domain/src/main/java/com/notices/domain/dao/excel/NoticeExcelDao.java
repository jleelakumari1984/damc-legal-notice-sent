package com.notices.domain.dao.excel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.notices.domain.dto.notice.NoticeValidationRowDto;

@Getter
@Setter
@Builder
public class NoticeExcelDao {
    private Long scheduledNoticeId;
    private String ExcelName;
    private List<NoticeValidationRowDto> validationRows;
    private String status;
    private String failureReason;
}

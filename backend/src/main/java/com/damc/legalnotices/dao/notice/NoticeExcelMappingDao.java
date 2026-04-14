package com.damc.legalnotices.dao.notice;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeExcelMappingDao {

    private Long id;

    private Long processId;

    private String excelFieldName;

    private String dbFieldName;

    private Integer isKey;

    private Integer isMobile;

    private Integer isMandatory;

    private Integer isAttachment;

    private LocalDateTime createdAt;
}

package com.damc.legalnotices.dao.notice;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessTemplateDao {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private List<NoticeExcelMappingDao> excelMap;
}

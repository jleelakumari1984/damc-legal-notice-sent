package com.damc.legalnotices.dto.report;

import com.damc.legalnotices.dto.DatatableDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeReportDto extends DatatableDto {
    private String status;

    @Override
    public String getSortColumn() {
        return super.getSortColumn() == null ? "createdAt" : super.getSortColumn();
    };
}

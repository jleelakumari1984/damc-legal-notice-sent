package com.damc.legalnotices.dao.report;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NoticeReportDetailDao {
    private NoticeReportDao summary;
    private List<NoticeReportItemDao> items;
}

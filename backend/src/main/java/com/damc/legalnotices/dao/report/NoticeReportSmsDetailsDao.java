package com.damc.legalnotices.dao.report;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

import com.damc.legalnotices.dao.notification.SendSmsDao; 

@Getter
@Builder
public class NoticeReportSmsDetailsDao {
    private NoticeReportDao summary;
    private List<SendSmsDao> items;
}

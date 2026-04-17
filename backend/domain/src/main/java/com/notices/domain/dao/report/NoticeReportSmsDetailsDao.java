package com.notices.domain.dao.report;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

import com.notices.domain.dao.notification.SendSmsDao; 

@Getter
@Builder
public class NoticeReportSmsDetailsDao {
    private NoticeReportDao summary;
    private List<SendSmsDao> items;
}

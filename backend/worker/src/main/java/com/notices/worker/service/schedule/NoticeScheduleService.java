package com.notices.worker.service.schedule;

import java.util.List;

import com.notices.domain.dao.excel.NoticeExcelDao;
import com.notices.domain.dao.excel.ScheduledNoticeItemDao;

public interface NoticeScheduleService {

        List<NoticeExcelDao> noticePendingExcelParsing();

        List<ScheduledNoticeItemDao> noticePendingNoticeItems();

}

package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.report.NoticeReportDetailDao;
import com.damc.legalnotices.dao.report.NoticeReportDao;

import java.util.List;

public interface NoticeReportService {

    List<NoticeReportDao> getAllNoticeReports();

    NoticeReportDetailDao getNoticeReportDetail(Long noticeId);
}

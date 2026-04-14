package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.report.NoticeReportDetailDao;
import com.damc.legalnotices.dao.report.NoticeReportSmsDetailsDao;
import com.damc.legalnotices.dao.report.NoticeReportWhatsappDetailsDao;
import com.damc.legalnotices.dto.report.NoticeReportDto;
import com.damc.legalnotices.dto.report.NoticeSmsLogReportDto;
import com.damc.legalnotices.dto.report.NoticeWhatsappLogReportDto;
import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.report.NoticeReportDao;

import java.util.List;

public interface NoticeReportService {

    DataTableDao<List<NoticeReportDao>> getAllNoticeReports(NoticeReportDto request);

    NoticeReportDetailDao getNoticeReportDetail(Long noticeId, String status);

    DataTableDao<NoticeReportSmsDetailsDao> getSmsLogs(Long noticeId, NoticeSmsLogReportDto request);

    DataTableDao<NoticeReportSmsDetailsDao> getSmsErrorLogs(Long id, NoticeSmsLogReportDto request);

    DataTableDao<NoticeReportWhatsappDetailsDao> getWhatsAppLogs(Long noticeId, NoticeWhatsappLogReportDto request);

    DataTableDao<NoticeReportWhatsappDetailsDao> getWhatsAppErrorLogs(Long id, NoticeWhatsappLogReportDto request);
}

package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.report.NoticeReportDetailDao;
import com.damc.legalnotices.dao.report.NoticeReportSmsDetailsDao;
import com.damc.legalnotices.dao.report.NoticeReportWhatsappDetailsDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.DatatableDto;
import com.damc.legalnotices.dto.report.NoticeReportDto;
import com.damc.legalnotices.dto.report.NoticeSmsLogListReportDto;
import com.damc.legalnotices.dto.report.NoticeWhatsappLogListReportDto;
import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.report.NoticeReportDao;

import java.util.List;

public interface NoticeReportService {

        DataTableDao<List<NoticeReportDao>> getAllNoticeReports(LoginUserDao sessionUser,
                        DatatableDto<NoticeReportDto> request);

        NoticeReportDetailDao getNoticeReportDetail(LoginUserDao sessionUser, Long noticeId, String status);

        DataTableDao<NoticeReportSmsDetailsDao> getSmsLogs(LoginUserDao sessionUser, Long noticeId,
                        DatatableDto<NoticeSmsLogListReportDto> request);

        DataTableDao<NoticeReportSmsDetailsDao> getSmsErrorLogs(LoginUserDao sessionUser, Long id,
                        DatatableDto<NoticeSmsLogListReportDto> request);

        DataTableDao<NoticeReportWhatsappDetailsDao> getWhatsAppLogs(LoginUserDao sessionUser, Long noticeId,
                        DatatableDto<NoticeWhatsappLogListReportDto> request);

        DataTableDao<NoticeReportWhatsappDetailsDao> getWhatsAppErrorLogs(LoginUserDao sessionUser, Long id,
                        DatatableDto<NoticeWhatsappLogListReportDto> request);
}

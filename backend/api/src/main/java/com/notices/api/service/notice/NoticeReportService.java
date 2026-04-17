package com.notices.api.service.notice;

import com.notices.domain.dao.report.NoticeReportDetailDao;
import com.notices.domain.dao.report.NoticeReportSmsDetailsDao;
import com.notices.domain.dao.report.NoticeReportWhatsappDetailsDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.DatatableDto;
import com.notices.domain.dto.report.NoticeReportDto;
import com.notices.domain.dto.report.NoticeSmsLogListReportDto;
import com.notices.domain.dto.report.NoticeWhatsappLogListReportDto;
import com.notices.domain.dao.DataTableDao;
import com.notices.domain.dao.report.NoticeReportDao;

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

package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.notice.NoticeTemplateReportDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.DatatableDto;
import com.damc.legalnotices.dto.notice.NoticeTypeDto;
import com.damc.legalnotices.dto.notice.NoticeTypesRequest;

import java.util.List;

public interface NoticeService {

        DataTableDao<List<NoticeTemplateReportDao>> getNoticeTypes(LoginUserDao sessionUser,
                        DatatableDto<NoticeTypesRequest> request);

        NoticeTemplateReportDao getNoticeTypesDetail(LoginUserDao sessionUser, Long id);

        NoticeTemplateReportDao createNoticeType(LoginUserDao sessionUser, NoticeTypeDto request);

        NoticeTemplateReportDao updateNoticeType(LoginUserDao sessionUser, Long id, NoticeTypeDto request);

        boolean noticeTypeNameExists(LoginUserDao sessionUser, String name, Long excludeId);

}

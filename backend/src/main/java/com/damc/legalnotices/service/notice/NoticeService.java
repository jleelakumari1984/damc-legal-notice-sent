package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.notice.ProcessTemplateReportDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeTypesRequest;

import java.util.List;

public interface NoticeService {

        DataTableDao<List<ProcessTemplateReportDao>> getNoticeTypes(LoginUserDao sessionUser,
                        NoticeTypesRequest request);

        ProcessTemplateReportDao getNoticeTypesDetail(LoginUserDao sessionUser, Long id);

}

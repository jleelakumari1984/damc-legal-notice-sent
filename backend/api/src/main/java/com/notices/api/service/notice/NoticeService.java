package com.notices.api.service.notice;

import com.notices.domain.dao.DataTableDao;
import com.notices.domain.dao.notice.NoticeTemplateReportDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.DatatableDto;
import com.notices.domain.dto.notice.NoticeTypeDto;
import com.notices.domain.dto.notice.NoticeTypesRequest;

import java.util.List;

public interface NoticeService {

        DataTableDao<List<NoticeTemplateReportDao>> getNoticeTypes(LoginUserDao sessionUser,
                        DatatableDto<NoticeTypesRequest> request);

        NoticeTemplateReportDao getNoticeTypesDetail(LoginUserDao sessionUser, Long id);

        NoticeTemplateReportDao createNoticeType(LoginUserDao sessionUser, NoticeTypeDto request);

        NoticeTemplateReportDao updateNoticeType(LoginUserDao sessionUser, Long id, NoticeTypeDto request);

        boolean noticeTypeNameExists(LoginUserDao sessionUser, String name, Long excludeId);

}

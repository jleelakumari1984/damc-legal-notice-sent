package com.notices.api.service.notice;

import com.notices.domain.dao.DataTableDao;
import com.notices.domain.dao.notice.SmsPendingTemplateDao;
import com.notices.domain.dao.notice.SmsTemplateDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.DatatableDto;
import com.notices.domain.dto.notice.NoticeSmsPendingDto;

import java.util.List;

public interface NoticeSmsMappingAdminService {

    List<SmsTemplateDao> getByNoticeId(LoginUserDao sessionUser, Long noticeId, Boolean status);

    SmsTemplateDao getById(LoginUserDao sessionUser, Long id);

    DataTableDao<List<SmsPendingTemplateDao>> getPendingTemplates(LoginUserDao sessionUser,
            DatatableDto<NoticeSmsPendingDto> request);

}

package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.notice.SmsPendingTemplateDao;
import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.DatatableDto;
import com.damc.legalnotices.dto.notice.NoticeSmsPendingDto;

import java.util.List;

public interface NoticeSmsMappingAdminService {

    List<SmsTemplateDao> getByNoticeId(LoginUserDao sessionUser, Long noticeId, Boolean status);

    SmsTemplateDao getById(LoginUserDao sessionUser, Long id);

    DataTableDao<List<SmsPendingTemplateDao>> getPendingTemplates(LoginUserDao sessionUser,
            DatatableDto<NoticeSmsPendingDto> request);

}

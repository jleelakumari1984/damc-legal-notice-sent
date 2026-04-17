package com.notices.api.service.notice;

import com.notices.domain.dao.DataTableDao;
import com.notices.domain.dao.notice.WhatsAppPendingTemplateDao;
import com.notices.domain.dao.notice.WhatsAppTemplateDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.DatatableDto;
import com.notices.domain.dto.notice.NoticeWhatsAppPendingDto;

import java.util.List;

public interface NoticeWhatsappMappingAdminService {

    List<WhatsAppTemplateDao> getByNoticeId(LoginUserDao sessionUser, Long noticeId, Boolean status);

    WhatsAppTemplateDao getById(LoginUserDao sessionUser, Long id);

    DataTableDao<List<WhatsAppPendingTemplateDao>> getPendingTemplates(LoginUserDao sessionUser,
            DatatableDto<NoticeWhatsAppPendingDto> request);
}

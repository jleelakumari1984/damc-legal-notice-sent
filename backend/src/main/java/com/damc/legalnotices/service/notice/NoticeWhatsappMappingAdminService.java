package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.notice.WhatsAppPendingTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.DatatableDto;
import com.damc.legalnotices.dto.notice.NoticeWhatsAppPendingDto;

import java.util.List;

public interface NoticeWhatsappMappingAdminService {

    List<WhatsAppTemplateDao> getByNoticeId(LoginUserDao sessionUser, Long noticeId, Boolean status);

    WhatsAppTemplateDao getById(LoginUserDao sessionUser, Long id);

    DataTableDao<List<WhatsAppPendingTemplateDao>> getPendingTemplates(LoginUserDao sessionUser,
            DatatableDto<NoticeWhatsAppPendingDto> request);
}

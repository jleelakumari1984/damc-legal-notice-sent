package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeWhatsappConfigDto;

import java.util.List;

public interface NoticeWhatsappMappingAdminService {

    List<WhatsAppTemplateDao> getByProcessId(LoginUserDao sessionUser, Long processId);

    WhatsAppTemplateDao getById(LoginUserDao sessionUser, Long id);

    WhatsAppTemplateDao create(LoginUserDao sessionUser, NoticeWhatsappConfigDto request) throws Exception;

    WhatsAppTemplateDao update(LoginUserDao sessionUser, Long id, NoticeWhatsappConfigDto request) throws Exception;

    void delete(LoginUserDao sessionUser, Long id);
}

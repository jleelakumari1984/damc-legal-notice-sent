package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.notice.WhatsAppUserTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeWhatsappConfigDto;

public interface NoticeWhatsappMappingUserService {

    WhatsAppUserTemplateDao create(LoginUserDao sessionUser, NoticeWhatsappConfigDto request) throws Exception;

    WhatsAppUserTemplateDao update(LoginUserDao sessionUser, Long id, NoticeWhatsappConfigDto request) throws Exception;
}

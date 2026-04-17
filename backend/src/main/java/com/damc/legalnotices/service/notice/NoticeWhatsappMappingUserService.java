package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.notice.WhatsAppUserTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeWhatsAppConfigDto;

public interface NoticeWhatsappMappingUserService {

    WhatsAppUserTemplateDao create(LoginUserDao sessionUser, NoticeWhatsAppConfigDto request) throws Exception;

    WhatsAppUserTemplateDao update(LoginUserDao sessionUser, Long id, NoticeWhatsAppConfigDto request) throws Exception;
}

package com.notices.api.service.notice;

import com.notices.domain.dao.notice.WhatsAppUserTemplateDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.notice.NoticeWhatsAppConfigDto;

public interface NoticeWhatsappMappingUserService {

    WhatsAppUserTemplateDao create(LoginUserDao sessionUser, NoticeWhatsAppConfigDto request) throws Exception;

    WhatsAppUserTemplateDao update(LoginUserDao sessionUser, Long id, NoticeWhatsAppConfigDto request) throws Exception;
}

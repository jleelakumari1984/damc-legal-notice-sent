package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.notice.SmsUserTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeSmsConfigDto;

public interface NoticeSmsMappingUserService {

    SmsUserTemplateDao create(LoginUserDao sessionUser, NoticeSmsConfigDto request) throws Exception;

    SmsUserTemplateDao update(LoginUserDao sessionUser, Long id, NoticeSmsConfigDto request) throws Exception;
}

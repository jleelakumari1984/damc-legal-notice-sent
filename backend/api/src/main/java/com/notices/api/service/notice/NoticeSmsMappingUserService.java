package com.notices.api.service.notice;

import com.notices.domain.dao.notice.SmsUserTemplateDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.notice.NoticeSmsConfigDto;

public interface NoticeSmsMappingUserService {

    SmsUserTemplateDao create(LoginUserDao sessionUser, NoticeSmsConfigDto request) throws Exception;

    SmsUserTemplateDao update(LoginUserDao sessionUser, Long id, NoticeSmsConfigDto request) throws Exception;
}

package com.notices.api.service.notice;

import com.notices.domain.dao.notice.SmsTemplateDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.notice.NoticeSmsApproveDto;
import com.notices.domain.dto.notice.NoticeSmsRejectDto;

public interface NoticeSmsApprovalService {

    SmsTemplateDao approve(LoginUserDao sessionUser, Long id, NoticeSmsApproveDto dto) throws Exception;

    SmsTemplateDao toggleStatus(LoginUserDao sessionUser, Long id);

    SmsTemplateDao reject(LoginUserDao sessionUser, Long id, NoticeSmsRejectDto request);
}

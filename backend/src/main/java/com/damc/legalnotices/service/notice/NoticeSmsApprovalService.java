package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeSmsApproveDto;
import com.damc.legalnotices.dto.notice.NoticeSmsRejectDto;

public interface NoticeSmsApprovalService {

    SmsTemplateDao approve(LoginUserDao sessionUser, Long id, NoticeSmsApproveDto dto);

    SmsTemplateDao toggleStatus(LoginUserDao sessionUser, Long id);

    SmsTemplateDao reject(LoginUserDao sessionUser, Long id, NoticeSmsRejectDto request);
}

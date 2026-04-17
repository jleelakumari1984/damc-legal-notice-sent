package com.notices.api.service.notice;

import com.notices.domain.dao.notice.WhatsAppTemplateDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.notice.NoticeWhatsAppApproveDto;
import com.notices.domain.dto.notice.NoticeWhatsAppRejectDto;


public interface NoticeWhatsappApprovalService {

    WhatsAppTemplateDao approve(LoginUserDao sessionUser, Long id, NoticeWhatsAppApproveDto dto) throws Exception;

    WhatsAppTemplateDao toggleStatus(LoginUserDao sessionUser, Long id);

    WhatsAppTemplateDao reject(LoginUserDao sessionUser, Long id, NoticeWhatsAppRejectDto request);
}

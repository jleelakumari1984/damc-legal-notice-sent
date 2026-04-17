package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeWhatsAppApproveDto;
import com.damc.legalnotices.dto.notice.NoticeWhatsAppRejectDto;


public interface NoticeWhatsappApprovalService {

    WhatsAppTemplateDao approve(LoginUserDao sessionUser, Long id, NoticeWhatsAppApproveDto dto) throws Exception;

    WhatsAppTemplateDao toggleStatus(LoginUserDao sessionUser, Long id);

    WhatsAppTemplateDao reject(LoginUserDao sessionUser, Long id, NoticeWhatsAppRejectDto request);
}

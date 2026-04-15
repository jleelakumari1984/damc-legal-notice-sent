package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeWhatsappApproveDto;
import com.damc.legalnotices.dto.notice.NoticeWhatsappRejectDto;


public interface NoticeWhatsappApprovalService {

    WhatsAppTemplateDao approve(LoginUserDao sessionUser, Long id, NoticeWhatsappApproveDto dto);

    WhatsAppTemplateDao toggleStatus(LoginUserDao sessionUser, Long id);

    WhatsAppTemplateDao reject(LoginUserDao sessionUser, Long id, NoticeWhatsappRejectDto request);
}

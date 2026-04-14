package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.notice.ProcessTemplateDao;
import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;

import java.util.List;

public interface NoticeService {

        List<ProcessTemplateDao> getNoticeTypes();

        ProcessTemplateDao getNoticeTypesDetail(Long id);

        List<SmsTemplateDao> getSmsTemplates(Long processId);

        List<WhatsAppTemplateDao> getWhatsAppTemplates(Long processId);

}

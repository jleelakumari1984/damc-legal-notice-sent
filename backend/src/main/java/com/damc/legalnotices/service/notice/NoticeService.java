package com.damc.legalnotices.service.notice;

 
import com.damc.legalnotices.dao.notice.ProcessTemplateReportDao;
import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;

import java.util.List;

public interface NoticeService {

        List<ProcessTemplateReportDao> getNoticeTypes();

        ProcessTemplateReportDao getNoticeTypesDetail(Long id);

        List<SmsTemplateDao> getSmsTemplates(Long processId);

        List<WhatsAppTemplateDao> getWhatsAppTemplates(Long processId);

}

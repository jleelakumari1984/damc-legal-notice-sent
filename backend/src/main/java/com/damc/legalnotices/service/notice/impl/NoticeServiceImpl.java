package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.dao.notice.ProcessTemplateReportDao;
import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.repository.master.MasterProcessSmsConfigDetailRepository;
import com.damc.legalnotices.repository.master.MasterProcessWhatsappConfigDetailRepository;
import com.damc.legalnotices.repository.view.ProcessConfigReportViewRepository;
import com.damc.legalnotices.service.notice.NoticeService;
import com.damc.legalnotices.util.EntityDaoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final MasterProcessSmsConfigDetailRepository smsConfigRepository;
    private final MasterProcessWhatsappConfigDetailRepository whatsappConfigRepository;
    private final ProcessConfigReportViewRepository processConfigReportViewRepository;

    private final EntityDaoConverter entityDaoConverter;

    @Override
    public List<ProcessTemplateReportDao> getNoticeTypes() {
        return processConfigReportViewRepository.findAll().stream()
                .map(entityDaoConverter::toProcessTemplateReportDao)
                .toList();
    }

    @Override
    public ProcessTemplateReportDao getNoticeTypesDetail(Long id) {
        return processConfigReportViewRepository.findById(id)
                .map(entityDaoConverter::toProcessTemplateReportDao)
                .orElseThrow(() -> new IllegalArgumentException("Notice type not found with id: " + id));
    }

    @Override
    public List<SmsTemplateDao> getSmsTemplates(Long processId) {
        return smsConfigRepository.findByProcessIdAndStatus(processId, 1).stream()
                .map(entityDaoConverter::toSmsTemplateDao)
                .toList();
    }

    @Override
    public List<WhatsAppTemplateDao> getWhatsAppTemplates(Long processId) {
        return whatsappConfigRepository.findByProcessIdAndStatus(processId, 1).stream()
                .map(entityDaoConverter::toWhatsAppTemplateDao)
                .toList();
    }

 

}

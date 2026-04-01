package com.damc.legalnotices.service.impl;

import com.damc.legalnotices.entity.MasterProcessWhatsappConfigDetail;
import com.damc.legalnotices.entity.ScheduledNoticeItem;
import com.damc.legalnotices.service.WhatsappSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WhatsappSenderServiceImpl implements WhatsappSenderService {

    @Override
    public void send(ScheduledNoticeItem item, MasterProcessWhatsappConfigDetail config, String attachmentPath) {
        log.info("Sending WhatsApp for agreement {} to {} using template {} attachment {}",
                item.getAgreementNumber(), item.getMobileWhatsapp(), config.getTemplateName(), attachmentPath);
    }
}

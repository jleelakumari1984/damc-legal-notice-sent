package com.damc.legalnotices.service.impl;

import com.damc.legalnotices.entity.MasterProcessSmsConfigDetail;
import com.damc.legalnotices.entity.ScheduledNoticeItem;
import com.damc.legalnotices.service.SmsSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsSenderServiceImpl implements SmsSenderService {

    @Override
    public void send(ScheduledNoticeItem item, MasterProcessSmsConfigDetail config, String attachmentPath) {
        log.info("Sending SMS for agreement {} to {} using template {} attachment {}",
                item.getAgreementNumber(), item.getMobileSms(), config.getTemplateId(), attachmentPath);
    }
}

package com.damc.legalnotices.service.notification;

import com.damc.legalnotices.config.SmsCredential;
import com.damc.legalnotices.dto.notification.SmsDataDto;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;

public interface SmsSenderService {
    void send(SmsDataDto item, MasterProcessTemplateDetailEntity template, SmsCredential credential);
}

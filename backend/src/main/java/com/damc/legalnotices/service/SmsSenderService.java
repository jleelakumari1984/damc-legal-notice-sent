package com.damc.legalnotices.service;

import com.damc.legalnotices.dto.SmsDataDto;
import com.damc.legalnotices.entity.MasterProcessTemplateDetailEntity;

public interface SmsSenderService {
    void send(SmsDataDto item, MasterProcessTemplateDetailEntity template);
}

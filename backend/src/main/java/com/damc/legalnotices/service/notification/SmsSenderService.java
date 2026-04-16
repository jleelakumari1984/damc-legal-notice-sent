package com.damc.legalnotices.service.notification;

import com.damc.legalnotices.dao.user.UserSmsCredentialDao;
import com.damc.legalnotices.dto.notification.SmsDataDto;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;

public interface SmsSenderService {
    void send(SmsDataDto item, MasterProcessTemplateDetailEntity template, UserSmsCredentialDao credential);
}

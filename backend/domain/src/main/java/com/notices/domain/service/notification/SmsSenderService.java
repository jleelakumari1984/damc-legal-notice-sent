package com.notices.domain.service.notification;

import com.notices.domain.dao.user.UserSmsCredentialDao;
import com.notices.domain.dto.notification.SmsDataDto;
import com.notices.domain.entity.master.MasterProcessTemplateDetailEntity;

public interface SmsSenderService {
    void send(SmsDataDto item, MasterProcessTemplateDetailEntity template, UserSmsCredentialDao credential);
}

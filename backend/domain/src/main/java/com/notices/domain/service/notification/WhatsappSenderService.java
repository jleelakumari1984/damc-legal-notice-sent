package com.notices.domain.service.notification;

import java.util.List;

import com.notices.domain.dao.user.UserWhatsAppCredentialDao;
import com.notices.domain.dto.notification.WhatsAppDataDto;
import com.notices.domain.entity.master.MasterProcessTemplateDetailEntity;

public interface WhatsappSenderService {
    void send(WhatsAppDataDto item, MasterProcessTemplateDetailEntity template, List<String> attachments,
            UserWhatsAppCredentialDao credential);
}

package com.damc.legalnotices.service.notification;

import java.util.List;

import com.damc.legalnotices.config.WhatsAppCredential;
import com.damc.legalnotices.dto.notification.WhatsAppDataDto;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;

public interface WhatsappSenderService {
    void send(WhatsAppDataDto item, MasterProcessTemplateDetailEntity template, List<String> attachments,
            WhatsAppCredential credential);
}

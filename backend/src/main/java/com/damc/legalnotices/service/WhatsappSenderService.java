package com.damc.legalnotices.service;

import java.util.List;

import com.damc.legalnotices.dto.WhatsAppDataDto;
import com.damc.legalnotices.entity.MasterProcessTemplateDetailEntity;

public interface WhatsappSenderService {
    void send(WhatsAppDataDto item, MasterProcessTemplateDetailEntity template, List<String> attachments);
}

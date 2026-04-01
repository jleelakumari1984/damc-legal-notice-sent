package com.damc.legalnotices.service;

import com.damc.legalnotices.entity.MasterProcessWhatsappConfigDetail;
import com.damc.legalnotices.entity.ScheduledNoticeItem;

public interface WhatsappSenderService {
    void send(ScheduledNoticeItem item, MasterProcessWhatsappConfigDetail config, String attachmentPath);
}

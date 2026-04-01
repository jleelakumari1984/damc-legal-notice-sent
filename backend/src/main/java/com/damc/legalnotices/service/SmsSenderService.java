package com.damc.legalnotices.service;

import com.damc.legalnotices.entity.MasterProcessSmsConfigDetail;
import com.damc.legalnotices.entity.ScheduledNoticeItem;

public interface SmsSenderService {
    void send(ScheduledNoticeItem item, MasterProcessSmsConfigDetail config, String attachmentPath);
}

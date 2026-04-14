package com.damc.legalnotices.service.notification;

import com.damc.legalnotices.dto.notification.SmsDataDto;
import com.damc.legalnotices.dto.notification.WhatsAppDataDto;

public interface NotificationsSaveService {
    public void StoreSmsDetails(String sendType, SmsDataDto smsData, boolean success,
            String response, boolean schedule, Exception ex);

    public void StoreWhatsAppDetails(String sendType, WhatsAppDataDto whatsappData, boolean success,
            String response, boolean schedule, Exception ex);

}

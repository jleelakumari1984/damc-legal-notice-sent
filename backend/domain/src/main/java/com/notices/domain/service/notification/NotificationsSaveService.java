package com.notices.domain.service.notification;

import com.notices.domain.dto.notification.SmsDataDto;
import com.notices.domain.dto.notification.WhatsAppDataDto;

public interface NotificationsSaveService {
        
    public void StoreSmsDetails(String sendType, SmsDataDto smsData, boolean success,
            String response, boolean schedule, Exception ex);

    public void StoreWhatsAppDetails(String sendType, WhatsAppDataDto whatsappData, boolean success,
            String response, boolean schedule, Exception ex);

}

package com.notices.domain.service.notification;

import com.notices.domain.entity.notification.StatusReportWhatsappEntity;

public interface WhatsAppSaveStatusService {
    
    StatusReportWhatsappEntity saveData(String requestParams, String requestBody);

    StatusReportWhatsappEntity saveData(StatusReportWhatsappEntity entity);

}

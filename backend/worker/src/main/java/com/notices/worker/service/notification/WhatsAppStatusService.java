package com.notices.worker.service.notification;

import com.notices.domain.entity.notification.StatusReportWhatsappEntity;

import java.util.List;

public interface WhatsAppStatusService {

    
    List<StatusReportWhatsappEntity> getAll();

    StatusReportWhatsappEntity getById(Long id);


}

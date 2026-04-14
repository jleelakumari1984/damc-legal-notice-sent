package com.damc.legalnotices.service.notification;

import com.damc.legalnotices.entity.notification.StatusReportWhatsappEntity;

import java.util.List;

public interface WhatsAppStatusService {

    StatusReportWhatsappEntity saveData(String requestParams, String requestBody);

    List<StatusReportWhatsappEntity> getAll();

    StatusReportWhatsappEntity getById(Long id);

    StatusReportWhatsappEntity saveData(StatusReportWhatsappEntity entity);

}

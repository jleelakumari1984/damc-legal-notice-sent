package com.damc.legalnotices.service;

import com.damc.legalnotices.entity.StatusReportWhatsappEntity;

import java.util.List;

public interface WhatsAppStatusService {

    void saveData(String requestParams, String requestBody);

    List<StatusReportWhatsappEntity> getAll();

    StatusReportWhatsappEntity getById(Long id);
}

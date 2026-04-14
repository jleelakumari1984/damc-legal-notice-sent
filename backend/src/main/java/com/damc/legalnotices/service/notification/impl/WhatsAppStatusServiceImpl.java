package com.damc.legalnotices.service.notification.impl;

import com.damc.legalnotices.entity.notification.StatusReportWhatsappEntity;
import com.damc.legalnotices.repository.notification.StatusReportWhatsappRepository;
import com.damc.legalnotices.service.notification.WhatsAppStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WhatsAppStatusServiceImpl implements WhatsAppStatusService {

    private final StatusReportWhatsappRepository whatsappRepository;

    @Override
    public StatusReportWhatsappEntity saveData(String requestParams, String requestBody) {
        StatusReportWhatsappEntity entity = new StatusReportWhatsappEntity();
        entity.setRequestParams(requestParams);
        entity.setRequestBody(requestBody);
        return whatsappRepository.save(entity);
    }

    @Override
    public List<StatusReportWhatsappEntity> getAll() {
        return whatsappRepository.findAll();
    }

    @Override
    public StatusReportWhatsappEntity getById(Long id) {
        return whatsappRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp report not found: " + id));
    }

    @Override
    public StatusReportWhatsappEntity saveData(StatusReportWhatsappEntity entity) {
        return whatsappRepository.save(entity);

    }
}

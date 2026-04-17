package com.notices.domain.service.notification.impl;

import org.springframework.stereotype.Service;

import com.notices.domain.entity.notification.StatusReportWhatsappEntity;
import com.notices.domain.repository.notification.StatusReportWhatsappRepository;
import com.notices.domain.service.notification.WhatsAppSaveStatusService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WhatsAppSaveStatusServiceImpl implements WhatsAppSaveStatusService {

    private final StatusReportWhatsappRepository whatsappRepository;

    @Override
    public StatusReportWhatsappEntity saveData(String requestParams, String requestBody) {
        StatusReportWhatsappEntity entity = new StatusReportWhatsappEntity();
        entity.setRequestParams(requestParams);
        entity.setRequestBody(requestBody);
        return whatsappRepository.save(entity);
    }

    @Override
    public StatusReportWhatsappEntity saveData(StatusReportWhatsappEntity entity) {
        return whatsappRepository.save(entity);

    }
}

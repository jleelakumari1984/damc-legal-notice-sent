package com.damc.legalnotices.service.notification.impl;

import com.damc.legalnotices.entity.notification.StatusReportSmsEntity;
import com.damc.legalnotices.repository.notification.StatusReportSmsRepository;
import com.damc.legalnotices.service.notification.SmsStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsStatusServiceImpl implements SmsStatusService {

    private final StatusReportSmsRepository smsRepository;

    @Override
    public StatusReportSmsEntity saveData(String requestParams, String requestBody) {
        StatusReportSmsEntity entity = new StatusReportSmsEntity();
        entity.setRequestParams(requestParams);
        entity.setRequestBody(requestBody);
        return smsRepository.save(entity);
    }

    @Override
    public List<StatusReportSmsEntity> getAll() {
        return smsRepository.findAll();
    }

    @Override
    public StatusReportSmsEntity getById(Long id) {
        return smsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SMS report not found: " + id));
    }

    @Override
    public StatusReportSmsEntity saveData(StatusReportSmsEntity entity) {
        return smsRepository.save(entity);
    }


}

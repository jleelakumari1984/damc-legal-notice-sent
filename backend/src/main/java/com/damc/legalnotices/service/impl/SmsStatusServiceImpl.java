package com.damc.legalnotices.service.impl;

import com.damc.legalnotices.entity.StatusReportSmsEntity;
import com.damc.legalnotices.repository.StatusReportSmsRepository;
import com.damc.legalnotices.service.SmsStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsStatusServiceImpl implements SmsStatusService {

    private final StatusReportSmsRepository smsRepository;

    @Override
    public void saveData(String requestParams, String requestBody) {
        StatusReportSmsEntity entity = new StatusReportSmsEntity();
        entity.setRequestParams(requestParams);
        entity.setRequestBody(requestBody);
        smsRepository.save(entity);
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
}

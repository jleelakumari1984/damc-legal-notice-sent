package com.notices.domain.service.notification.impl;

import org.springframework.stereotype.Service;

import com.notices.domain.entity.notification.StatusReportSmsEntity;
import com.notices.domain.repository.notification.StatusReportSmsRepository;
import com.notices.domain.service.notification.SmsSaveStatusService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class SmsSaveStatusServiceImpl implements SmsSaveStatusService {

    private final StatusReportSmsRepository smsRepository;

    @Override
    public StatusReportSmsEntity saveData(String requestParams, String requestBody) {
        StatusReportSmsEntity entity = new StatusReportSmsEntity();
        entity.setRequestParams(requestParams);
        entity.setRequestBody(requestBody);
        return smsRepository.save(entity);
    }

    @Override
    public StatusReportSmsEntity saveData(StatusReportSmsEntity entity) {
        return smsRepository.save(entity);
    }

}

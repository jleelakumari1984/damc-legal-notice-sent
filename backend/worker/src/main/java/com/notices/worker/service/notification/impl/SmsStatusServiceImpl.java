package com.notices.worker.service.notification.impl;

import com.notices.domain.entity.notification.StatusReportSmsEntity;
import com.notices.domain.repository.notification.StatusReportSmsRepository;
import com.notices.worker.service.notification.SmsStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsStatusServiceImpl implements SmsStatusService {

    private final StatusReportSmsRepository smsRepository;

   
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

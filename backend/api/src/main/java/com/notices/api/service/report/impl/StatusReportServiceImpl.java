package com.notices.api.service.report.impl;

import com.notices.api.service.report.StatusReportService;
import com.notices.domain.entity.notification.StatusReportSmsEntity;
import com.notices.domain.entity.notification.StatusReportWhatsappEntity;
import com.notices.domain.repository.notification.StatusReportSmsRepository;
import com.notices.domain.repository.notification.StatusReportWhatsappRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusReportServiceImpl implements StatusReportService {

    private final StatusReportSmsRepository smsRepository;
    private final StatusReportWhatsappRepository whatsappRepository;

    @Override
    public StatusReportSmsEntity saveSmsReport(StatusReportSmsEntity entity) {
        return smsRepository.save(entity);
    }

    @Override
    public StatusReportWhatsappEntity saveWhatsappReport(StatusReportWhatsappEntity entity) {
        return whatsappRepository.save(entity);
    }

    @Override
    public List<StatusReportSmsEntity> getAllSmsReports() {
        return smsRepository.findAll();
    }

    @Override
    public List<StatusReportWhatsappEntity> getAllWhatsappReports() {
        return whatsappRepository.findAll();
    }

    @Override
    public StatusReportSmsEntity getSmsReport(Long id) {
        return smsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SMS report not found: " + id));
    }

    @Override
    public StatusReportWhatsappEntity getWhatsappReport(Long id) {
        return whatsappRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp report not found: " + id));
    }
}

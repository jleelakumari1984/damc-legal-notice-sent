package com.damc.legalnotices.service.report.impl;

import com.damc.legalnotices.entity.notification.StatusReportSmsEntity;
import com.damc.legalnotices.entity.notification.StatusReportWhatsappEntity;
import com.damc.legalnotices.repository.notification.StatusReportSmsRepository;
import com.damc.legalnotices.repository.notification.StatusReportWhatsappRepository;
import com.damc.legalnotices.service.report.StatusReportService;

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

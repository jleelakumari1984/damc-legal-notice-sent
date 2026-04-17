package com.notices.worker.service.notification.impl;

import com.notices.domain.entity.notification.StatusReportWhatsappEntity;
import com.notices.domain.repository.notification.StatusReportWhatsappRepository;
import com.notices.worker.service.notification.WhatsAppStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WhatsAppStatusServiceImpl implements WhatsAppStatusService {

    private final StatusReportWhatsappRepository whatsappRepository;

  
    @Override
    public List<StatusReportWhatsappEntity> getAll() {
        return whatsappRepository.findAll();
    }

    @Override
    public StatusReportWhatsappEntity getById(Long id) {
        return whatsappRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp report not found: " + id));
    }

 
}

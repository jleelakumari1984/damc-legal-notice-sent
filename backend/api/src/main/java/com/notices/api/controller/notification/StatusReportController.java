package com.notices.api.controller.notification;

import com.notices.domain.entity.notification.StatusReportSmsEntity;
import com.notices.domain.entity.notification.StatusReportWhatsappEntity;
import com.notices.api.service.report.StatusReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/status-report")
@RequiredArgsConstructor
public class StatusReportController {

    private final StatusReportService statusReportService;

    // SMS CRUD endpoints
    @GetMapping("/sms")
    public ResponseEntity<List<StatusReportSmsEntity>> getAllSmsReports() {
        return ResponseEntity.ok(statusReportService.getAllSmsReports());
    }

    @GetMapping("/sms/{id}")
    public ResponseEntity<StatusReportSmsEntity> getSmsReport(@PathVariable Long id) {
        return ResponseEntity.ok(statusReportService.getSmsReport(id));
    }

    @PostMapping("/sms")
    public ResponseEntity<StatusReportSmsEntity> saveSmsReport(@RequestBody StatusReportSmsEntity entity) {
        return ResponseEntity.ok(statusReportService.saveSmsReport(entity));
    }

    // WhatsApp CRUD endpoints
    @GetMapping("/whatsapp")
    public ResponseEntity<List<StatusReportWhatsappEntity>> getAllWhatsappReports() {
        return ResponseEntity.ok(statusReportService.getAllWhatsappReports());
    }

    @GetMapping("/whatsapp/{id}")
    public ResponseEntity<StatusReportWhatsappEntity> getWhatsappReport(@PathVariable Long id) {
        return ResponseEntity.ok(statusReportService.getWhatsappReport(id));
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<StatusReportWhatsappEntity> saveWhatsappReport(@RequestBody StatusReportWhatsappEntity entity) {
        return ResponseEntity.ok(statusReportService.saveWhatsappReport(entity));
    }
}


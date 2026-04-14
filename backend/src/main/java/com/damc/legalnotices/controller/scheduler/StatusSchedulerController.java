package com.damc.legalnotices.controller.scheduler;

import com.damc.legalnotices.dao.notification.StatusReportSmsDao;
import com.damc.legalnotices.dao.notification.StatusReportWhatsappDao;
import com.damc.legalnotices.service.notification.NotificationCallbackService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/scheduler/status")
@RequiredArgsConstructor
public class StatusSchedulerController {

    private final NotificationCallbackService callbackService;
    // ─── Re-parse missed ack_ids ───────────────────────────────────────────────

    /**
     * POST: Re-parse send_response for SMS records where ack_id was not captured at
     * send time.
     * Returns count of records updated.
     */
    @GetMapping("/pending-sms")
    public ResponseEntity<List<StatusReportSmsDao>> processPendingSmsParsing() {
        return ResponseEntity.ok(callbackService.processPendingSmsParsing());
    }

    /**
     * POST: Re-parse send_response for WhatsApp records where ack_id was not
     * captured at send time.
     * Returns count of records updated.
     */
    @GetMapping("/pending-whatsapp")
    public ResponseEntity<List<StatusReportWhatsappDao>> processPendingWhatsAppParsing() {
        return ResponseEntity.ok(callbackService.processPendingWhatsAppParsing());
    }
}

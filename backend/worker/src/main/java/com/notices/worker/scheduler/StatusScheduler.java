package com.notices.worker.scheduler;

import com.notices.domain.service.notification.NotificationCallbackService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusScheduler {

    private final NotificationCallbackService callbackService;

    // ─── Re-parse missed ack_ids ───────────────────────────────────────────────

    @Scheduled(fixedDelayString = "${app.scheduler.fixed-delay-ms}")
    public void noticePendingSmsParsing() {
        log.info("Running scheduled status processor for pending SMS ack_ids");
        callbackService.noticePendingSmsParsing();
    }

    @Scheduled(fixedDelayString = "${app.scheduler.fixed-delay-ms}")
    public void noticePendingWhatsAppParsing() {
        log.info("Running scheduled status processor for pending WhatsApp ack_ids");
        callbackService.noticePendingWhatsAppParsing();
    }
}

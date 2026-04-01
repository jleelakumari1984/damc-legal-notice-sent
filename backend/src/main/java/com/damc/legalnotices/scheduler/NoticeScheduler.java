package com.damc.legalnotices.scheduler;

import com.damc.legalnotices.service.NoticeProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeScheduler {

    private final NoticeProcessingService noticeProcessingService;

    @Scheduled(fixedDelayString = "${app.scheduler.fixed-delay-ms:60000}")
    public void executePendingNotices() {
        log.info("Running scheduled notice processor");
        noticeProcessingService.processPendingNotices();
    }
}

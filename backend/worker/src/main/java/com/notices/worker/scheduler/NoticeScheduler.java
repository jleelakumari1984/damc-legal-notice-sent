package com.notices.worker.scheduler;

import com.notices.worker.service.schedule.NoticeScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeScheduler {

    private final NoticeScheduleService scheduleService;

    @Scheduled(fixedDelayString = "${app.scheduler.fixed-delay-ms}")
    public void executePendingExcelParsing() {
        log.info("Running scheduled notice processor for pending Excel parsing");
        scheduleService.noticePendingExcelParsing();
    }

    @Scheduled(fixedDelayString = "${app.scheduler.fixed-delay-ms}")
    public void executePendingNoticeItemsProcessing() {
        log.info("Running scheduled notice processor for pending notice items");
        scheduleService.noticePendingNoticeItems();
    }
}

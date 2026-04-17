package com.damc.legalnotices.controller.scheduler;

import com.damc.legalnotices.dao.excel.NoticeExcelDao;
import com.damc.legalnotices.dao.excel.ScheduledNoticeItemDao;
import com.damc.legalnotices.service.schedule.NoticeScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/scheduler/notices")
@RequiredArgsConstructor
public class NoticeSchedulerController {

    private final NoticeScheduleService scheduleService;

    @GetMapping("/pending-excel")
    public ResponseEntity<List<NoticeExcelDao>> executePendingExcelParsing() {
        log.info("Running scheduled notice noticeor for pending Excel parsing");
        return ResponseEntity.ok(scheduleService.noticePendingExcelParsing());
    }

    @GetMapping("/pending-notice-items")
    public ResponseEntity<List<ScheduledNoticeItemDao>> executePendingNoticeItemsNoticeing() {
        log.info("Running scheduled notice noticeor for pending notice items");
        return ResponseEntity.ok(scheduleService.noticePendingNoticeItems());
    }
}

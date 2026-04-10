package com.damc.legalnotices.controller.scheduler;

import com.damc.legalnotices.dao.ProcessedExcelDao;
import com.damc.legalnotices.dao.ProcessedNoticeItemDao;
import com.damc.legalnotices.service.NoticeProcessingService;
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

    private final NoticeProcessingService noticeProcessingService;

    @GetMapping("/pending-excel")
    public ResponseEntity<List<ProcessedExcelDao>> executePendingExcelParsing() {
        log.info("Running scheduled notice processor for pending Excel parsing");
        return ResponseEntity.ok(noticeProcessingService.processPendingExcelParsing());
    }

    @GetMapping("/pending-notice-items")
    public ResponseEntity<List<ProcessedNoticeItemDao>> executePendingNoticeItemsProcessing() {
        log.info("Running scheduled notice processor for pending notice items");
        return ResponseEntity.ok(noticeProcessingService.processPendingNoticeItems());
    }
}

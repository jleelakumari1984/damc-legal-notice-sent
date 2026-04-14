package com.damc.legalnotices.controller.report;

import com.damc.legalnotices.dao.report.NoticeReportDetailDao;
import com.damc.legalnotices.dao.report.NoticeReportDao;
import com.damc.legalnotices.service.notice.NoticeReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reports/notices")
@RequiredArgsConstructor
public class NoticeReportController {

    private final NoticeReportService noticeReportService;

    @GetMapping
    public ResponseEntity<List<NoticeReportDao>> getAllNoticeReports() {
        return ResponseEntity.ok(noticeReportService.getAllNoticeReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeReportDetailDao> getNoticeReportDetail(@PathVariable Long id) {
        return ResponseEntity.ok(noticeReportService.getNoticeReportDetail(id));
    }
}

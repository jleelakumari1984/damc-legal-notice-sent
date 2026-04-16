package com.damc.legalnotices.controller.report;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.notification.SendSmsDao;
import com.damc.legalnotices.dao.notification.SendWhatsappDao;
import com.damc.legalnotices.dao.report.NoticeReportDetailDao;
import com.damc.legalnotices.dao.report.NoticeReportSmsDetailsDao;
import com.damc.legalnotices.dao.report.NoticeReportWhatsappDetailsDao;
import com.damc.legalnotices.dto.DatatableDto;
import com.damc.legalnotices.dto.report.NoticeReportDto;
import com.damc.legalnotices.dto.report.NoticeSmsLogListReportDto;
import com.damc.legalnotices.dto.report.NoticeWhatsappLogListReportDto;
import com.damc.legalnotices.dao.report.NoticeReportDao;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.notice.NoticeReportService;
import com.damc.legalnotices.util.CsvExportUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reports/notices")
@RequiredArgsConstructor
public class NoticeReportController {

    private final NoticeReportService noticeReportService;
    private final CsvExportUtil csvExportUtil;
    private final BaseService baseService;

    @PostMapping
    public ResponseEntity<DataTableDao<List<NoticeReportDao>>> getAllNoticeReports(
            @Valid @RequestBody DatatableDto<NoticeReportDto> request) {
        return ResponseEntity.ok(noticeReportService.getAllNoticeReports(baseService.getSessionUser(), request));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<NoticeReportDetailDao> getNoticeReportDetail(
            @PathVariable Long id,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(noticeReportService.getNoticeReportDetail(baseService.getSessionUser(), id, status));
    }

    @PostMapping("/sms-logs/{id}")
    public ResponseEntity<DataTableDao<NoticeReportSmsDetailsDao>> getSmsLogs(@PathVariable Long id,
            @Valid @RequestBody DatatableDto<NoticeSmsLogListReportDto> request) {
        request.setAllData(false);
        return ResponseEntity.ok(noticeReportService.getSmsLogs(baseService.getSessionUser(), id, request));
    }

    @PostMapping("/error-sms-logs/{id}")
    public ResponseEntity<DataTableDao<NoticeReportSmsDetailsDao>> getSmsErrorLogs(@PathVariable Long id,
            @Valid @RequestBody DatatableDto<NoticeSmsLogListReportDto> request) {
        request.setAllData(false);
        return ResponseEntity.ok(noticeReportService.getSmsErrorLogs(baseService.getSessionUser(), id, request));
    }

    @PostMapping("/whats-app-logs/{id}")
    public ResponseEntity<DataTableDao<NoticeReportWhatsappDetailsDao>> getWhatsAppLogs(@PathVariable Long id,
            @Valid @RequestBody DatatableDto<NoticeWhatsappLogListReportDto> request) {
        request.setAllData(false);
        return ResponseEntity.ok(noticeReportService.getWhatsAppLogs(baseService.getSessionUser(), id, request));
    }

    @PostMapping("/error-whats-app-logs/{id}")
    public ResponseEntity<DataTableDao<NoticeReportWhatsappDetailsDao>> getWhatsAppErrorLogs(@PathVariable Long id,
            @Valid @RequestBody DatatableDto<NoticeWhatsappLogListReportDto> request) {
        request.setAllData(false);
        return ResponseEntity.ok(noticeReportService.getWhatsAppErrorLogs(baseService.getSessionUser(), id, request));
    }

    // ─── CSV Downloads ────────────────────────────────────────────────────────

    @PostMapping("/download-sms-logs/{id}")
    public ResponseEntity<byte[]> downloadSmsLogs(@PathVariable Long id,
            @Valid @RequestBody DatatableDto<NoticeSmsLogListReportDto> request) {
        request.setAllData(true);
        List<SendSmsDao> items = noticeReportService.getSmsLogs(baseService.getSessionUser(), id, request).getData()
                .getItems();
        byte[] csv = csvExportUtil.buildSmsCsv(items, false);
        return csvResponse(csv, "sms-logs-" + id + ".csv");
    }

    @PostMapping("/download-error-sms-logs/{id}")
    public ResponseEntity<byte[]> downloadErrorSmsLogs(@PathVariable Long id,
            @Valid @RequestBody DatatableDto<NoticeSmsLogListReportDto> request) {
        request.setAllData(true);
        List<SendSmsDao> items = noticeReportService.getSmsErrorLogs(baseService.getSessionUser(), id, request)
                .getData().getItems();
        byte[] csv = csvExportUtil.buildSmsCsv(items, true);
        return csvResponse(csv, "error-sms-logs-" + id + ".csv");
    }

    @PostMapping("/download-whats-app-logs/{id}")
    public ResponseEntity<byte[]> downloadWhatsAppLogs(@PathVariable Long id,
            @Valid @RequestBody DatatableDto<NoticeWhatsappLogListReportDto> request) {
        request.setAllData(true);
        List<SendWhatsappDao> items = noticeReportService.getWhatsAppLogs(baseService.getSessionUser(), id, request)
                .getData().getItems();
        byte[] csv = csvExportUtil.buildWhatsappCsv(items, false);
        return csvResponse(csv, "whatsapp-logs-" + id + ".csv");
    }

    @PostMapping("/download-error-whats-app-logs/{id}")
    public ResponseEntity<byte[]> downloadErrorWhatsAppLogs(@PathVariable Long id,
            @Valid @RequestBody DatatableDto<NoticeWhatsappLogListReportDto> request) {
        request.setAllData(true);
        List<SendWhatsappDao> items = noticeReportService
                .getWhatsAppErrorLogs(baseService.getSessionUser(), id, request).getData().getItems();
        byte[] csv = csvExportUtil.buildWhatsappCsv(items, true);
        return csvResponse(csv, "error-whatsapp-logs-" + id + ".csv");
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private ResponseEntity<byte[]> csvResponse(byte[] csv, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        headers.setContentLength(csv.length);
        return ResponseEntity.ok().headers(headers).body(csv);
    }
}

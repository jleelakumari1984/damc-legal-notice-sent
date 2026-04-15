package com.damc.legalnotices.controller.notice;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.notice.NoticeValidationDao;
import com.damc.legalnotices.dao.notice.ProcessTemplateReportDao;
import com.damc.legalnotices.dto.notice.NoticeTypesRequest;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.notice.NoticeService;
import com.damc.legalnotices.service.schedule.NoticeScheduleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final BaseService baseService;
    private final NoticeService noticeService;
    private final NoticeScheduleService noticeScheduleService;

    @GetMapping("/types")
    public ResponseEntity<DataTableDao<List<ProcessTemplateReportDao>>> getNoticeTypes() {
        return ResponseEntity.ok(noticeService.getNoticeTypes(baseService.getSessionUser(), null));
    }

    @PostMapping("/types")
    public ResponseEntity<DataTableDao<List<ProcessTemplateReportDao>>> postNoticeTypes(
            @Valid @RequestBody NoticeTypesRequest request) {
        return ResponseEntity.ok(noticeService.getNoticeTypes(baseService.getSessionUser(), request));
    }

    @GetMapping("/types/{id}")
    public ResponseEntity<ProcessTemplateReportDao> getNoticeTypesDetail(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getNoticeTypesDetail(baseService.getSessionUser(), id));
    }

    @PostMapping(value = "/schedule", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeValidationDao> schedule(@RequestParam("processSno") Long processSno,
            @RequestParam("sendSms") Boolean sendSms,
            @RequestParam("sendWhatsapp") Boolean sendWhatsapp,
            @RequestParam("zipFile") MultipartFile zipFile) {
        log.info("Schedule notice request by user: {}, processSno: {}, sendSms: {}, sendWhatsapp: {}, file: {}",
                baseService.getSessionUser().getDisplayName(), processSno, sendSms, sendWhatsapp,
                zipFile != null ? zipFile.getOriginalFilename() : "null");
        return ResponseEntity.ok(noticeScheduleService.scheduleNotice(baseService.getSessionUser(),
                processSno,
                sendSms,
                sendWhatsapp,
                zipFile));
    }
}

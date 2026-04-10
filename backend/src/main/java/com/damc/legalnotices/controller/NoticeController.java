package com.damc.legalnotices.controller;

import com.damc.legalnotices.dao.ProcessTemplateDao;
import com.damc.legalnotices.dao.ScheduledNoticeDao;
import com.damc.legalnotices.dao.ScheduledNoticeDetailDao;
import com.damc.legalnotices.dto.NoticeValidationResponseDto;
import com.damc.legalnotices.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    private final NoticeService noticeService;

    @GetMapping("/types")
    public ResponseEntity<List<ProcessTemplateDao>> getNoticeTypes() {
        return ResponseEntity.ok(noticeService.getNoticeTypes());
    }

    @GetMapping
    public ResponseEntity<List<ScheduledNoticeDao>> getScheduledNotices() {
        return ResponseEntity.ok(noticeService.getScheduledNotices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduledNoticeDetailDao> getScheduledNoticeDetail(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getScheduledNoticeDetail(id));
    }

    @PostMapping(value = "/schedule", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeValidationResponseDto> schedule(@RequestParam("processSno") Long processSno,
                                                                @RequestParam("sendSms") Boolean sendSms,
                                                                @RequestParam("sendWhatsapp") Boolean sendWhatsapp,
                                                                @RequestParam("zipFile") MultipartFile zipFile,
                                                                Authentication authentication) {
        log.info("Schedule notice request by user: {}, processSno: {}, sendSms: {}, sendWhatsapp: {}, file: {}",
                authentication.getName(), processSno, sendSms, sendWhatsapp,
                zipFile != null ? zipFile.getOriginalFilename() : "null");
        return ResponseEntity.ok(noticeService.scheduleNotice(
                processSno,
                sendSms,
                sendWhatsapp,
                zipFile,
                authentication.getName()
        ));
    }
}

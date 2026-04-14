package com.damc.legalnotices.controller.notice;

import com.damc.legalnotices.dao.notice.NoticeValidationDao;
import com.damc.legalnotices.dao.notice.ProcessTemplateDao;
import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.notice.NoticeService;
import com.damc.legalnotices.service.schedule.NoticeScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    private final BaseService   baseService;
    private final NoticeService noticeService;
    private final NoticeScheduleService noticeScheduleService;

    @GetMapping("/types")
    public ResponseEntity<List<ProcessTemplateDao>> getNoticeTypes() {
        return ResponseEntity.ok(noticeService.getNoticeTypes());
    }

    @GetMapping("/types/{id}")
    public ResponseEntity<ProcessTemplateDao> getNoticeTypesDetail(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getNoticeTypesDetail(id));
    }

    @GetMapping("/types/{id}/sms-templates")
    public ResponseEntity<List<SmsTemplateDao>> getSmsTemplates(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getSmsTemplates(id));
    }

    @GetMapping("/types/{id}/whatsapp-templates")
    public ResponseEntity<List<WhatsAppTemplateDao>> getWhatsAppTemplates(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getWhatsAppTemplates(id));
    }

    @PostMapping(value = "/schedule", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeValidationDao> schedule(@RequestParam("processSno") Long processSno,
            @RequestParam("sendSms") Boolean sendSms,
            @RequestParam("sendWhatsapp") Boolean sendWhatsapp,
            @RequestParam("zipFile") MultipartFile zipFile ) {
        log.info("Schedule notice request by user: {}, processSno: {}, sendSms: {}, sendWhatsapp: {}, file: {}",
                baseService.getSessionUser().getUsername(), processSno, sendSms, sendWhatsapp,
                zipFile != null ? zipFile.getOriginalFilename() : "null");
        return ResponseEntity.ok(noticeScheduleService.scheduleNotice(
                processSno,
                sendSms,
                sendWhatsapp,
                zipFile,
                baseService.getSessionUser()));
    }
}

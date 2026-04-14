package com.damc.legalnotices.controller.notice;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.service.notice.NoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notice-mappings/sms")
@RequiredArgsConstructor
public class NoticeSmsMappingController {
    
    private final NoticeService noticeService;

    @GetMapping("/types/{id}/sms-templates")
    public ResponseEntity<List<SmsTemplateDao>> getSmsTemplates(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getSmsTemplates(id));
    }
}

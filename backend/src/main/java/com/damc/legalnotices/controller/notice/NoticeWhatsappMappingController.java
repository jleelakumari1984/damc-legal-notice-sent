package com.damc.legalnotices.controller.notice;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.service.notice.NoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notice-mappings/whatsapp")
@RequiredArgsConstructor
public class NoticeWhatsappMappingController {

    private final NoticeService noticeService;

    @GetMapping("/{id}")
    public ResponseEntity<List<WhatsAppTemplateDao>> getWhatsAppTemplates(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getWhatsAppTemplates(id));
    }
}

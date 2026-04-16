package com.damc.legalnotices.controller.notice;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.notice.WhatsAppPendingTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppUserTemplateDao;
import com.damc.legalnotices.dto.DatatableDto;
import com.damc.legalnotices.dto.notice.NoticeWhatsappApproveDto;
import com.damc.legalnotices.dto.notice.NoticeWhatsappConfigDto;
import com.damc.legalnotices.dto.notice.NoticeWhatsappPendingDto;
import com.damc.legalnotices.dto.notice.NoticeWhatsappRejectDto;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.notice.NoticeWhatsappMappingAdminService;
import com.damc.legalnotices.service.notice.NoticeWhatsappMappingUserService;
import com.damc.legalnotices.service.notice.NoticeWhatsappApprovalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notice-mappings/whatsapp")
@RequiredArgsConstructor
public class NoticeWhatsappMappingController {

    private final BaseService baseService;
    private final NoticeWhatsappMappingAdminService adminService;
    private final NoticeWhatsappMappingUserService userService;
    private final NoticeWhatsappApprovalService approvalService;

    @GetMapping("/list")
    public ResponseEntity<List<WhatsAppTemplateDao>> getByNoticeId(@RequestParam Long noticeId) {
        return ResponseEntity.ok(adminService.getByNoticeId(baseService.getSessionUser(), noticeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WhatsAppTemplateDao> getById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getById(baseService.getSessionUser(), id));
    }

    @PostMapping("/pending")
    public ResponseEntity<DataTableDao<List<WhatsAppPendingTemplateDao>>> getPendingTemplates(
            @Valid @RequestBody DatatableDto<NoticeWhatsappPendingDto> request) {
        return ResponseEntity.ok(adminService.getPendingTemplates(baseService.getSessionUser(), request));
    }

    @PostMapping
    public ResponseEntity<WhatsAppUserTemplateDao> createAdmin(@Valid @RequestBody NoticeWhatsappConfigDto request)
            throws Exception {
        return ResponseEntity.ok(userService.create(baseService.getSessionUser(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WhatsAppUserTemplateDao> updateAdmin(@PathVariable Long id,
            @Valid @RequestBody NoticeWhatsappConfigDto request) throws Exception {
        return ResponseEntity.ok(userService.update(baseService.getSessionUser(), id, request));

    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<WhatsAppTemplateDao> approve(@PathVariable Long id,
            @Valid @RequestBody NoticeWhatsappApproveDto request) {
        return ResponseEntity.ok(approvalService.approve(baseService.getSessionUser(), id, request));
    }

    @PatchMapping("/reject/{id}")
    public ResponseEntity<WhatsAppTemplateDao> reject(@PathVariable Long id,
            @Valid @RequestBody NoticeWhatsappRejectDto request) {
        return ResponseEntity.ok(approvalService.reject(baseService.getSessionUser(), id, request));
    }

    @PatchMapping("/toggle-status/{id}")
    public ResponseEntity<WhatsAppTemplateDao> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(
                approvalService.toggleStatus(baseService.getSessionUser(), id));
    }

    /*
     * @DeleteMapping("/{id}")
     * public ResponseEntity<Void> delete(@PathVariable Long id) {
     * adminService.delete(baseService.getSessionUser(), id);
     * return ResponseEntity.noContent().build();
     * }
     */
}

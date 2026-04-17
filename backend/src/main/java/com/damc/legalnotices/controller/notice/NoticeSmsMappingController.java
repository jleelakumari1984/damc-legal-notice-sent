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
import com.damc.legalnotices.dao.notice.SmsPendingTemplateDao;
import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.notice.SmsUserTemplateDao;
import com.damc.legalnotices.dto.DatatableDto;
import com.damc.legalnotices.dto.notice.NoticeSmsApproveDto;
import com.damc.legalnotices.dto.notice.NoticeSmsConfigDto;
import com.damc.legalnotices.dto.notice.NoticeSmsPendingDto;
import com.damc.legalnotices.dto.notice.NoticeSmsRejectDto;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.notice.NoticeSmsMappingAdminService;
import com.damc.legalnotices.service.notice.NoticeSmsMappingUserService;
import com.damc.legalnotices.service.notice.NoticeSmsApprovalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notice-mappings/sms")
@RequiredArgsConstructor
public class NoticeSmsMappingController {

    private final BaseService baseService;
    private final NoticeSmsMappingAdminService adminService;
    private final NoticeSmsMappingUserService userService;
    private final NoticeSmsApprovalService approvalService;

    @GetMapping("/list")
    public ResponseEntity<List<SmsTemplateDao>> getByNoticeId(@RequestParam Long noticeId,
            @RequestParam Boolean status) {

        return ResponseEntity.ok(adminService.getByNoticeId(baseService.getSessionUser(), noticeId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SmsTemplateDao> getById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getById(baseService.getSessionUser(), id));
    }

    @PostMapping("/pending")
    public ResponseEntity<DataTableDao<List<SmsPendingTemplateDao>>> getPendingTemplates(
            @Valid @RequestBody DatatableDto<NoticeSmsPendingDto> request) {
        return ResponseEntity.ok(adminService.getPendingTemplates(baseService.getSessionUser(), request));
    }

    @PostMapping
    public ResponseEntity<SmsUserTemplateDao> createAdmin(@Valid @RequestBody NoticeSmsConfigDto request)
            throws Exception {
        return ResponseEntity.ok(userService.create(baseService.getSessionUser(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SmsUserTemplateDao> updateAdmin(@PathVariable Long id,
            @Valid @RequestBody NoticeSmsConfigDto request) throws Exception {
        return ResponseEntity.ok(userService.update(baseService.getSessionUser(), id, request));
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<SmsTemplateDao> approve(@PathVariable Long id,
            @Valid @RequestBody NoticeSmsApproveDto request) throws Exception {
        return ResponseEntity.ok(approvalService.approve(baseService.getSessionUser(), id, request));
    }

    @PatchMapping("/reject/{id}")
    public ResponseEntity<SmsTemplateDao> reject(@PathVariable Long id,
            @Valid @RequestBody NoticeSmsRejectDto request) {
        return ResponseEntity.ok(approvalService.reject(baseService.getSessionUser(), id, request));
    }

    @PatchMapping("/toggle-status/{id}")
    public ResponseEntity<SmsTemplateDao> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(approvalService.toggleStatus(baseService.getSessionUser(), id));
    }
}

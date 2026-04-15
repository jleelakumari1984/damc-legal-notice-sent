package com.damc.legalnotices.controller.notice;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.notice.SmsUserTemplateDao;
import com.damc.legalnotices.dto.notice.NoticeSmsConfigDto;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.notice.NoticeSmsMappingAdminService;
import com.damc.legalnotices.service.notice.NoticeSmsMappingUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notice-mappings/sms")
@RequiredArgsConstructor
public class NoticeSmsMappingController {

    private final BaseService baseService;
    private final NoticeSmsMappingAdminService adminService;
    private final NoticeSmsMappingUserService userService;

    @GetMapping
    public ResponseEntity<List<SmsTemplateDao>> getByProcessId(@RequestParam Long processId) {

        return ResponseEntity.ok(adminService.getByProcessId(baseService.getSessionUser(), processId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SmsTemplateDao> getById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getById(baseService.getSessionUser(), id));
    }

    @PostMapping
    public ResponseEntity<SmsUserTemplateDao> createAdmin(@Valid @RequestBody NoticeSmsConfigDto request)
            throws Exception {

        if (!baseService.getSessionUser().isSuperAdmin()) {
            return ResponseEntity.ok(userService.create(baseService.getSessionUser(), request));
        }
        return ResponseEntity.ok(adminService.create(baseService.getSessionUser(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SmsUserTemplateDao> updateAdmin(@PathVariable Long id,
            @Valid @RequestBody NoticeSmsConfigDto request) throws Exception {

        if (!baseService.getSessionUser().isSuperAdmin()) {
            return ResponseEntity.ok(userService.update(baseService.getSessionUser(), id, request));
        }
        return ResponseEntity.ok(adminService.update(baseService.getSessionUser(), id, request));
    }
}

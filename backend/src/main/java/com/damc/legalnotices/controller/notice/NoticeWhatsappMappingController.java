package com.damc.legalnotices.controller.notice;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppUserTemplateDao;
import com.damc.legalnotices.dto.notice.NoticeWhatsappConfigDto;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.notice.NoticeWhatsappMappingAdminService;
import com.damc.legalnotices.service.notice.NoticeWhatsappMappingUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notice-mappings/whatsapp")
@RequiredArgsConstructor
public class NoticeWhatsappMappingController {

    private final BaseService baseService;
    private final NoticeWhatsappMappingAdminService adminService;
    private final NoticeWhatsappMappingUserService userService;

    @GetMapping
    public ResponseEntity<List<WhatsAppTemplateDao>> getByProcessId(@RequestParam Long processId) {
        return ResponseEntity.ok(adminService.getByProcessId(baseService.getSessionUser(), processId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WhatsAppTemplateDao> getById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getById(baseService.getSessionUser(), id));
    }

    @PostMapping
    public ResponseEntity<WhatsAppUserTemplateDao> createAdmin(@Valid @RequestBody NoticeWhatsappConfigDto request)
            throws Exception {

        if (!baseService.getSessionUser().isSuperAdmin()) {
            return ResponseEntity.ok(userService.create(baseService.getSessionUser(), request));
        }
        return ResponseEntity.ok(adminService.create(baseService.getSessionUser(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WhatsAppUserTemplateDao> updateAdmin(@PathVariable Long id,
            @Valid @RequestBody NoticeWhatsappConfigDto request) throws Exception {
        if (!baseService.getSessionUser().isSuperAdmin()) {
            return ResponseEntity.ok(userService.update(baseService.getSessionUser(), id, request));
        }
        return ResponseEntity.ok(adminService.update(baseService.getSessionUser(), id, request));
    }

    /*
     * @DeleteMapping("/{id}")
     * public ResponseEntity<Void> delete(@PathVariable Long id) {
     * adminService.delete(baseService.getSessionUser(), id);
     * return ResponseEntity.noContent().build();
     * }
     */
}

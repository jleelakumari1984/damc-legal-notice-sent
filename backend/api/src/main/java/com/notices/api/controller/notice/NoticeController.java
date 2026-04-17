package com.notices.api.controller.notice;

import com.notices.domain.dao.DataTableDao;
import com.notices.domain.dao.notice.NoticeValidationDao;
import com.notices.domain.dao.notice.NoticeTemplateReportDao;
import com.notices.domain.dto.DatatableDto;
import com.notices.domain.dto.notice.NoticeNameCheckDto;
import com.notices.domain.dto.notice.NoticeTypeDto;
import com.notices.domain.dto.notice.NoticeTypesRequest;
import com.notices.api.dto.notice.NoticeScheduleRequestDto;
import com.notices.api.service.BaseService;
import com.notices.api.service.notice.NoticeService;
import com.notices.api.service.schedule.ScheduleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final BaseService baseService;
    private final NoticeService noticeService;
    private final ScheduleService noticeScheduleService;

    @PostMapping("/types/list")
    public ResponseEntity<DataTableDao<List<NoticeTemplateReportDao>>> getListNoticeTypes(
            @Valid @RequestBody DatatableDto<NoticeTypesRequest> request) {
        return ResponseEntity.ok(noticeService.getNoticeTypes(baseService.getSessionUser(), request));
    }

    @PostMapping("/types/list/me")
    public ResponseEntity<DataTableDao<List<NoticeTemplateReportDao>>> getListNoticeTypesMe(
            @Valid @RequestBody DatatableDto<NoticeTypesRequest> request) {
        request.getFilter().setUserId(baseService.getSessionUser().getId());
        return ResponseEntity.ok(noticeService.getNoticeTypes(baseService.getSessionUser(), request));
    }

    @GetMapping("/types/{id}")
    public ResponseEntity<NoticeTemplateReportDao> getNoticeTypesDetail(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getNoticeTypesDetail(baseService.getSessionUser(), id));
    }

    @PostMapping("/types/create")
    public ResponseEntity<NoticeTemplateReportDao> createNoticeType(@Valid @RequestBody NoticeTypeDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noticeService.createNoticeType(baseService.getSessionUser(), request));
    }

    @PutMapping("/types/{id}")
    public ResponseEntity<NoticeTemplateReportDao> updateNoticeType(@PathVariable Long id,
            @Valid @RequestBody NoticeTypeDto request) {
        return ResponseEntity.ok(noticeService.updateNoticeType(baseService.getSessionUser(), id, request));
    }

    @PostMapping("/types/exists")
    public ResponseEntity<Map<String, Boolean>> noticeTypeNameExists(
            @Valid @RequestBody NoticeNameCheckDto request) {
        boolean exists = noticeService.noticeTypeNameExists(baseService.getSessionUser(), request.getName(),
                request.getExcludeId());
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping(value = "/schedule", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeValidationDao> schedule(@Valid @ModelAttribute NoticeScheduleRequestDto request) {
        log.info("Schedule notice request by user: {}, noticeSno: {}, sendSms: {}, sendWhatsapp: {}, file: {}",
                baseService.getSessionUser().getDisplayName(), request.getNoticeSno(), request.getSendSms(),
                request.getSendWhatsapp(),
                request.getZipFile() != null ? request.getZipFile().getOriginalFilename() : "null");
        return ResponseEntity.ok(noticeScheduleService.saveScheduleNotice(baseService.getSessionUser(),
                request));
    }

}

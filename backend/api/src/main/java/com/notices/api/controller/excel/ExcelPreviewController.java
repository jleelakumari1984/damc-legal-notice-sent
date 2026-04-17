package com.notices.api.controller.excel;

import com.notices.domain.dao.excel.ExcelPreviewDao;
import com.notices.domain.dto.notice.SendSampleNoticeDto;
import com.notices.api.dto.excel.ExcelPreviewDto;
import com.notices.api.service.BaseService;
import com.notices.api.service.excel.ExcelService;
import com.notices.api.service.schedule.ScheduleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/excel-preview")
@RequiredArgsConstructor
public class ExcelPreviewController {

    private final ExcelService excelService;
    private final ScheduleService noticeSchedule;
    private final BaseService baseService;

    @PostMapping(value = "/zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExcelPreviewDao> previewZip(
            @Valid @ModelAttribute ExcelPreviewDto request) {
        log.info("Excel preview request for file: {}",
                request.getZipFile() != null ? request.getZipFile().getOriginalFilename() : "null");
        return ResponseEntity.ok(excelService.previewExcel(baseService.getSessionUser(), request));
    }

    @PostMapping("/send-sample")
    public ResponseEntity<Void> sendSample(@RequestBody SendSampleNoticeDto request) {
        log.info("Send sample notice request for noticeSno: {}, mobile: {}", request.getNoticeSno(),
                request.getMobileNumber());
        noticeSchedule.sendSampleNotice(baseService.getSessionUser(), request);
        return ResponseEntity.ok().build();
    }
}

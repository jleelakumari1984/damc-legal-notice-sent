package com.damc.legalnotices.controller.excel;

import com.damc.legalnotices.dao.excel.ExcelPreviewDao;
import com.damc.legalnotices.dto.excel.ExcelPreviewDto;
import com.damc.legalnotices.dto.notice.SendSampleNoticeDto;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.excel.ExcelService;
import com.damc.legalnotices.service.schedule.NoticeScheduleService;

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
    private final NoticeScheduleService noticeSchedule;
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

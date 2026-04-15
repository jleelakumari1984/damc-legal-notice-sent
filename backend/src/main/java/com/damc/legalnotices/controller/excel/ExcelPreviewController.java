package com.damc.legalnotices.controller.excel;

import com.damc.legalnotices.dto.excel.ExcelPreviewDto;
import com.damc.legalnotices.dto.notice.SendSampleNoticeDto;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.excel.ExcelService;
import com.damc.legalnotices.service.schedule.NoticeScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/excel-preview")
@RequiredArgsConstructor
public class ExcelPreviewController {

    private final ExcelService excelService;
    private final NoticeScheduleService noticeSchedule;
    private final BaseService baseService;

    @PostMapping(value = "/zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExcelPreviewDto> previewZip(
            @RequestParam("zipFile") MultipartFile zipFile) {
        log.info("Excel preview request for file: {}", zipFile != null ? zipFile.getOriginalFilename() : "null");
        return ResponseEntity.ok(excelService.previewExcel(baseService.getSessionUser(), zipFile));
    }

    @PostMapping("/send-sample")
    public ResponseEntity<Void> sendSample(@RequestBody SendSampleNoticeDto request) {
        log.info("Send sample notice request for processSno: {}, mobile: {}", request.getProcessSno(),
                request.getMobileNumber());
        noticeSchedule.sendSampleNotice(request);
        return ResponseEntity.ok().build();
    }
}

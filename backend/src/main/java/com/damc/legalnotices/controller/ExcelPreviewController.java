package com.damc.legalnotices.controller;

import com.damc.legalnotices.dto.ExcelPreviewDto;
import com.damc.legalnotices.dto.SendSampleNoticeRequestDto;
import com.damc.legalnotices.service.ExcelService;
import com.damc.legalnotices.service.NoticeProcessingService;
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
    private final NoticeProcessingService noticeProcessingService;

    @PostMapping(value = "/zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExcelPreviewDto> previewZip(
            @RequestParam("zipFile") MultipartFile zipFile) {
        log.info("Excel preview request for file: {}", zipFile != null ? zipFile.getOriginalFilename() : "null");
        return ResponseEntity.ok(excelService.previewExcel(zipFile));
    }

    @PostMapping("/send-sample")
    public ResponseEntity<Void> sendSample(@RequestBody SendSampleNoticeRequestDto request) {
        log.info("Send sample notice request for processSno: {}, mobile: {}", request.getProcessSno(), request.getMobileNumber());
        noticeProcessingService.sendSampleNotice(request);
        return ResponseEntity.ok().build();
    }
}

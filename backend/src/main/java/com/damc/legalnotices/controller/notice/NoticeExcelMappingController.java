package com.damc.legalnotices.controller.notice;

import com.damc.legalnotices.dao.notice.NoticeExcelMappingDao;
import com.damc.legalnotices.dto.notice.NoticeExcelMappingDto;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.notice.NoticeExcelMappingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

import java.util.List;

@RestController
@RequestMapping("/api/notice-mappings/excel")
@RequiredArgsConstructor
public class NoticeExcelMappingController {

    private final BaseService baseService;
    private final NoticeExcelMappingService mappingService;

    @GetMapping("/list")
    public ResponseEntity<List<NoticeExcelMappingDao>> getByProcessId(@RequestParam Long processId) {
        return ResponseEntity.ok(mappingService.getByProcessId(baseService.getSessionUser(), processId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeExcelMappingDao> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mappingService.getById(baseService.getSessionUser(), id));
    }

    @PostMapping
    public ResponseEntity<NoticeExcelMappingDao> create(@Valid @RequestBody NoticeExcelMappingDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mappingService.create(baseService.getSessionUser(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoticeExcelMappingDao> update(@PathVariable Long id,
            @Valid @RequestBody NoticeExcelMappingDto request) {
        return ResponseEntity.ok(mappingService.update(baseService.getSessionUser(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mappingService.delete(baseService.getSessionUser(), id);
        return ResponseEntity.noContent().build();
    }
}

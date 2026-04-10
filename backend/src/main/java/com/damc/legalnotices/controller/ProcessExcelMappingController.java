package com.damc.legalnotices.controller;

import com.damc.legalnotices.dto.ProcessExcelMappingRequestDto;
import com.damc.legalnotices.dto.ProcessExcelMappingResponseDto;
import com.damc.legalnotices.service.ProcessExcelMappingService;
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
@RequestMapping("/api/excel-mappings")
@RequiredArgsConstructor
public class ProcessExcelMappingController {

    private final ProcessExcelMappingService mappingService;

    @GetMapping
    public ResponseEntity<List<ProcessExcelMappingResponseDto>> getByProcessId(@RequestParam Long processId) {
        return ResponseEntity.ok(mappingService.getByProcessId(processId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessExcelMappingResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mappingService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProcessExcelMappingResponseDto> create(@Valid @RequestBody ProcessExcelMappingRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mappingService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcessExcelMappingResponseDto> update(@PathVariable Long id,
                                                                  @Valid @RequestBody ProcessExcelMappingRequestDto request) {
        return ResponseEntity.ok(mappingService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mappingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

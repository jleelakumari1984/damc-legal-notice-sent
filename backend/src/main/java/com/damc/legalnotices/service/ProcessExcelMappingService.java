package com.damc.legalnotices.service;

import com.damc.legalnotices.dto.ProcessExcelMappingRequestDto;
import com.damc.legalnotices.dto.ProcessExcelMappingResponseDto;

import java.util.List;

public interface ProcessExcelMappingService {

    List<ProcessExcelMappingResponseDto> getByProcessId(Long processId);

    ProcessExcelMappingResponseDto getById(Long id);

    ProcessExcelMappingResponseDto create(ProcessExcelMappingRequestDto request);

    ProcessExcelMappingResponseDto update(Long id, ProcessExcelMappingRequestDto request);

    void delete(Long id);
}

package com.damc.legalnotices.service.impl;

import com.damc.legalnotices.dto.ProcessExcelMappingRequestDto;
import com.damc.legalnotices.dto.ProcessExcelMappingResponseDto;
import com.damc.legalnotices.entity.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.ProcessExcelMappingEntity;
import com.damc.legalnotices.repository.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.repository.ProcessExcelMappingRepository;
import com.damc.legalnotices.service.ProcessExcelMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessExcelMappingServiceImpl implements ProcessExcelMappingService {

    private final ProcessExcelMappingRepository mappingRepository;
    private final MasterProcessTemplateDetailRepository processTemplateRepository;

    @Override
    public List<ProcessExcelMappingResponseDto> getByProcessId(Long processId) {
        return mappingRepository.findByProcessId(processId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ProcessExcelMappingResponseDto getById(Long id) {
        ProcessExcelMappingEntity entity = mappingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Excel mapping not found with id: " + id));
        return toResponse(entity);
    }

    @Override
    public ProcessExcelMappingResponseDto create(ProcessExcelMappingRequestDto request) {
        MasterProcessTemplateDetailEntity process = processTemplateRepository.findById(request.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException("Process template not found with id: " + request.getProcessId()));
        ProcessExcelMappingEntity entity = new ProcessExcelMappingEntity();
        entity.setProcess(process);
        entity.setExcelFieldName(request.getExcelFieldName());
        entity.setDbFieldName(request.getDbFieldName());
        entity.setIsKey(request.getIsKey());
        entity.setIsMobile(request.getIsMobile());
        entity.setIsMandatory(request.getIsMandatory());
        entity.setIsAttachment(request.getIsAttachment());
        entity.setCreatedAt(LocalDateTime.now());
        return toResponse(mappingRepository.save(entity));
    }

    @Override
    public ProcessExcelMappingResponseDto update(Long id, ProcessExcelMappingRequestDto request) {
        ProcessExcelMappingEntity entity = mappingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Excel mapping not found with id: " + id));
        MasterProcessTemplateDetailEntity process = processTemplateRepository.findById(request.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException("Process template not found with id: " + request.getProcessId()));
        entity.setProcess(process);
        entity.setExcelFieldName(request.getExcelFieldName());
        entity.setDbFieldName(request.getDbFieldName());
        entity.setIsKey(request.getIsKey());
        entity.setIsMobile(request.getIsMobile());
        entity.setIsMandatory(request.getIsMandatory());
        entity.setIsAttachment(request.getIsAttachment());
        return toResponse(mappingRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!mappingRepository.existsById(id)) {
            throw new IllegalArgumentException("Excel mapping not found with id: " + id);
        }
        mappingRepository.deleteById(id);
    }

    private ProcessExcelMappingResponseDto toResponse(ProcessExcelMappingEntity entity) {
        return ProcessExcelMappingResponseDto.builder()
                .id(entity.getId())
                .processId(entity.getProcess() != null ? entity.getProcess().getId() : null)
                .excelFieldName(entity.getExcelFieldName())
                .dbFieldName(entity.getDbFieldName())
                .isKey(entity.getIsKey())
                .isMobile(entity.getIsMobile())
                .isMandatory(entity.getIsMandatory())
                .isAttachment(entity.getIsAttachment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.dao.notice.NoticeExcelMappingDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeExcelMappingDto;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.excel.ProcessExcelMappingEntity;
import com.damc.legalnotices.repository.master.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.service.notice.NoticeExcelMappingService;
import com.damc.legalnotices.repository.excel.ProcessExcelMappingRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeExcelMappingServiceImpl implements NoticeExcelMappingService {

    private final ProcessExcelMappingRepository mappingRepository;
    private final MasterProcessTemplateDetailRepository processTemplateRepository;

    @Override
    public List<NoticeExcelMappingDao> getByProcessId(LoginUserDao sessionUser, Long processId) {
        return mappingRepository.findByProcessId(processId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NoticeExcelMappingDao getById(LoginUserDao sessionUser, Long id) {
        ProcessExcelMappingEntity entity = mappingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Excel mapping not found with id: " + id));
        return toResponse(entity);
    }

    @Override
    public NoticeExcelMappingDao create(LoginUserDao sessionUser, NoticeExcelMappingDto request) {
        MasterProcessTemplateDetailEntity process = processTemplateRepository.findById(request.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Process template not found with id: " + request.getProcessId()));
        ProcessExcelMappingEntity entity = new ProcessExcelMappingEntity();
        entity.setProcess(process);
        entity.setExcelFieldName(request.getExcelFieldName());
        entity.setDbFieldName(request.getDbFieldName());
        entity.setIsKey(request.getIsKey());
        entity.setIsMobile(request.getIsMobile());
        entity.setIsMandatory(request.getIsMandatory());
        entity.setIsAttachment(request.getIsAttachment());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(sessionUser.getId());
        return toResponse(mappingRepository.save(entity));
    }

    @Override
    public NoticeExcelMappingDao update(LoginUserDao sessionUser, Long id, NoticeExcelMappingDto request) {
        ProcessExcelMappingEntity entity = mappingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Excel mapping not found with id: " + id));
        MasterProcessTemplateDetailEntity process = processTemplateRepository.findById(request.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Process template not found with id: " + request.getProcessId()));
        entity.setProcess(process);
        entity.setExcelFieldName(request.getExcelFieldName());
        entity.setDbFieldName(request.getDbFieldName());
        entity.setIsKey(request.getIsKey());
        entity.setIsMobile(request.getIsMobile());
        entity.setIsMandatory(request.getIsMandatory());
        entity.setIsAttachment(request.getIsAttachment());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(sessionUser.getId());
        return toResponse(mappingRepository.save(entity));
    }

    @Override
    public void delete(LoginUserDao sessionUser, Long id) {
        if (!mappingRepository.existsById(id)) {
            throw new IllegalArgumentException("Excel mapping not found with id: " + id);
        }
        mappingRepository.deleteById(id);
    }

    private NoticeExcelMappingDao toResponse(ProcessExcelMappingEntity entity) {
        return NoticeExcelMappingDao.builder()
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

package com.notices.api.service.notice.impl;

import com.notices.domain.dao.notice.NoticeExcelMappingDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.notice.NoticeExcelMappingDto;
import com.notices.domain.entity.master.MasterProcessExcelMappingEntity;
import com.notices.domain.entity.master.MasterProcessTemplateDetailEntity;
import com.notices.domain.repository.master.MasterProcessExcelMappingRepository;
import com.notices.domain.repository.master.MasterProcessTemplateDetailRepository;
import com.notices.api.service.notice.NoticeExcelMappingService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeExcelMappingServiceImpl implements NoticeExcelMappingService {

    private final MasterProcessExcelMappingRepository mappingRepository;
    private final MasterProcessTemplateDetailRepository noticeTemplateRepository;

    @Override
    public List<NoticeExcelMappingDao> getByNoticeId(LoginUserDao sessionUser, Long noticeId) {
        return mappingRepository.findByProcessId(noticeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NoticeExcelMappingDao getById(LoginUserDao sessionUser, Long id) {
        MasterProcessExcelMappingEntity entity = mappingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Excel mapping not found with id: " + id));
        return toResponse(entity);
    }

    @Override
    public NoticeExcelMappingDao create(LoginUserDao sessionUser, NoticeExcelMappingDto request) {
        MasterProcessTemplateDetailEntity notice = noticeTemplateRepository.findById(request.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notice template not found with id: " + request.getNoticeId()));
        MasterProcessExcelMappingEntity entity = new MasterProcessExcelMappingEntity();
        entity.setProcess(notice);
        entity.setExcelFieldName(request.getExcelFieldName());
        entity.setDbFieldName(request.getDbFieldName());
        entity.setIsAgreement(request.getIsAgreement());
        entity.setIsCustomerName(request.getIsCustomerName());
        entity.setIsMobile(request.getIsMobile());
        entity.setIsMandatory(request.getIsMandatory());
        entity.setIsAttachment(request.getIsAttachment());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(sessionUser.getId());
        return toResponse(mappingRepository.save(entity));
    }

    @Override
    public NoticeExcelMappingDao update(LoginUserDao sessionUser, Long id, NoticeExcelMappingDto request) {
        MasterProcessExcelMappingEntity entity = mappingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Excel mapping not found with id: " + id));
        MasterProcessTemplateDetailEntity notice = noticeTemplateRepository.findById(request.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notice template not found with id: " + request.getNoticeId()));
        entity.setProcess(notice);
        entity.setExcelFieldName(request.getExcelFieldName());
        entity.setDbFieldName(request.getDbFieldName());
        entity.setIsAgreement(request.getIsAgreement());
        entity.setIsCustomerName(request.getIsCustomerName());
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

    private NoticeExcelMappingDao toResponse(MasterProcessExcelMappingEntity entity) {
        return NoticeExcelMappingDao.builder()
                .id(entity.getId())
                .noticeId(entity.getProcess() != null ? entity.getProcess().getId() : null)
                .excelFieldName(entity.getExcelFieldName())
                .dbFieldName(entity.getDbFieldName())
                .isAgreement(entity.getIsAgreement())
                .isCustomerName(entity.getIsCustomerName())
                .isMobile(entity.getIsMobile())
                .isMandatory(entity.getIsMandatory())
                .isAttachment(entity.getIsAttachment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

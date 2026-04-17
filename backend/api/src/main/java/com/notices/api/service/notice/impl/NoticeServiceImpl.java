package com.notices.api.service.notice.impl;

import com.notices.domain.dao.DataTableDao;
import com.notices.domain.dao.notice.NoticeTemplateReportDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.DatatableDto;
import com.notices.domain.dto.notice.NoticeTypeDto;
import com.notices.domain.dto.notice.NoticeTypesRequest;
import com.notices.domain.entity.master.MasterProcessTemplateDetailEntity;
import com.notices.domain.entity.view.ProcessConfigReportViewEntity;
import com.notices.domain.repository.master.MasterProcessTemplateDetailRepository;
import com.notices.domain.repository.view.ProcessConfigReportViewRepository;
import com.notices.api.service.notice.NoticeService;
import com.notices.domain.util.converter.EntityDaoConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

        private final ProcessConfigReportViewRepository processConfigReportViewRepository;
        private final MasterProcessTemplateDetailRepository noticeTemplateRepository;
        private final EntityDaoConverter entityDaoConverter;

        @Override
        public DataTableDao<List<NoticeTemplateReportDao>> getNoticeTypes(LoginUserDao sessionUser,
                        DatatableDto<NoticeTypesRequest> request) {
                Pageable pageable = request == null || request.isAllData() ? Pageable.unpaged()
                                : request.getPagination("id");
                Page<ProcessConfigReportViewEntity> data = null;
                if (sessionUser.isAdmin()) {
                        if (request.getFilter() != null && request.getFilter().getUserId() != null) {
                                data = processConfigReportViewRepository.findAllByCreatedBy(
                                                request.getFilter().getUserId(),
                                                pageable);
                        } else {
                                data = processConfigReportViewRepository.findAll(pageable);
                        }
                } else {
                        data = processConfigReportViewRepository.findAllByCreatedBy(sessionUser.getId(), pageable);
                }
                return DataTableDao.<List<NoticeTemplateReportDao>>builder()
                                .recordsTotal(data.getTotalElements())
                                .recordsFiltered(data.getContent().size())
                                .data(data.getContent().stream()
                                                .map(entityDaoConverter::toNoticeTemplateReportDao)
                                                .toList())
                                .build();
        }

        @Override
        public NoticeTemplateReportDao getNoticeTypesDetail(LoginUserDao sessionUser, Long id) {
                return processConfigReportViewRepository.findById(id)
                                .map(entityDaoConverter::toNoticeTemplateReportDao)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Notice type not found with id: " + id));
        }

        @Override
        @Transactional
        public NoticeTemplateReportDao createNoticeType(LoginUserDao sessionUser, NoticeTypeDto request) {
                if (noticeTemplateRepository.existsByNameAndCreatedBy(request.getName(), sessionUser.getId())) {
                        throw new IllegalArgumentException(
                                        "Notice type with name \"" + request.getName() + "\" already exists");
                }
                MasterProcessTemplateDetailEntity entity = new MasterProcessTemplateDetailEntity();
                entity.setName(request.getNoticeName());
                entity.setStepName(request.getStepName());
                entity.setDescription(request.getDescription());
                entity.setCreatedBy(sessionUser.getId());
                entity.setUpdatedBy(sessionUser.getId());
                MasterProcessTemplateDetailEntity saved = noticeTemplateRepository.save(entity);
                return processConfigReportViewRepository.findById(saved.getId())
                                .map(entityDaoConverter::toNoticeTemplateReportDao)
                                .orElseThrow(() -> new IllegalStateException(
                                                "View not updated yet for notice type id: " + saved.getId()));
        }

        @Override
        @Transactional
        public NoticeTemplateReportDao updateNoticeType(LoginUserDao sessionUser, Long id, NoticeTypeDto request) {
                MasterProcessTemplateDetailEntity entity = noticeTemplateRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Notice type not found: " + id));
                if (request.getName() != null &&
                                noticeTemplateRepository.existsByNameAndCreatedByAndIdNot(request.getName(),
                                                sessionUser.getId(), id)) {
                        throw new IllegalArgumentException(
                                        "Notice type with name \"" + request.getName() + "\" already exists");
                }
                if (request.getNoticeName() != null)
                        entity.setName(request.getNoticeName());
                if (request.getStepName() != null)
                        entity.setStepName(request.getStepName());
                if (request.getDescription() != null)
                        entity.setDescription(request.getDescription());
                entity.setUpdatedBy(sessionUser.getId());
                noticeTemplateRepository.save(entity);
                return processConfigReportViewRepository.findById(id)
                                .map(entityDaoConverter::toNoticeTemplateReportDao)
                                .orElseThrow(() -> new IllegalStateException(
                                                "View not updated yet for notice type id: " + id));
        }

        @Override
        public boolean noticeTypeNameExists(LoginUserDao sessionUser, String name, Long excludeId) {
                if (excludeId != null) {
                        return noticeTemplateRepository.existsByNameAndCreatedByAndIdNot(name, sessionUser.getId(),
                                        excludeId);
                }
                return noticeTemplateRepository.existsByNameAndCreatedBy(name, sessionUser.getId());
        }

}

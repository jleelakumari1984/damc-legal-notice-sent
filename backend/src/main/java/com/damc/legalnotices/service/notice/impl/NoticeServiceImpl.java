package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.notice.ProcessTemplateReportDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeTypesRequest;
import com.damc.legalnotices.entity.view.ProcessConfigReportViewEntity;
import com.damc.legalnotices.repository.view.ProcessConfigReportViewRepository;
import com.damc.legalnotices.service.notice.NoticeService;
import com.damc.legalnotices.util.converter.EntityDaoConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final ProcessConfigReportViewRepository processConfigReportViewRepository;
    private final EntityDaoConverter entityDaoConverter;

    @Override
    public DataTableDao<List<ProcessTemplateReportDao>> getNoticeTypes(LoginUserDao sessionUser,
            NoticeTypesRequest request) {
        Pageable pageable = request == null  || request.isAllData() ? Pageable.unpaged() : request.getPagination();
        Page<ProcessConfigReportViewEntity> data = processConfigReportViewRepository.findAll(pageable);
        return DataTableDao.<List<ProcessTemplateReportDao>>builder()
                .recordsTotal(data.getTotalElements())
                .recordsFiltered(data.getContent().size())
                .data(data.getContent().stream()
                        .map(entityDaoConverter::toProcessTemplateReportDao)
                        .toList())
                .build();
    }

    @Override
    public ProcessTemplateReportDao getNoticeTypesDetail(LoginUserDao sessionUser, Long id) {
        return processConfigReportViewRepository.findById(id)
                .map(entityDaoConverter::toProcessTemplateReportDao)
                .orElseThrow(() -> new IllegalArgumentException("Notice type not found with id: " + id));
    }

}

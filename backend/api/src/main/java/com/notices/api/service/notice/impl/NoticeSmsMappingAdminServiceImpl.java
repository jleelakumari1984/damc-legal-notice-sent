package com.notices.api.service.notice.impl;

import com.notices.domain.dao.DataTableDao;
import com.notices.domain.dao.notice.SmsPendingTemplateDao;
import com.notices.domain.dao.notice.SmsTemplateDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.DatatableDto;
import com.notices.domain.dto.notice.NoticeSmsPendingDto;
import com.notices.domain.entity.master.MasterProcessSmsConfigDetailEntity;
import com.notices.domain.repository.master.MasterProcessSmsConfigDetailRepository;
import com.notices.api.service.notice.NoticeSmsMappingAdminService;
import com.notices.domain.util.converter.NoticeMappingEntityDaoConverter;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeSmsMappingAdminServiceImpl implements NoticeSmsMappingAdminService {

    private final MasterProcessSmsConfigDetailRepository smsConfigRepository;
    private final NoticeMappingEntityDaoConverter entityDaoConverter;

    @Override
    public List<SmsTemplateDao> getByNoticeId(LoginUserDao sessionUser, Long noticeId, Boolean status) {
        if (sessionUser.isAdmin()) {
            if (status != null) {
                return smsConfigRepository.findByProcessIdAndStatus(noticeId, status).stream()
                        .map(e -> entityDaoConverter.toSmsTemplateDao(e, sessionUser))
                        .toList();
            }
            return smsConfigRepository.findByProcessId(noticeId).stream()
                    .map(e -> entityDaoConverter.toSmsTemplateDao(e, sessionUser))
                    .toList();
        }
        return smsConfigRepository.findByProcessIdAndCreatedBy(noticeId, sessionUser.getId()).stream()
                .map(e -> entityDaoConverter.toSmsTemplateDao(e, sessionUser))
                .toList();
    }

    @Override
    public SmsTemplateDao getById(LoginUserDao sessionUser, Long id) {
        MasterProcessSmsConfigDetailEntity entity = smsConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SMS config not found with id: " + id));
        return entityDaoConverter.toSmsTemplateDao(entity, sessionUser);
    }

    @Override
    public DataTableDao<List<SmsPendingTemplateDao>> getPendingTemplates(LoginUserDao sessionUser,
            DatatableDto<NoticeSmsPendingDto> request) {
        if (sessionUser.isAdmin()) {
            Pageable pageable = request == null || request.isAllData() ? Pageable.unpaged()
                    : request.getPagination("id");
            Page<MasterProcessSmsConfigDetailEntity> page = null;
            if (request.getFilter() != null && request.getFilter().getUserId() != null) {
                page = smsConfigRepository.findPendingTemplatesByCreatedBy(request.getFilter().getUserId(),
                        pageable);
            } else {
                page = smsConfigRepository.findPendingTemplates(pageable);
            }
            return DataTableDao.<List<SmsPendingTemplateDao>>builder().draw(request.getDraw())
                    .recordsTotal(page.getTotalElements())
                    .recordsFiltered(page.getNumberOfElements())
                    .data(page.getContent().stream()
                            .map(e -> entityDaoConverter.toSmsPendingTemplateDao(e, sessionUser)).toList())
                    .build();
        }
        return DataTableDao.<List<SmsPendingTemplateDao>>builder().data(List.of()).build();

    }

}

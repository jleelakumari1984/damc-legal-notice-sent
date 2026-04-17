package com.notices.api.service.notice.impl;

import com.notices.domain.dao.DataTableDao;
import com.notices.domain.dao.notice.WhatsAppPendingTemplateDao;
import com.notices.domain.dao.notice.WhatsAppTemplateDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.DatatableDto;
import com.notices.domain.dto.notice.NoticeWhatsAppPendingDto;
import com.notices.domain.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.notices.domain.repository.master.MasterProcessWhatsappConfigDetailRepository;
import com.notices.api.service.notice.NoticeWhatsappMappingAdminService;
import com.notices.domain.util.converter.NoticeMappingEntityDaoConverter;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeWhatsappMappingAdminServiceImpl implements NoticeWhatsappMappingAdminService {

    private final MasterProcessWhatsappConfigDetailRepository whatsappConfigRepository;
    private final NoticeMappingEntityDaoConverter entityDaoConverter;

    @Override
    public List<WhatsAppTemplateDao> getByNoticeId(LoginUserDao sessionUser, Long noticeId, Boolean status) {
        if (sessionUser.isAdmin()) {
            if (status != null) {
                return whatsappConfigRepository.findByProcessIdAndStatus(noticeId, status).stream()
                        .map(e -> entityDaoConverter.toWhatsAppTemplateDao(e, sessionUser))
                        .toList();
            }
            return whatsappConfigRepository.findByProcessId(noticeId).stream()
                    .map(e -> entityDaoConverter.toWhatsAppTemplateDao(e, sessionUser))
                    .toList();
        }
        return whatsappConfigRepository.findByProcessIdAndCreatedBy(noticeId, sessionUser.getId()).stream()
                .map(e -> entityDaoConverter.toWhatsAppTemplateDao(e, sessionUser))
                .toList();
    }

    @Override
    public WhatsAppTemplateDao getById(LoginUserDao sessionUser, Long id) {
        MasterProcessWhatsappConfigDetailEntity entity = whatsappConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp config not found with id: " + id));
        return entityDaoConverter.toWhatsAppTemplateDao(entity, sessionUser);
    }

    @Override
    public DataTableDao<List<WhatsAppPendingTemplateDao>> getPendingTemplates(LoginUserDao sessionUser,
            DatatableDto<NoticeWhatsAppPendingDto> request) {
        if (sessionUser.isAdmin()) {
            Pageable pageable = request == null || request.isAllData() ? Pageable.unpaged()
                    : request.getPagination("id");
            Page<MasterProcessWhatsappConfigDetailEntity> page = null;
            if (request.getFilter() != null && request.getFilter().getUserId() != null) {
                page = whatsappConfigRepository.findPendingTemplatesByCreatedBy(request.getFilter().getUserId(),
                        pageable);
            } else {
                page = whatsappConfigRepository.findPendingTemplates(pageable);
            }
            return DataTableDao.<List<WhatsAppPendingTemplateDao>>builder().draw(request.getDraw())
                    .recordsTotal(page.getTotalElements())
                    .recordsFiltered(page.getNumberOfElements())
                    .data(page.getContent().stream()
                            .map(e -> entityDaoConverter.toWhatsAppPendingTemplateDao(e, sessionUser)).toList())
                    .build();
        }
        return DataTableDao.<List<WhatsAppPendingTemplateDao>>builder().data(List.of()).build();
    }

}

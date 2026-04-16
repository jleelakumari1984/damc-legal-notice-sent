package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.notice.WhatsAppPendingTemplateDao;
import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.DatatableDto;
import com.damc.legalnotices.dto.notice.NoticeWhatsappConfigDto;
import com.damc.legalnotices.dto.notice.NoticeWhatsappPendingDto;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.damc.legalnotices.repository.master.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.repository.master.MasterProcessWhatsappConfigDetailRepository;
import com.damc.legalnotices.enums.TemplateApproveStatus;
import com.damc.legalnotices.service.notice.NoticeWhatsappMappingAdminService;
import com.damc.legalnotices.util.TemplateUtil;
import com.damc.legalnotices.util.converter.NoticeMappingEntityDaoConverter;
import com.damc.legalnotices.util.validator.NoticeWhatsappMappingValidationUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeWhatsappMappingAdminServiceImpl implements NoticeWhatsappMappingAdminService {

    private final LocationProperties appConfig;
    private final MasterProcessWhatsappConfigDetailRepository whatsappConfigRepository;
    private final MasterProcessTemplateDetailRepository noticeTemplateRepository;
    private final NoticeMappingEntityDaoConverter entityDaoConverter;
    private final TemplateUtil templateUtil;

    @Override
    public List<WhatsAppTemplateDao> getByNoticeId(LoginUserDao sessionUser, Long noticeId) {
        if (sessionUser.isAdmin()) {
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
    public WhatsAppTemplateDao create(LoginUserDao sessionUser, NoticeWhatsappConfigDto request) throws Exception {
        NoticeWhatsappMappingValidationUtil.validateAdminTemplate(request);
        MasterProcessTemplateDetailEntity notice = noticeTemplateRepository.findById(request.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notice template not found with id: " + request.getNoticeId()));
        var noticePath = templateUtil.getUserNoticePath(notice, sessionUser, "whatsapp");
        var relativeUserPath = noticePath + "/WhatsAppUserTemplate";
        var relativePath = noticePath + "/WhatsAppTemplate";

        Path templatePath = Path.of(appConfig.getTemplateLocation(), relativePath + ".html");
        Files.createDirectories(templatePath.getParent());
        if (request.getTemplateContent() != null) {
            Files.writeString(templatePath, request.getTemplateContent());
        }
        MasterProcessWhatsappConfigDetailEntity entity = new MasterProcessWhatsappConfigDetailEntity();
        entity.setProcess(notice);
        entity.setTemplateName(request.getTemplateName());
        entity.setTemplatePath(relativePath);
        entity.setUserTemplatePath(relativeUserPath);
        entity.setTemplateLang(request.getTemplateLang());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        entity.setApproveStatus(TemplateApproveStatus.APPROVED.getValue());
        entity.setApprovedBy(sessionUser.getId());
        entity.setCreatedBy(sessionUser.getId());
        return entityDaoConverter.toWhatsAppTemplateDao(whatsappConfigRepository.save(entity), sessionUser);
    }

    @Override
    public WhatsAppTemplateDao update(LoginUserDao sessionUser, Long id, NoticeWhatsappConfigDto request)
            throws Exception {
        NoticeWhatsappMappingValidationUtil.validateAdminTemplate(request);
        MasterProcessWhatsappConfigDetailEntity entity = whatsappConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp config not found with id: " + id));
        MasterProcessTemplateDetailEntity notice = noticeTemplateRepository.findById(request.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notice template not found with id: " + request.getNoticeId()));
        if (request.getTemplateContent() != null) {
            Path templatePath = Path.of(appConfig.getTemplateLocation(), entity.getTemplatePath());
            Files.writeString(templatePath, request.getTemplateContent());
        }
        entity.setProcess(notice);
        entity.setTemplateName(request.getTemplateName());
        entity.setTemplateLang(request.getTemplateLang());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : entity.getStatus());
        entity.setUpdatedBy(sessionUser.getId());
        return entityDaoConverter.toWhatsAppTemplateDao(whatsappConfigRepository.save(entity), sessionUser);
    }

    @Override
    public void delete(LoginUserDao sessionUser, Long id) {
        if (!whatsappConfigRepository.existsById(id)) {
            throw new IllegalArgumentException("WhatsApp config not found with id: " + id);
        }
        whatsappConfigRepository.deleteById(id);
    }

    @Override
    public DataTableDao<List<WhatsAppPendingTemplateDao>> getPendingTemplates(LoginUserDao sessionUser,
            DatatableDto<NoticeWhatsappPendingDto> request) {
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

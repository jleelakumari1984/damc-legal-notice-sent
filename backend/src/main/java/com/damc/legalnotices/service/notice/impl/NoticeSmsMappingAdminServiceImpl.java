package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.notice.SmsPendingTemplateDao;
import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.DatatableDto;
import com.damc.legalnotices.dto.notice.NoticeSmsConfigDto;
import com.damc.legalnotices.dto.notice.NoticeSmsPendingDto;
import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.repository.master.MasterProcessSmsConfigDetailRepository;
import com.damc.legalnotices.repository.master.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.enums.TemplateApproveStatus;
import com.damc.legalnotices.service.notice.NoticeSmsMappingAdminService;
import com.damc.legalnotices.util.TemplateUtil;
import com.damc.legalnotices.util.converter.NoticeMappingEntityDaoConverter;
import com.damc.legalnotices.util.validator.NoticeSmsMappingValidationUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeSmsMappingAdminServiceImpl implements NoticeSmsMappingAdminService {

    private final LocationProperties appConfig;
    private final MasterProcessSmsConfigDetailRepository smsConfigRepository;
    private final MasterProcessTemplateDetailRepository noticeTemplateRepository;
    private final NoticeMappingEntityDaoConverter entityDaoConverter;
    private final TemplateUtil templateUtil;

    @Override
    public List<SmsTemplateDao> getByNoticeId(LoginUserDao sessionUser, Long noticeId) {
        if (sessionUser.isAdmin()) {
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
    public SmsTemplateDao create(LoginUserDao sessionUser, NoticeSmsConfigDto request) throws Exception {
        NoticeSmsMappingValidationUtil.validateAdminTemplate(request);
        MasterProcessTemplateDetailEntity notice = noticeTemplateRepository.findById(request.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notice template not found with id: " + request.getNoticeId()));
        var noticePath = templateUtil.getUserNoticePath(notice, sessionUser, "sms");
        var relativeUserPath = noticePath + "/SmsUserTemplate";
        var relativePath = noticePath + "/SmsTemplate";
        Path templatePath = Path.of(appConfig.getTemplateLocation(), relativePath + ".html");
        Files.createDirectories(templatePath.getParent());
        if (request.getTemplateContent() != null) {
            Files.writeString(templatePath, request.getTemplateContent());
        }
        MasterProcessSmsConfigDetailEntity entity = new MasterProcessSmsConfigDetailEntity();
        entity.setProcess(notice);
        entity.setPeid(request.getPeid());
        entity.setSenderId(request.getSenderId());
        entity.setRouteId(request.getRouteId());
        entity.setTemplatePath(relativePath);
        entity.setUserTemplatePath(relativeUserPath);
        entity.setTemplateId(request.getTemplateId());
        entity.setChannel(request.getChannel());
        entity.setDcs(request.getDcs());
        entity.setFlashSms(request.getFlashSms());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        entity.setApproveStatus(TemplateApproveStatus.APPROVED.getValue());
        entity.setApprovedBy(sessionUser.getId());
        entity.setCreatedBy(sessionUser.getId());
        return entityDaoConverter.toSmsTemplateDao(smsConfigRepository.save(entity), sessionUser);
    }

    @Override
    public SmsTemplateDao update(LoginUserDao sessionUser, Long id, NoticeSmsConfigDto request) throws Exception {
        NoticeSmsMappingValidationUtil.validateAdminTemplate(request);
        MasterProcessSmsConfigDetailEntity entity = smsConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SMS config not found with id: " + id));
        MasterProcessTemplateDetailEntity notice = noticeTemplateRepository.findById(request.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notice template not found with id: " + request.getNoticeId()));
        if (request.getTemplateContent() != null) {
            Path templatePath = Path.of(appConfig.getTemplateLocation(), entity.getTemplatePath());
            Files.writeString(templatePath, request.getTemplateContent());
        }
        entity.setProcess(notice);
        entity.setPeid(request.getPeid());
        entity.setSenderId(request.getSenderId());
        entity.setRouteId(request.getRouteId());
        entity.setTemplateId(request.getTemplateId());
        entity.setChannel(request.getChannel());
        entity.setDcs(request.getDcs());
        entity.setFlashSms(request.getFlashSms());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : entity.getStatus());
        entity.setUpdatedBy(sessionUser.getId());
        return entityDaoConverter.toSmsTemplateDao(smsConfigRepository.save(entity), sessionUser);
    }

    @Override
    public void delete(LoginUserDao sessionUser, Long id) {
        if (!smsConfigRepository.existsById(id)) {
            throw new IllegalArgumentException("SMS config not found with id: " + id);
        }
        smsConfigRepository.deleteById(id);
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

package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.notice.WhatsAppUserTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeWhatsappConfigDto;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.damc.legalnotices.repository.master.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.repository.master.MasterProcessWhatsappConfigDetailRepository;
import com.damc.legalnotices.enums.TemplateApproveStatus;
import com.damc.legalnotices.service.notice.NoticeWhatsappMappingUserService;
import com.damc.legalnotices.util.TemplateUtil;
import com.damc.legalnotices.util.converter.NoticeMappingEntityDaoConverter;
import com.damc.legalnotices.util.validator.NoticeWhatsappMappingValidationUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class NoticeWhatsappMappingUserServiceImpl implements NoticeWhatsappMappingUserService {

    private final LocationProperties appConfig;
    private final MasterProcessWhatsappConfigDetailRepository whatsappConfigRepository;
    private final MasterProcessTemplateDetailRepository noticeTemplateRepository;
    private final NoticeMappingEntityDaoConverter entityDaoConverter;
    private final TemplateUtil templateUtil;

    @Override
    public WhatsAppUserTemplateDao create(LoginUserDao sessionUser, NoticeWhatsappConfigDto request) throws Exception {
        NoticeWhatsappMappingValidationUtil.validateUserTemplate(request);
        MasterProcessTemplateDetailEntity notice = noticeTemplateRepository.findById(request.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notice template not found with id: " + request.getNoticeId()));
        var noticePath = templateUtil.getUserNoticePath(notice, sessionUser, "whatsapp");
        var relativeUserPath = noticePath + "/WhatsAppUserTemplate";
        var relativePath = noticePath + "/WhatsAppTemplate";

        Path userTemplatePath = Path.of(appConfig.getTemplateLocation(), relativeUserPath + ".html");
        Files.createDirectories(userTemplatePath.getParent());
        Files.writeString(userTemplatePath, request.getUserTemplateContent());
        MasterProcessWhatsappConfigDetailEntity entity = new MasterProcessWhatsappConfigDetailEntity();
        entity.setProcess(notice);
        entity.setUserTemplatePath(relativeUserPath);
        entity.setTemplatePath(relativePath);
        entity.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        entity.setApproveStatus(TemplateApproveStatus.PENDING.getValue());
        entity.setCreatedBy(sessionUser.getId());
        return entityDaoConverter.toWhatsAppUserTemplateDao(whatsappConfigRepository.save(entity), sessionUser);
    }

    @Override
    public WhatsAppUserTemplateDao update(LoginUserDao sessionUser, Long id, NoticeWhatsappConfigDto request)
            throws Exception {
        NoticeWhatsappMappingValidationUtil.validateUserTemplate(request);
        MasterProcessWhatsappConfigDetailEntity entity = whatsappConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp config not found with id: " + id));
        MasterProcessTemplateDetailEntity notice = noticeTemplateRepository.findById(request.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notice template not found with id: " + request.getNoticeId()));
        Path userTemplatePath = Path.of(appConfig.getTemplateLocation(), entity.getUserTemplatePath() + ".html");
        Files.writeString(userTemplatePath, request.getUserTemplateContent());
        entity.setProcess(notice);
        entity.setStatus(request.getStatus() != null ? request.getStatus() : entity.getStatus());
        if (Integer.valueOf(TemplateApproveStatus.REJECTED.getValue()).equals(entity.getApproveStatus())) {
            entity.setApproveStatus(TemplateApproveStatus.PENDING.getValue());
        }
        entity.setUpdatedBy(sessionUser.getId());
        return entityDaoConverter.toWhatsAppUserTemplateDao(whatsappConfigRepository.save(entity), sessionUser);
    }

}

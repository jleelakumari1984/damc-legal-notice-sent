package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.notice.WhatsAppUserTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeWhatsappConfigDto;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.damc.legalnotices.repository.master.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.repository.master.MasterProcessWhatsappConfigDetailRepository;
import com.damc.legalnotices.service.notice.NoticeWhatsappMappingUserService;
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
    private final MasterProcessTemplateDetailRepository processTemplateRepository;
    private final NoticeMappingEntityDaoConverter entityDaoConverter;

    @Override
    public WhatsAppUserTemplateDao create(LoginUserDao sessionUser, NoticeWhatsappConfigDto request) throws Exception {
        NoticeWhatsappMappingValidationUtil.validateUserTemplate(request);
        MasterProcessTemplateDetailEntity process = processTemplateRepository.findById(request.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Process template not found with id: " + request.getProcessId()));
        long timestamp = System.currentTimeMillis();
        var relativeUserPath = entityDaoConverter.getUserNoticePath(process, sessionUser)
                + "/WhatsAppUserTemplate_" + timestamp + ".html";
        Path userTemplatePath = Path.of(appConfig.getTemplateLocation(), relativeUserPath);
        Files.createDirectories(userTemplatePath.getParent());
        Files.writeString(userTemplatePath, request.getUserTemplateContent());
        MasterProcessWhatsappConfigDetailEntity entity = new MasterProcessWhatsappConfigDetailEntity();
        entity.setProcess(process);
        entity.setUserTemplatePath(relativeUserPath);
        entity.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        entity.setCreatedBy(sessionUser.getId());
        return entityDaoConverter.toWhatsAppUserTemplateDao(whatsappConfigRepository.save(entity));
    }

    @Override
    public WhatsAppUserTemplateDao update(LoginUserDao sessionUser, Long id, NoticeWhatsappConfigDto request)
            throws Exception {
        NoticeWhatsappMappingValidationUtil.validateUserTemplate(request);
        MasterProcessWhatsappConfigDetailEntity entity = whatsappConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp config not found with id: " + id));
        MasterProcessTemplateDetailEntity process = processTemplateRepository.findById(request.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Process template not found with id: " + request.getProcessId()));
        Path userTemplatePath = Path.of(appConfig.getTemplateLocation(), entity.getUserTemplatePath());
        Files.writeString(userTemplatePath, request.getUserTemplateContent());
        entity.setProcess(process);
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy(sessionUser.getId());
        return entityDaoConverter.toWhatsAppUserTemplateDao(whatsappConfigRepository.save(entity));
    }

}

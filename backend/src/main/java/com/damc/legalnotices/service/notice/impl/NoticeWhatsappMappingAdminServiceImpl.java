package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.notice.WhatsAppTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeWhatsappConfigDto;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.damc.legalnotices.repository.master.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.repository.master.MasterProcessWhatsappConfigDetailRepository;
import com.damc.legalnotices.service.notice.NoticeWhatsappMappingAdminService;
import com.damc.legalnotices.util.converter.NoticeMappingEntityDaoConverter;
import com.damc.legalnotices.util.validator.NoticeWhatsappMappingValidationUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeWhatsappMappingAdminServiceImpl implements NoticeWhatsappMappingAdminService {

    private final LocationProperties appConfig;
    private final MasterProcessWhatsappConfigDetailRepository whatsappConfigRepository;
    private final MasterProcessTemplateDetailRepository processTemplateRepository;
    private final NoticeMappingEntityDaoConverter entityDaoConverter;

    @Override
    public List<WhatsAppTemplateDao> getByProcessId(LoginUserDao sessionUser, Long processId) {
        return whatsappConfigRepository.findByProcessId(processId).stream()
                .map(entityDaoConverter::toWhatsAppTemplateDao)
                .toList();
    }

    @Override
    public WhatsAppTemplateDao getById(LoginUserDao sessionUser, Long id) {
        MasterProcessWhatsappConfigDetailEntity entity = whatsappConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp config not found with id: " + id));
        return entityDaoConverter.toWhatsAppTemplateDao(entity);
    }

    @Override
    public WhatsAppTemplateDao create(LoginUserDao sessionUser, NoticeWhatsappConfigDto request) throws Exception {
        NoticeWhatsappMappingValidationUtil.validateAdminTemplate(request);
        MasterProcessTemplateDetailEntity process = processTemplateRepository.findById(request.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Process template not found with id: " + request.getProcessId()));
        long timestamp = System.currentTimeMillis();
        var relativePath = entityDaoConverter.getUserNoticePath(process, sessionUser)
                + "/WhatsAppTemplate_" + timestamp + ".html";
        Path templatePath = Path.of(appConfig.getTemplateLocation(), relativePath);
        Files.createDirectories(templatePath.getParent());
        if (request.getTemplateContent() != null) {
            Files.writeString(templatePath, request.getTemplateContent());
        }
        MasterProcessWhatsappConfigDetailEntity entity = new MasterProcessWhatsappConfigDetailEntity();
        entity.setProcess(process);
        entity.setSentLevel(request.getSentLevel());
        entity.setTemplateName(request.getTemplateName());
        entity.setTemplatePath(relativePath);
        entity.setTemplateLang(request.getTemplateLang());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        entity.setCreatedBy(sessionUser.getId());
        return entityDaoConverter.toWhatsAppTemplateDao(whatsappConfigRepository.save(entity));
    }

    @Override
    public WhatsAppTemplateDao update(LoginUserDao sessionUser, Long id, NoticeWhatsappConfigDto request)
            throws Exception {
        NoticeWhatsappMappingValidationUtil.validateAdminTemplate(request);
        MasterProcessWhatsappConfigDetailEntity entity = whatsappConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp config not found with id: " + id));
        MasterProcessTemplateDetailEntity process = processTemplateRepository.findById(request.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Process template not found with id: " + request.getProcessId()));
        if (request.getTemplateContent() != null) {
            Path templatePath = Path.of(appConfig.getTemplateLocation(), entity.getTemplatePath());
            Files.writeString(templatePath, request.getTemplateContent());
        }
        entity.setProcess(process);
        entity.setSentLevel(request.getSentLevel());
        entity.setTemplateName(request.getTemplateName());
        entity.setTemplateLang(request.getTemplateLang());
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy(sessionUser.getId());
        return entityDaoConverter.toWhatsAppTemplateDao(whatsappConfigRepository.save(entity));
    }

    @Override
    public void delete(LoginUserDao sessionUser, Long id) {
        if (!whatsappConfigRepository.existsById(id)) {
            throw new IllegalArgumentException("WhatsApp config not found with id: " + id);
        }
        whatsappConfigRepository.deleteById(id);
    }

}

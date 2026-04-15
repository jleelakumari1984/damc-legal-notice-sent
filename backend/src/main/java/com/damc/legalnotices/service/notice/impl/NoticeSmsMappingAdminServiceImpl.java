package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeSmsConfigDto;
import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.repository.master.MasterProcessSmsConfigDetailRepository;
import com.damc.legalnotices.repository.master.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.service.notice.NoticeSmsMappingAdminService;
import com.damc.legalnotices.util.converter.NoticeMappingEntityDaoConverter;
import com.damc.legalnotices.util.validator.NoticeSmsMappingValidationUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeSmsMappingAdminServiceImpl implements NoticeSmsMappingAdminService {

    private final LocationProperties appConfig;
    private final MasterProcessSmsConfigDetailRepository smsConfigRepository;
    private final MasterProcessTemplateDetailRepository processTemplateRepository;
    private final NoticeMappingEntityDaoConverter entityDaoConverter;

    @Override
    public List<SmsTemplateDao> getByProcessId(LoginUserDao sessionUser, Long processId) {
        return smsConfigRepository.findByProcessId(processId).stream()
                .map(entityDaoConverter::toSmsTemplateDao)
                .toList();
    }

    @Override
    public SmsTemplateDao getById(LoginUserDao sessionUser, Long id) {
        MasterProcessSmsConfigDetailEntity entity = smsConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SMS config not found with id: " + id));
        return entityDaoConverter.toSmsTemplateDao(entity);
    }

    @Override
    public SmsTemplateDao create(LoginUserDao sessionUser, NoticeSmsConfigDto request) throws Exception {
        NoticeSmsMappingValidationUtil.validateAdminTemplate(request);
        MasterProcessTemplateDetailEntity process = processTemplateRepository.findById(request.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Process template not found with id: " + request.getProcessId()));
        long timestamp = System.currentTimeMillis();
        var relativePath = entityDaoConverter.getUserNoticePath(process, sessionUser)
                + "/SmsTemplate_" + timestamp + ".html";
        Path templatePath = Path.of(appConfig.getTemplateLocation(), relativePath);
        Files.createDirectories(templatePath.getParent());
        if (request.getTemplateContent() != null) {
            Files.writeString(templatePath, request.getTemplateContent());
        }
        MasterProcessSmsConfigDetailEntity entity = new MasterProcessSmsConfigDetailEntity();
        entity.setProcess(process);
        entity.setSentLevel(request.getSentLevel());
        entity.setPeid(request.getPeid());
        entity.setSenderId(request.getSenderId());
        entity.setRouteId(request.getRouteId());
        entity.setTemplatePath(relativePath);
        entity.setTemplateId(request.getTemplateId());
        entity.setChannel(request.getChannel());
        entity.setDcs(request.getDcs());
        entity.setFlashSms(request.getFlashSms());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        entity.setCreatedBy(sessionUser.getId());
        return entityDaoConverter.toSmsTemplateDao(smsConfigRepository.save(entity));
    }

    @Override
    public SmsTemplateDao update(LoginUserDao sessionUser, Long id, NoticeSmsConfigDto request) throws Exception {
        NoticeSmsMappingValidationUtil.validateAdminTemplate(request);
        MasterProcessSmsConfigDetailEntity entity = smsConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SMS config not found with id: " + id));
        MasterProcessTemplateDetailEntity process = processTemplateRepository.findById(request.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Process template not found with id: " + request.getProcessId()));
        if (request.getTemplateContent() != null) {
            Path templatePath = Path.of(appConfig.getTemplateLocation(), entity.getTemplatePath());
            Files.writeString(templatePath, request.getTemplateContent());
        }
        entity.setProcess(process);
        entity.setSentLevel(request.getSentLevel());
        entity.setPeid(request.getPeid());
        entity.setSenderId(request.getSenderId());
        entity.setRouteId(request.getRouteId());
        entity.setTemplateId(request.getTemplateId());
        entity.setChannel(request.getChannel());
        entity.setDcs(request.getDcs());
        entity.setFlashSms(request.getFlashSms());
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy(sessionUser.getId());
        return entityDaoConverter.toSmsTemplateDao(smsConfigRepository.save(entity));
    }

    @Override
    public void delete(LoginUserDao sessionUser, Long id) {
        if (!smsConfigRepository.existsById(id)) {
            throw new IllegalArgumentException("SMS config not found with id: " + id);
        }
        smsConfigRepository.deleteById(id);
    }

}

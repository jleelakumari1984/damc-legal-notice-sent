package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.notice.SmsUserTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeSmsConfigDto;
import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.repository.master.MasterProcessSmsConfigDetailRepository;
import com.damc.legalnotices.repository.master.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.service.notice.NoticeSmsMappingUserService;
import com.damc.legalnotices.util.converter.NoticeMappingEntityDaoConverter;
import com.damc.legalnotices.util.validator.NoticeSmsMappingValidationUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class NoticeSmsMappingUserServiceImpl implements NoticeSmsMappingUserService {

    private final LocationProperties appConfig;
    private final MasterProcessSmsConfigDetailRepository smsConfigRepository;
    private final MasterProcessTemplateDetailRepository processTemplateRepository;
    private final NoticeMappingEntityDaoConverter entityDaoConverter;

    @Override
    public SmsUserTemplateDao create(LoginUserDao sessionUser, NoticeSmsConfigDto request) throws Exception {
        NoticeSmsMappingValidationUtil.validateUserTemplate(request);
        MasterProcessTemplateDetailEntity process = processTemplateRepository.findById(request.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Process template not found with id: " + request.getProcessId()));
        long timestamp = System.currentTimeMillis();
        var relativeUserPath = entityDaoConverter.getUserNoticePath(process, sessionUser)
                + "/SmsUserTemplate_" + timestamp + ".html";
        Path userTemplatePath = Path.of(appConfig.getTemplateLocation(), relativeUserPath);
        Files.createDirectories(userTemplatePath.getParent());
        Files.writeString(userTemplatePath, request.getUserTemplateContent());
        MasterProcessSmsConfigDetailEntity entity = new MasterProcessSmsConfigDetailEntity();
        entity.setProcess(process);
        entity.setUserTemplatePath(relativeUserPath);
        entity.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        entity.setCreatedBy(sessionUser.getId());
        return entityDaoConverter.toSmsUserTemplateDao(smsConfigRepository.save(entity));
    }

    @Override
    public SmsUserTemplateDao update(LoginUserDao sessionUser, Long id, NoticeSmsConfigDto request) throws Exception {
        NoticeSmsMappingValidationUtil.validateUserTemplate(request);
        MasterProcessSmsConfigDetailEntity entity = smsConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SMS config not found with id: " + id));
        MasterProcessTemplateDetailEntity process = processTemplateRepository.findById(request.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Process template not found with id: " + request.getProcessId()));
        Path userTemplatePath = Path.of(appConfig.getTemplateLocation(), entity.getUserTemplatePath());
        Files.writeString(userTemplatePath, request.getUserTemplateContent());
        entity.setProcess(process);
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy(sessionUser.getId());
        return entityDaoConverter.toSmsUserTemplateDao(smsConfigRepository.save(entity));
    }

}

package com.damc.legalnotices.service.notice.impl;

import java.nio.file.Path;
import java.time.LocalDateTime;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeSmsApproveDto;
import com.damc.legalnotices.dto.notice.NoticeSmsRejectDto;
import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity;
import com.damc.legalnotices.enums.TemplateApproveStatus;
import com.damc.legalnotices.repository.master.MasterProcessSmsConfigDetailRepository;
import com.damc.legalnotices.service.notice.NoticeSmsApprovalService;
import com.damc.legalnotices.util.FileUtil;
import com.damc.legalnotices.util.converter.NoticeMappingEntityDaoConverter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeSmsApprovalServiceImpl implements NoticeSmsApprovalService {

    private final MasterProcessSmsConfigDetailRepository smsConfigRepository;
    private final NoticeMappingEntityDaoConverter entityDaoConverter;
    private final LocationProperties appConfig;
    private final FileUtil fileUtil;

    @Override
    public SmsTemplateDao approve(LoginUserDao sessionUser, Long id, NoticeSmsApproveDto dto) throws Exception {
        MasterProcessSmsConfigDetailEntity entity = smsConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SMS config not found with id: " + id));

        Path userTemplatePath = Path.of(appConfig.getTemplateLocation(), entity.getTemplatePath() + ".html");
        fileUtil.writeString(userTemplatePath, dto.getTemplateContent());

        entity.setPeid(dto.getPeid());
        entity.setSenderId(dto.getSenderId());
        entity.setRouteId(dto.getRouteId());
        entity.setTemplateId(dto.getTemplateId());
        entity.setChannel(dto.getChannel());
        entity.setDcs(dto.getDcs());
        entity.setFlashSms(dto.getFlashSms());
        entity.setApproveStatus(TemplateApproveStatus.APPROVED.getValue());
        entity.setApprovedBy(sessionUser.getId());
        entity.setApprovedAt(LocalDateTime.now());
        entity.setUpdatedBy(sessionUser.getId());
        entity.setUpdatedAt(LocalDateTime.now());
        return entityDaoConverter.toSmsTemplateDao(smsConfigRepository.save(entity), sessionUser);
    }

    @Override
    public SmsTemplateDao toggleStatus(LoginUserDao sessionUser, Long id) {
        MasterProcessSmsConfigDetailEntity entity = smsConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SMS config not found with id: " + id));
        if (entity.getStatus() == null || entity.getStatus() == 0) {
            entity.setStatus(1);
        } else {
            entity.setStatus(0);
        }
        entity.setUpdatedBy(sessionUser.getId());
        entity.setUpdatedAt(LocalDateTime.now());
        return entityDaoConverter.toSmsTemplateDao(smsConfigRepository.save(entity), sessionUser);
    }

    @Override
    public SmsTemplateDao reject(LoginUserDao sessionUser, Long id, NoticeSmsRejectDto request) {
        MasterProcessSmsConfigDetailEntity entity = smsConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SMS config not found with id: " + id));
        // entity.setRejectReason(request.getRejectReason());
        entity.setApproveStatus(TemplateApproveStatus.REJECTED.getValue());
        entity.setUpdatedBy(sessionUser.getId());
        entity.setUpdatedAt(LocalDateTime.now());
        return entityDaoConverter.toSmsTemplateDao(smsConfigRepository.save(entity), sessionUser);
    }
}

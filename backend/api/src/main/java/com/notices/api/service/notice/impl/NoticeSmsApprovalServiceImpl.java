package com.notices.api.service.notice.impl;

import java.nio.file.Path;
import java.time.LocalDateTime;

import com.notices.domain.config.LocationProperties;
import com.notices.domain.dao.notice.SmsTemplateDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.util.FileUtil;
import com.notices.domain.dto.notice.NoticeSmsApproveDto;
import com.notices.domain.dto.notice.NoticeSmsRejectDto;
import com.notices.domain.entity.master.MasterProcessSmsConfigDetailEntity;
import com.notices.domain.enums.TemplateApproveStatus;
import com.notices.domain.repository.master.MasterProcessSmsConfigDetailRepository;
import com.notices.api.service.notice.NoticeSmsApprovalService;
import com.notices.domain.util.converter.NoticeMappingEntityDaoConverter;

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

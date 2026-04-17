package com.notices.api.service.notice.impl;

import java.nio.file.Path;
import java.time.LocalDateTime;

import com.notices.domain.config.LocationProperties;
import com.notices.domain.dao.notice.WhatsAppTemplateDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.util.FileUtil;
import com.notices.domain.dto.notice.NoticeWhatsAppApproveDto;
import com.notices.domain.dto.notice.NoticeWhatsAppRejectDto;
import com.notices.domain.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.notices.domain.enums.TemplateApproveStatus;
import com.notices.domain.repository.master.MasterProcessWhatsappConfigDetailRepository;
import com.notices.api.service.notice.NoticeWhatsappApprovalService;
import com.notices.domain.util.converter.NoticeMappingEntityDaoConverter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeWhatsappApprovalServiceImpl implements NoticeWhatsappApprovalService {

    private final MasterProcessWhatsappConfigDetailRepository whatsappConfigRepository;
    private final NoticeMappingEntityDaoConverter entityDaoConverter;
    private final LocationProperties appConfig;
    private final FileUtil fileUtil;

    @Override
    public WhatsAppTemplateDao approve(LoginUserDao sessionUser, Long id, NoticeWhatsAppApproveDto dto)
            throws Exception {
        MasterProcessWhatsappConfigDetailEntity entity = whatsappConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp config not found with id: " + id));
        Path templatePath = Path.of(appConfig.getTemplateLocation(), entity.getTemplatePath() + ".html");
        fileUtil.writeString(templatePath, dto.getTemplateContent());

        entity.setTemplateName(dto.getTemplateName());
        entity.setTemplateLang(dto.getTemplateLang());
        entity.setApproveStatus(TemplateApproveStatus.APPROVED.getValue());
        entity.setApprovedBy(sessionUser.getId());
        entity.setApprovedAt(LocalDateTime.now());
        entity.setUpdatedBy(sessionUser.getId());
        entity.setUpdatedAt(LocalDateTime.now());
        return entityDaoConverter.toWhatsAppTemplateDao(whatsappConfigRepository.save(entity), sessionUser);
    }

    @Override
    public WhatsAppTemplateDao toggleStatus(LoginUserDao sessionUser, Long id) {
        MasterProcessWhatsappConfigDetailEntity entity = whatsappConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp config not found with id: " + id));
        if (entity.getStatus() == null || entity.getStatus() == 0) {
            entity.setStatus(1);
        } else {
            entity.setStatus(0);
        }
        entity.setUpdatedBy(sessionUser.getId());
        entity.setUpdatedAt(LocalDateTime.now());
        return entityDaoConverter.toWhatsAppTemplateDao(whatsappConfigRepository.save(entity), sessionUser);
    }

    @Override
    public WhatsAppTemplateDao reject(LoginUserDao sessionUser, Long id, NoticeWhatsAppRejectDto request) {
        MasterProcessWhatsappConfigDetailEntity entity = whatsappConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WhatsApp config not found with id: " + id));
        // entity.setRejectReason(request.getRejectReason());
        entity.setApproveStatus(TemplateApproveStatus.REJECTED.getValue());
        entity.setUpdatedBy(sessionUser.getId());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(sessionUser.getId());
        return entityDaoConverter.toWhatsAppTemplateDao(whatsappConfigRepository.save(entity), sessionUser);
    }
}

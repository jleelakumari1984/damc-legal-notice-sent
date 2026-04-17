package com.notices.api.service.notice.impl;

import com.notices.domain.config.LocationProperties;
import com.notices.domain.dao.notice.WhatsAppUserTemplateDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.util.FileUtil;
import com.notices.domain.dto.notice.NoticeWhatsAppConfigDto;
import com.notices.domain.dto.notice.TemplateMessageCountDto;
import com.notices.domain.entity.master.MasterProcessTemplateDetailEntity;
import com.notices.domain.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.notices.domain.repository.master.MasterProcessTemplateDetailRepository;
import com.notices.domain.repository.master.MasterProcessWhatsappConfigDetailRepository;
import com.notices.domain.enums.TemplateApproveStatus;
import com.notices.api.service.notice.NoticeWhatsappMappingUserService;
import com.notices.api.util.validator.NoticeWhatsappMappingValidationUtil;
import com.notices.domain.util.TemplateUtil;
import com.notices.domain.util.converter.NoticeMappingEntityDaoConverter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class NoticeWhatsappMappingUserServiceImpl implements NoticeWhatsappMappingUserService {

        private final LocationProperties appConfig;
        private final MasterProcessWhatsappConfigDetailRepository whatsappConfigRepository;
        private final MasterProcessTemplateDetailRepository noticeTemplateRepository;
        private final NoticeMappingEntityDaoConverter entityDaoConverter;
        private final TemplateUtil templateUtil;
        private final FileUtil fileUtil;

        @Override
        public WhatsAppUserTemplateDao create(LoginUserDao sessionUser, NoticeWhatsAppConfigDto request)
                        throws Exception {
                NoticeWhatsappMappingValidationUtil.validateUserTemplate(request);
                MasterProcessTemplateDetailEntity notice = noticeTemplateRepository.findById(request.getNoticeId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Notice template not found with id: " + request.getNoticeId()));
                var noticePath = templateUtil.getUserNoticePath(notice, sessionUser, "whatsapp");
                var relativeUserPath = noticePath + "/WhatsAppUserTemplate";
                var relativePath = noticePath + "/WhatsAppTemplate";
                TemplateMessageCountDto msgCountDto = templateUtil
                                .findNoOfWhatsAppMessages(request.getUserTemplateContent());
                String approvedTemplateDefaultFormat = templateUtil
                                .getWhatsAppApprovedTemplateMessage(msgCountDto.getFields());
                Path userTemplatePath = Path.of(appConfig.getTemplateLocation(), relativeUserPath + ".html");
                Path templatePath = Path.of(appConfig.getTemplateLocation(), relativePath + ".html");
                fileUtil.writeString(userTemplatePath, request.getUserTemplateContent());
                fileUtil.writeString(templatePath, approvedTemplateDefaultFormat);

                MasterProcessWhatsappConfigDetailEntity entity = new MasterProcessWhatsappConfigDetailEntity();
                entity.setProcess(notice);
                entity.setUserTemplatePath(relativeUserPath);
                entity.setTemplatePath(relativePath);
                entity.setMessageLength(msgCountDto.getMessageLengths());
                entity.setNumberOfMessage(msgCountDto.getNoOfMessages());
                entity.setStatus(request.getStatus() != null ? request.getStatus() : 0);
                entity.setApproveStatus(TemplateApproveStatus.PENDING.getValue());
                entity.setCreatedBy(sessionUser.getId());
                return entityDaoConverter.toWhatsAppUserTemplateDao(whatsappConfigRepository.save(entity), sessionUser);
        }

        @Override
        public WhatsAppUserTemplateDao update(LoginUserDao sessionUser, Long id, NoticeWhatsAppConfigDto request)
                        throws Exception {
                NoticeWhatsappMappingValidationUtil.validateUserTemplate(request);
                MasterProcessWhatsappConfigDetailEntity entity = whatsappConfigRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "WhatsApp config not found with id: " + id));
                MasterProcessTemplateDetailEntity notice = noticeTemplateRepository.findById(request.getNoticeId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Notice template not found with id: " + request.getNoticeId()));
                Path userTemplatePath = Path.of(appConfig.getTemplateLocation(),
                                entity.getUserTemplatePath() + ".html");
                Path templatePath = Path.of(appConfig.getTemplateLocation(), entity.getTemplatePath() + ".html");
                fileUtil.writeString(userTemplatePath, request.getUserTemplateContent());

                TemplateMessageCountDto msgCountDto = templateUtil
                                .findNoOfWhatsAppMessages(request.getUserTemplateContent());
                String approvedTemplateDefaultFormat = templateUtil
                                .getWhatsAppApprovedTemplateMessage(msgCountDto.getFields());
                fileUtil.writeString(templatePath, approvedTemplateDefaultFormat);

                entity.setProcess(notice);
                entity.setMessageLength(msgCountDto.getMessageLengths());
                entity.setNumberOfMessage(msgCountDto.getNoOfMessages());
                entity.setStatus(request.getStatus() != null ? request.getStatus() : entity.getStatus());
                if (Integer.valueOf(TemplateApproveStatus.REJECTED.getValue()).equals(entity.getApproveStatus())) {
                        entity.setApproveStatus(TemplateApproveStatus.PENDING.getValue());
                }
                entity.setUpdatedBy(sessionUser.getId());
                return entityDaoConverter.toWhatsAppUserTemplateDao(whatsappConfigRepository.save(entity), sessionUser);
        }

}

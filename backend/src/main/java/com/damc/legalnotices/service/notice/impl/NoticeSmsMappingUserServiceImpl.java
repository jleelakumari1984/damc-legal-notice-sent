package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.notice.SmsUserTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeSmsConfigDto;
import com.damc.legalnotices.dto.notice.TemplateMessageCountDto;
import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.repository.master.MasterProcessSmsConfigDetailRepository;
import com.damc.legalnotices.repository.master.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.enums.TemplateApproveStatus;
import com.damc.legalnotices.service.notice.NoticeSmsMappingUserService;
import com.damc.legalnotices.util.FileUtil;
import com.damc.legalnotices.util.TemplateUtil;
import com.damc.legalnotices.util.converter.NoticeMappingEntityDaoConverter;
import com.damc.legalnotices.util.validator.NoticeSmsMappingValidationUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class NoticeSmsMappingUserServiceImpl implements NoticeSmsMappingUserService {

        private final LocationProperties appConfig;
        private final MasterProcessSmsConfigDetailRepository smsConfigRepository;
        private final MasterProcessTemplateDetailRepository noticeTemplateRepository;
        private final NoticeMappingEntityDaoConverter entityDaoConverter;
        private final TemplateUtil templateUtil;
        private final FileUtil fileUtil;

        @Override
        public SmsUserTemplateDao create(LoginUserDao sessionUser, NoticeSmsConfigDto request) throws Exception {
                NoticeSmsMappingValidationUtil.validateUserTemplate(request);
                MasterProcessTemplateDetailEntity notice = noticeTemplateRepository.findById(request.getNoticeId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Notice template not found with id: " + request.getNoticeId()));
                var noticePath = templateUtil.getUserNoticePath(notice, sessionUser, "sms");
                var relativeUserPath = noticePath + "/SmsUserTemplate";
                var relativePath = noticePath + "/SmsTemplate";
                TemplateMessageCountDto msgCountDto = templateUtil.findNoOfSmsMessages(request.getUserTemplateContent());

                Path userTemplatePath = Path.of(appConfig.getTemplateLocation(), relativeUserPath + ".html");
                fileUtil.writeString(userTemplatePath, request.getUserTemplateContent());

                String approvedTemplateDefaultFormat = templateUtil
                                .getSmsAppApprovedTemplateMessage(request.getUserTemplateContent(),
                                                msgCountDto.getFields());

                Path templatePath = Path.of(appConfig.getTemplateLocation(), relativePath + ".html");
                fileUtil.writeString(templatePath, approvedTemplateDefaultFormat);

                MasterProcessSmsConfigDetailEntity entity = new MasterProcessSmsConfigDetailEntity();
                entity.setProcess(notice);
                entity.setUserTemplatePath(relativeUserPath);
                entity.setTemplatePath(relativePath);
                entity.setMessageLength(msgCountDto.getMessageLengths());
                entity.setNumberOfMessage(msgCountDto.getNoOfMessages());
                entity.setStatus(request.getStatus() != null ? request.getStatus() : 0);
                entity.setApproveStatus(TemplateApproveStatus.PENDING.getValue());
                entity.setCreatedBy(sessionUser.getId());
                return entityDaoConverter.toSmsUserTemplateDao(smsConfigRepository.save(entity), sessionUser);
        }

        @Override
        public SmsUserTemplateDao update(LoginUserDao sessionUser, Long id, NoticeSmsConfigDto request)
                        throws Exception {
                NoticeSmsMappingValidationUtil.validateUserTemplate(request);
                MasterProcessSmsConfigDetailEntity entity = smsConfigRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("SMS config not found with id: " + id));
                MasterProcessTemplateDetailEntity notice = noticeTemplateRepository.findById(request.getNoticeId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Notice template not found with id: " + request.getNoticeId()));
                TemplateMessageCountDto msgCountDto = templateUtil.findNoOfSmsMessages(request.getUserTemplateContent());
                String approvedTemplateDefaultFormat = templateUtil
                                .getSmsAppApprovedTemplateMessage(request.getUserTemplateContent(),
                                                msgCountDto.getFields());

                Path userTemplatePath = Path.of(appConfig.getTemplateLocation(),
                                entity.getUserTemplatePath() + ".html");
                fileUtil.writeString(userTemplatePath, request.getUserTemplateContent());

                Path templatePath = Path.of(appConfig.getTemplateLocation(), entity.getTemplatePath() + ".html");
                fileUtil.writeString(templatePath, approvedTemplateDefaultFormat);

                entity.setProcess(notice);
                entity.setMessageLength(msgCountDto.getMessageLengths());
                entity.setNumberOfMessage(msgCountDto.getNoOfMessages());
                entity.setStatus(request.getStatus() != null ? request.getStatus() : entity.getStatus());
                if (Integer.valueOf(TemplateApproveStatus.REJECTED.getValue()).equals(entity.getApproveStatus())) {
                        entity.setApproveStatus(TemplateApproveStatus.PENDING.getValue());
                }
                entity.setUpdatedBy(sessionUser.getId());
                return entityDaoConverter.toSmsUserTemplateDao(smsConfigRepository.save(entity), sessionUser);
        }

}

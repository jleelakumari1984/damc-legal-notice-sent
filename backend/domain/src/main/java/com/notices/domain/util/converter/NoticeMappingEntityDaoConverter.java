package com.notices.domain.util.converter;

import com.notices.domain.dao.notice.SmsPendingTemplateDao;
import com.notices.domain.dao.notice.SmsTemplateDao;
import com.notices.domain.dao.notice.SmsUserTemplateDao;
import com.notices.domain.dao.notice.WhatsAppPendingTemplateDao;
import com.notices.domain.dao.notice.WhatsAppTemplateDao;
import com.notices.domain.dao.notice.WhatsAppUserTemplateDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.entity.master.MasterProcessSmsConfigDetailEntity;
import com.notices.domain.entity.master.MasterProcessWhatsappConfigDetailEntity;
import com.notices.domain.util.TemplateUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeMappingEntityDaoConverter {
        private final TemplateUtil templateUtil;

        public SmsUserTemplateDao toSmsUserTemplateDao(MasterProcessSmsConfigDetailEntity e, LoginUserDao sessionUser) {
                String templateContent = templateUtil.readTemplateContent(e.getUserTemplatePath());
                return SmsUserTemplateDao.builder()
                                .id(e.getId())
                                .noticeId(e.getProcess() != null ? e.getProcess().getId() : null)
                                .userTemplateContent(templateContent)
                                .messageLength(e.getMessageLength())
                                .numberOfMessage(e.getNumberOfMessage())
                                .status(e.getStatus())
                                .approveStatus(e.getApproveStatus())
                                .createdAt(e.getCreatedAt())
                                .build();
        }

        public SmsTemplateDao toSmsTemplateDao(MasterProcessSmsConfigDetailEntity e, LoginUserDao sessionUser) {
                String templateContent = templateUtil.readTemplateContent(e.getTemplatePath());
                String userTemplateContent = templateUtil.readTemplateContent(e.getUserTemplatePath());

                return SmsTemplateDao.builder()
                                .id(e.getId())
                                .noticeId(e.getProcess() != null ? e.getProcess().getId() : null)
                                .peid(e.getPeid())
                                .senderId(e.getSenderId())
                                .routeId(e.getRouteId())
                                .templateContent(templateContent)
                                .userTemplateContent(userTemplateContent)
                                .messageLength(e.getMessageLength())
                                .numberOfMessage(e.getNumberOfMessage())
                                .templateId(e.getTemplateId())
                                .channel(e.getChannel())
                                .dcs(e.getDcs())
                                .flashSms(e.getFlashSms())
                                .status(e.getStatus())
                                .approveStatus(e.getApproveStatus())
                                .createdAt(e.getCreatedAt())
                                .ownTemplate(e.getCreatedBy() != null && e.getCreatedBy().equals(sessionUser.getId()))
                                .createdUserName(
                                                e.getCreatedUser() != null ? e.getCreatedUser().getDisplayName() : null)
                                .build();
        }

        public SmsPendingTemplateDao toSmsPendingTemplateDao(
                        MasterProcessSmsConfigDetailEntity e, LoginUserDao sessionUser) {
                String userTemplateContent = templateUtil.readTemplateContent(e.getUserTemplatePath());
                String templateContent = templateUtil.readTemplateContent(e.getTemplatePath());
                return SmsPendingTemplateDao.builder()
                                .id(e.getId())
                                .noticeName(e.getProcess() != null ? e.getProcess().getName() : null)
                                .noticeId(e.getProcess() != null ? e.getProcess().getId() : null)
                                .userName(e.getCreatedUser() != null ? e.getCreatedUser().getDisplayName() : null)
                                .userTemplateContent(userTemplateContent)
                                .templateContent(templateContent)
                                .messageLength(e.getMessageLength())
                                .numberOfMessage(e.getNumberOfMessage())
                                .createdAt(e.getCreatedAt())
                                .build();
        }

        public WhatsAppUserTemplateDao toWhatsAppUserTemplateDao(MasterProcessWhatsappConfigDetailEntity e,
                        LoginUserDao sessionUser) {
                String templateContent = templateUtil.readTemplateContent(e.getUserTemplatePath());
                return WhatsAppUserTemplateDao.builder()
                                .id(e.getId())
                                .noticeId(e.getProcess() != null ? e.getProcess().getId() : null)
                                .userTemplateContent(templateContent)
                                .messageLength(e.getMessageLength())
                                .numberOfMessage(e.getNumberOfMessage())
                                .status(e.getStatus())
                                .approveStatus(e.getApproveStatus())
                                .createdAt(e.getCreatedAt())
                                .build();
        }

        public WhatsAppTemplateDao toWhatsAppTemplateDao(MasterProcessWhatsappConfigDetailEntity e,
                        LoginUserDao sessionUser) {
                String templateContent = templateUtil.readTemplateContent(e.getTemplatePath());
                String userTemplateContent = templateUtil.readTemplateContent(e.getUserTemplatePath());
                return WhatsAppTemplateDao.builder()
                                .id(e.getId())
                                .noticeId(e.getProcess() != null ? e.getProcess().getId() : null)
                                .templateName(e.getTemplateName())
                                .userTemplateContent(userTemplateContent)
                                .templateContent(templateContent)
                                .templateLang(e.getTemplateLang())
                                .messageLength(e.getMessageLength())
                                .numberOfMessage(e.getNumberOfMessage())
                                .status(e.getStatus())
                                .approveStatus(e.getApproveStatus())
                                .ownTemplate(e.getCreatedBy() != null && e.getCreatedBy().equals(sessionUser.getId()))
                                .createdUserName(
                                                e.getCreatedUser() != null ? e.getCreatedUser().getDisplayName() : null)
                                .createdAt(e.getCreatedAt())
                                .build();
        }

        public WhatsAppPendingTemplateDao toWhatsAppPendingTemplateDao(
                        MasterProcessWhatsappConfigDetailEntity e, LoginUserDao sessionUser) {
                String userTemplateContent = templateUtil.readTemplateContent(e.getUserTemplatePath());
                String templateContent = templateUtil.readTemplateContent(e.getTemplatePath());
                return WhatsAppPendingTemplateDao.builder()
                                .id(e.getId())
                                .noticeName(e.getProcess() != null ? e.getProcess().getName() : null)
                                .noticeId(e.getProcess() != null ? e.getProcess().getId() : null)
                                .userName(e.getCreatedUser() != null ? e.getCreatedUser().getDisplayName() : null)
                                .userTemplateContent(userTemplateContent)
                                .templateContent(templateContent)
                                .messageLength(e.getMessageLength())
                                .numberOfMessage(e.getNumberOfMessage())
                                .createdAt(e.getCreatedAt())
                                .build();
        }

}

package com.notices.domain.util.converter;

import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dao.notice.NoticeExcelMappingDao;
import com.notices.domain.dao.notice.NoticeTemplateReportDao;
import com.notices.domain.dao.schedule.ScheduledNoticeDao;
import com.notices.domain.entity.user.UserEntity;
import com.notices.domain.entity.view.ProcessConfigReportViewEntity;
import com.notices.domain.entity.master.MasterProcessExcelMappingEntity;
import com.notices.domain.entity.schedule.ScheduledNoticeEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntityDaoConverter {

        public LoginUserDao toLoginUserDao(UserEntity loginDetail) {
                return LoginUserDao.builder()
                                .id(loginDetail.getId())
                                .displayName(loginDetail.getDisplayName())
                                .loginName(loginDetail.getLoginName())
                                .password(loginDetail.getPassword())
                                .userEmail(loginDetail.getUserEmail())
                                .mobileSms(loginDetail.getUserMobileSms())
                                .mobileWhatsapp(loginDetail.getUserMobileWhatsapp())
                                .accessLevel(loginDetail.getAccessLevel())
                                .build();
        }

        public ScheduledNoticeDao toScheduledNoticeDao(ScheduledNoticeEntity notice) {
                return ScheduledNoticeDao.builder()
                                .id(notice.getId())
                                .noticeSno(notice.getProcessSno())
                                .noticeName(notice.getProcess() != null ? notice.getProcess().getStepName() : null)
                                .originalFileName(notice.getOriginalFileName())
                                .zipFilePath(notice.getZipFilePath())
                                .extractedFolderPath(notice.getExtractedFolderPath())
                                .sendSms(notice.getSendSms())
                                .sendWhatsapp(notice.getSendWhatsapp())
                                .status(notice.getStatus().name())
                                .createdAt(notice.getCreatedAt())
                                .build();
        }

        public NoticeTemplateReportDao toNoticeTemplateReportDao(
                        ProcessConfigReportViewEntity e) {
                return NoticeTemplateReportDao.builder()
                                .id(e.getSno())
                                .name(e.getStepName())
                                .description(e.getDescription())
                                .createdAt(e.getCreatedAt())
                                .createdUserName(e.getCreatedUserName())
                                .excelMapCount(e.getExcelMapCount() == null ? 0
                                                : e.getExcelMapCount())
                                .smsMessageCount(e.getSmsMessageCount() == null ? 0 : e.getSmsMessageCount())
                                .smsActiveCount(e.getSmsActiveCount() == null ? 0
                                                : e.getSmsActiveCount())
                                .smsInactiveCount(e.getSmsInactiveCount() == null ? 0
                                                : e.getSmsInactiveCount())
                                .smsMessageCount(e.getSmsMessageCount() == null ? 0 : e.getSmsMessageCount())
                                .whatsappMessageCount(
                                                e.getWhatsappMessageCount() == null ? 0 : e.getWhatsappMessageCount())
                                .whatsappActiveCount(e.getWhatsappActiveCount() == null ? 0
                                                : e.getWhatsappActiveCount())
                                .whatsappInactiveCount(e.getWhatsappInactiveCount() == null ? 0
                                                : e.getWhatsappInactiveCount())
                                .mailMessageCount(e.getMailMessageCount() == null ? 0 : e.getMailMessageCount())
                                .mailActiveCount(e.getMailActiveCount() == null ? 0
                                                : e.getMailActiveCount())
                                .mailInactiveCount(e.getMailInactiveCount() == null ? 0
                                                : e.getMailInactiveCount())
                                .build();
        }

        public NoticeExcelMappingDao toNoticeExcelMappingDao(MasterProcessExcelMappingEntity noticeexcelmapping1) {
                return NoticeExcelMappingDao.builder()
                                .id(noticeexcelmapping1.getId())
                                .excelFieldName(noticeexcelmapping1.getExcelFieldName())
                                .dbFieldName(noticeexcelmapping1.getDbFieldName())
                                .isMandatory(noticeexcelmapping1.getIsMandatory())
                                .isAttachment(noticeexcelmapping1.getIsAttachment())
                                .isAgreement(noticeexcelmapping1.getIsAgreement())
                                .isCustomerName(noticeexcelmapping1.getIsCustomerName())
                                .build();
        }

}

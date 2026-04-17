package com.damc.legalnotices.util.converter;

import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dao.notice.NoticeExcelMappingDao;
import com.damc.legalnotices.dao.notice.NoticeTemplateReportDao;
import com.damc.legalnotices.dao.schedule.ScheduledNoticeDao;
import com.damc.legalnotices.entity.user.UserEntity;
import com.damc.legalnotices.entity.view.ProcessConfigReportViewEntity;
import com.damc.legalnotices.entity.master.MasterProcessExcelMappingEntity;
import com.damc.legalnotices.entity.schedule.ScheduledNoticeEntity;

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
                                .smsActiveCount(e.getSmsActiveCount() == null ? 0
                                                : e.getSmsActiveCount())
                                .smsInactiveCount(e.getSmsInactiveCount() == null ? 0
                                                : e.getSmsInactiveCount())
                                .whatsappActiveCount(e.getWhatsappActiveCount() == null ? 0
                                                : e.getWhatsappActiveCount())
                                .whatsappInactiveCount(e.getWhatsappInactiveCount() == null ? 0
                                                : e.getWhatsappInactiveCount())
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

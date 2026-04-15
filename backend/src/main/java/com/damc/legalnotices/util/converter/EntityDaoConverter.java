package com.damc.legalnotices.util.converter;

import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dao.notice.NoticeExcelMappingDao;
import com.damc.legalnotices.dao.notice.ProcessTemplateReportDao;
import com.damc.legalnotices.dao.schedule.ScheduledNoticeDao;
import com.damc.legalnotices.entity.user.LoginDetailEntity;
import com.damc.legalnotices.entity.view.ProcessConfigReportViewEntity;
import com.damc.legalnotices.entity.schedule.ScheduledNoticeEntity;
import com.damc.legalnotices.entity.excel.ProcessExcelMappingEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntityDaoConverter {

    public LoginUserDao toLoginUserDao(LoginDetailEntity loginDetail) {
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
                .processSno(notice.getProcessSno())
                .processName(notice.getProcess() != null ? notice.getProcess().getStepName() : null)
                .originalFileName(notice.getOriginalFileName())
                .zipFilePath(notice.getZipFilePath())
                .extractedFolderPath(notice.getExtractedFolderPath())
                .sendSms(notice.getSendSms())
                .sendWhatsapp(notice.getSendWhatsapp())
                .status(notice.getStatus().name())
                .createdAt(notice.getCreatedAt())
                .build();
    }

    public ProcessTemplateReportDao toProcessTemplateReportDao(
            ProcessConfigReportViewEntity processConfigReportViewEntity) {
        return ProcessTemplateReportDao.builder()
                .id(processConfigReportViewEntity.getSno())
                .name(processConfigReportViewEntity.getStepName())
                .createdAt(processConfigReportViewEntity.getCreatedAt())
                .excelMapCount(processConfigReportViewEntity.getExcelMapCount() == null ? 0
                        : processConfigReportViewEntity.getExcelMapCount())
                .smsMapCount(processConfigReportViewEntity.getSmsMapCount() == null ? 0
                        : processConfigReportViewEntity.getSmsMapCount())
                .whatsappMapCount(processConfigReportViewEntity.getWhatsappMapCount() == null ? 0
                        : processConfigReportViewEntity.getWhatsappMapCount())
                .mailMapCount(processConfigReportViewEntity.getMailMapCount() == null ? 0
                        : processConfigReportViewEntity.getMailMapCount())
                .build();
    }

    public NoticeExcelMappingDao toProcessExcelMappingDao(ProcessExcelMappingEntity processexcelmapping1) {
        return NoticeExcelMappingDao.builder()
                .id(processexcelmapping1.getId())
                .excelFieldName(processexcelmapping1.getExcelFieldName())
                .dbFieldName(processexcelmapping1.getDbFieldName())
                .isMandatory(processexcelmapping1.getIsMandatory())
                .isAttachment(processexcelmapping1.getIsAttachment())
                .isKey(processexcelmapping1.getIsKey())
                .build();
    }

}

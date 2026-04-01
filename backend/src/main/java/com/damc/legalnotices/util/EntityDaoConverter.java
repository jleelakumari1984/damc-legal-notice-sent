package com.damc.legalnotices.util;

import com.damc.legalnotices.dao.LoginUserDao;
import com.damc.legalnotices.dao.ScheduledNoticeDao;
import com.damc.legalnotices.entity.LoginDetail;
import com.damc.legalnotices.entity.ScheduledNotice;
import org.springframework.stereotype.Component;

@Component
public class EntityDaoConverter {

    public LoginUserDao toLoginUserDao(LoginDetail loginDetail) {
        return LoginUserDao.builder()
                .id(loginDetail.getId())
                .displayName(loginDetail.getDisplayName())
                .loginName(loginDetail.getLoginName())
                .userEmail(loginDetail.getUserEmail())
                .mobileSms(loginDetail.getUserMobileSms())
                .mobileWhatsapp(loginDetail.getUserMobileWhatsapp())
                .accessLevel(loginDetail.getAccessLevel())
                .build();
    }

    public ScheduledNoticeDao toScheduledNoticeDao(ScheduledNotice notice) {
        return ScheduledNoticeDao.builder()
                .id(notice.getId())
                .processSno(notice.getProcessSno())
                .originalFileName(notice.getOriginalFileName())
                .zipFilePath(notice.getZipFilePath())
                .extractedFolderPath(notice.getExtractedFolderPath())
                .sendSms(notice.getSendSms())
                .sendWhatsapp(notice.getSendWhatsapp())
                .status(notice.getStatus().name())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}

package com.damc.legalnotices.service;

import com.damc.legalnotices.dao.ProcessTemplateDao;
import com.damc.legalnotices.dao.ScheduledNoticeDao;
import com.damc.legalnotices.dao.ScheduledNoticeDetailDao;
import com.damc.legalnotices.dto.NoticeValidationFileDto;
import com.damc.legalnotices.dto.NoticeValidationResponseDto;
import com.damc.legalnotices.entity.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.ScheduledNoticeEntity;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface NoticeService {
        NoticeValidationResponseDto scheduleNotice(Long processSno,
                        Boolean sendSms,
                        Boolean sendWhatsapp,
                        MultipartFile zipFile,
                        String loginName);

        List<ProcessTemplateDao> getNoticeTypes();

        List<ScheduledNoticeDao> getScheduledNotices();

        ScheduledNoticeDetailDao getScheduledNoticeDetail(Long id);

        NoticeValidationFileDto storeExcelData(Path extractedPath, MasterProcessTemplateDetailEntity template,
                        ScheduledNoticeEntity scheduledNotice);
}

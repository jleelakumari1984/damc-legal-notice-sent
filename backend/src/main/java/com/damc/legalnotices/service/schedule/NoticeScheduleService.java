package com.damc.legalnotices.service.schedule;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.damc.legalnotices.dao.excel.ProcessedExcelDao;
import com.damc.legalnotices.dao.excel.ProcessedNoticeItemDao;
import com.damc.legalnotices.dao.notice.NoticeValidationDao;
import com.damc.legalnotices.dao.user.SessionUserDao;
import com.damc.legalnotices.dto.notice.SendSampleNoticeDto;

public interface NoticeScheduleService {

        NoticeValidationDao scheduleNotice(Long processSno, Boolean sendSms, Boolean sendWhatsapp,
                        MultipartFile zipFile, SessionUserDao sessionuser);

        List<ProcessedExcelDao> processPendingExcelParsing();

        List<ProcessedNoticeItemDao> processPendingNoticeItems();

        void sendSampleNotice(SendSampleNoticeDto request);
}

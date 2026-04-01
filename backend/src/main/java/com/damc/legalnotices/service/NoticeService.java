package com.damc.legalnotices.service;

import com.damc.legalnotices.dao.ProcessTemplateDao;
import com.damc.legalnotices.dao.ScheduledNoticeDao;
import com.damc.legalnotices.dto.NoticeValidationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NoticeService {
    NoticeValidationResponseDto scheduleNotice(Long processSno,
                                               Boolean sendSms,
                                               Boolean sendWhatsapp,
                                               MultipartFile zipFile,
                                               String loginName);

    List<ProcessTemplateDao> getNoticeTypes();

    List<ScheduledNoticeDao> getScheduledNotices();
}

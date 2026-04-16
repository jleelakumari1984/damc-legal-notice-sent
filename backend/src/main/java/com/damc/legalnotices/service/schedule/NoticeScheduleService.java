package com.damc.legalnotices.service.schedule;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.damc.legalnotices.dao.excel.NoticeedExcelDao;
import com.damc.legalnotices.dao.excel.NoticeedNoticeItemDao;
import com.damc.legalnotices.dao.notice.NoticeValidationDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.SendSampleNoticeDto;

public interface NoticeScheduleService {

        NoticeValidationDao scheduleNotice(LoginUserDao  sessionUser, Long noticeSno, Boolean sendSms,
                        Boolean sendWhatsapp,
                        MultipartFile zipFile);

        List<NoticeedExcelDao> noticePendingExcelParsing();

        List<NoticeedNoticeItemDao> noticePendingNoticeItems();

        void sendSampleNotice(LoginUserDao sessionUser, SendSampleNoticeDto request);
}

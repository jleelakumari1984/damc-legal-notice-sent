package com.damc.legalnotices.service.schedule;

import java.util.List;

 

import com.damc.legalnotices.dao.excel.NoticeExcelDao;
import com.damc.legalnotices.dao.excel.ScheduledNoticeItemDao;
import com.damc.legalnotices.dao.notice.NoticeValidationDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeScheduleRequestDto;
import com.damc.legalnotices.dto.notice.SendSampleNoticeDto;

public interface NoticeScheduleService {

        NoticeValidationDao scheduleNotice(LoginUserDao sessionUser, NoticeScheduleRequestDto request);

        List<NoticeExcelDao> noticePendingExcelParsing();

        List<ScheduledNoticeItemDao> noticePendingNoticeItems();

        void sendSampleNotice(LoginUserDao sessionUser, SendSampleNoticeDto request);
}

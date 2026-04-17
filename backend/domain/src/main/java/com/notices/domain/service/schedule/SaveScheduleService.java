package com.notices.domain.service.schedule;

import java.nio.file.Path;

import com.notices.domain.dao.schedule.ScheduledNoticeDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.notice.NoticeValidationFileDto;
import com.notices.domain.entity.master.MasterProcessTemplateDetailEntity;
import com.notices.domain.entity.schedule.ScheduledNoticeEntity;

public interface SaveScheduleService {

  
    ScheduledNoticeDao startSchedule(LoginUserDao sessionUser, Long noticeId);

    ScheduledNoticeDao stopSchedule(LoginUserDao sessionUser, Long noticeId);

    NoticeValidationFileDto storeExcelData(Path extractedPath, Path directExcelPath,
            MasterProcessTemplateDetailEntity template, ScheduledNoticeEntity notice);

}

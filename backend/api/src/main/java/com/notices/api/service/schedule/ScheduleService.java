package com.notices.api.service.schedule;

import com.notices.api.dto.notice.NoticeScheduleRequestDto;
import com.notices.domain.dao.notice.NoticeValidationDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.notice.SendSampleNoticeDto;

public interface ScheduleService {

    void sendSampleNotice(LoginUserDao sessionUser, SendSampleNoticeDto request);

    NoticeValidationDao saveScheduleNotice(LoginUserDao sessionUser, NoticeScheduleRequestDto request);

}

package com.notices.api.service.notice;

import com.notices.domain.dao.notice.NoticeExcelMappingDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.notice.NoticeExcelMappingDto;

import java.util.List;

public interface NoticeExcelMappingService {

    List<NoticeExcelMappingDao> getByNoticeId(LoginUserDao  sessionUser, Long noticeId);

    NoticeExcelMappingDao getById(LoginUserDao  sessionUser, Long id);

    NoticeExcelMappingDao create(LoginUserDao  sessionUser, NoticeExcelMappingDto request);

    NoticeExcelMappingDao update(LoginUserDao  sessionUser, Long id, NoticeExcelMappingDto request);

    void delete(LoginUserDao  sessionUser, Long id);
}

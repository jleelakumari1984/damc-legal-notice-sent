package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.notice.NoticeExcelMappingDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeExcelMappingDto;

import java.util.List;

public interface NoticeExcelMappingService {

    List<NoticeExcelMappingDao> getByNoticeId(LoginUserDao  sessionUser, Long noticeId);

    NoticeExcelMappingDao getById(LoginUserDao  sessionUser, Long id);

    NoticeExcelMappingDao create(LoginUserDao  sessionUser, NoticeExcelMappingDto request);

    NoticeExcelMappingDao update(LoginUserDao  sessionUser, Long id, NoticeExcelMappingDto request);

    void delete(LoginUserDao  sessionUser, Long id);
}

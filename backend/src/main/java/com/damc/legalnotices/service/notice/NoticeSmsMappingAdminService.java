package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.notice.SmsTemplateDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.notice.NoticeSmsConfigDto;

import java.util.List;

public interface NoticeSmsMappingAdminService {

    List<SmsTemplateDao> getByProcessId(LoginUserDao sessionUser, Long processId);

    SmsTemplateDao getById(LoginUserDao sessionUser, Long id);

    SmsTemplateDao create(LoginUserDao sessionUser, NoticeSmsConfigDto request) throws Exception;

    SmsTemplateDao update(LoginUserDao sessionUser, Long id, NoticeSmsConfigDto request) throws Exception;

    void delete(LoginUserDao sessionUser, Long id);
}

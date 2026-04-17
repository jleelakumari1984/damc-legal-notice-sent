package com.notices.api.service.user;

import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dao.user.UserSmsCredentialDao;
import com.notices.domain.dao.user.UserWhatsAppCredentialDao;
import com.notices.domain.dto.user.UserSmsCredentialDto;
import com.notices.domain.dto.user.UserWhatsAppCredentialDto;

public interface UserCredentialService {

    void saveSmsCredential(LoginUserDao sessionUser, Long userId, UserSmsCredentialDto dto);

    void saveWhatsAppCredential(LoginUserDao sessionUser, Long userId, UserWhatsAppCredentialDto dto);

    UserSmsCredentialDao getSmsCredential(LoginUserDao sessionUser, Long userId);

    UserWhatsAppCredentialDao getWhatsAppCredential(LoginUserDao sessionUser, Long userId);
}

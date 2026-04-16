package com.damc.legalnotices.service.user;

import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dao.user.UserSmsCredentialDao;
import com.damc.legalnotices.dao.user.UserWhatsAppCredentialDao;
import com.damc.legalnotices.dto.user.UserSmsCredentialDto;
import com.damc.legalnotices.dto.user.UserWhatsAppCredentialDto;

public interface UserCredentialService {

    void saveSmsCredential(LoginUserDao sessionUser, Long userId, UserSmsCredentialDto dto);

    void saveWhatsAppCredential(LoginUserDao sessionUser, Long userId, UserWhatsAppCredentialDto dto);

    UserSmsCredentialDao getSmsCredential(LoginUserDao sessionUser, Long userId);

    UserWhatsAppCredentialDao getWhatsAppCredential(LoginUserDao sessionUser, Long userId);
}

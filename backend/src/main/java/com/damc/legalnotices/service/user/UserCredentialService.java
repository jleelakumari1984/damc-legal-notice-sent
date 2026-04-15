package com.damc.legalnotices.service.user;

import com.damc.legalnotices.config.SmsCredential;
import com.damc.legalnotices.config.WhatsAppCredential;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.user.UserSmsCredentialDto;
import com.damc.legalnotices.dto.user.UserWhatsAppCredentialDto;

import java.util.Optional;

public interface UserCredentialService {

    void saveSmsCredential(LoginUserDao sessionUser, Long userId, UserSmsCredentialDto dto);

    void saveWhatsAppCredential(LoginUserDao sessionUser, Long userId, UserWhatsAppCredentialDto dto);

    Optional<SmsCredential> getSmsCredential(Long userId);

    Optional<WhatsAppCredential> getWhatsAppCredential(Long userId);
}

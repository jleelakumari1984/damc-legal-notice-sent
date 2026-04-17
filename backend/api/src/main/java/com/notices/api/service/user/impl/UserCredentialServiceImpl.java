package com.notices.api.service.user.impl;

import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dao.user.UserSmsCredentialDao;
import com.notices.domain.dao.user.UserWhatsAppCredentialDao;
import com.notices.domain.dto.user.UserSmsCredentialDto;
import com.notices.domain.dto.user.UserWhatsAppCredentialDto;
import com.notices.domain.entity.user.UserActivityLogEntity;
import com.notices.domain.enums.UserActivityType;
import com.notices.domain.repository.user.UserActivityLogRepository;
import com.notices.api.service.user.UserCredentialService;
import com.notices.domain.util.EndpointUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCredentialServiceImpl implements UserCredentialService {

    private final UserActivityLogRepository activityLogRepository;
    private final EndpointUtil endpointUtil;

    @Override
    @Transactional
    public void saveSmsCredential(LoginUserDao sessionUser, Long userId, UserSmsCredentialDto dto) {
        endpointUtil.saveSmsCredential(userId, dto);
        logActivity(userId, sessionUser.getId(), UserActivityType.SMS_CREDENTIAL_UPDATE,
                "SMS credential updated: loginName=" + sessionUser.getLoginName());

    }

    @Override
    @Transactional
    public void saveWhatsAppCredential(LoginUserDao sessionUser, Long userId, UserWhatsAppCredentialDto dto) {
        endpointUtil.saveWhatsAppCredential(userId, dto);
        logActivity(userId, sessionUser.getId(), UserActivityType.WHATSAPP_CREDENTIAL_UPDATE,
                "WhatsApp credential updated: loginName=" + sessionUser.getLoginName());
    }

    @Override
    public UserSmsCredentialDao getSmsCredential(LoginUserDao sessionUser, Long userId) {
        return endpointUtil.getSmsCredential(userId);
    }

    @Override
    public UserWhatsAppCredentialDao getWhatsAppCredential(LoginUserDao sessionUser, Long userId) {
        return endpointUtil.getWhatsAppCredential(userId);
    }

    private void logActivity(Long userId, Long performedBy, UserActivityType action, String description) {
        try {
            activityLogRepository.save(UserActivityLogEntity.builder()
                    .userId(userId)
                    .performedBy(performedBy)
                    .action(action.name())
                    .description(description)
                    .build());
        } catch (Exception ex) {
            log.error("Failed to save user activity log for userId={}, action={}: {}", userId, action, ex.getMessage());
        }
    }
}

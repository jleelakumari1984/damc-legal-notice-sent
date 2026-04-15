package com.damc.legalnotices.service.user.impl;

import com.damc.legalnotices.config.SmsCredential;
import com.damc.legalnotices.config.WhatsAppCredential;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.user.UserSmsCredentialDto;
import com.damc.legalnotices.dto.user.UserWhatsAppCredentialDto;
import com.damc.legalnotices.entity.user.UserSmsCredentialEntity;
import com.damc.legalnotices.entity.user.UserWhatsAppCredentialEntity;
import com.damc.legalnotices.repository.user.UserSmsCredentialRepository;
import com.damc.legalnotices.repository.user.UserWhatsAppCredentialRepository;
import com.damc.legalnotices.service.user.UserCredentialService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCredentialServiceImpl implements UserCredentialService {

    private final UserSmsCredentialRepository smsCredentialRepository;
    private final UserWhatsAppCredentialRepository whatsAppCredentialRepository;

    @Override
    public void saveSmsCredential(LoginUserDao sessionUser, Long userId, UserSmsCredentialDto dto) {
        UserSmsCredentialEntity entity = smsCredentialRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserSmsCredentialEntity e = new UserSmsCredentialEntity();
                    e.setUserId(userId);
                    e.setCreatedBy(sessionUser.getId());
                    return e;
                });
        entity.setUrl(dto.getUrl());
        entity.setUserName(dto.getUserName());
        entity.setPassword(dto.getPassword());
        entity.setLive(dto.getLive());
        entity.setTestMobileNumber(dto.getTestMobileNumber());
        entity.setUpdatedBy(sessionUser.getId());
        smsCredentialRepository.save(entity);
    }

    @Override
    public void saveWhatsAppCredential(LoginUserDao sessionUser, Long userId, UserWhatsAppCredentialDto dto) {
        UserWhatsAppCredentialEntity entity = whatsAppCredentialRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserWhatsAppCredentialEntity e = new UserWhatsAppCredentialEntity();
                    e.setUserId(userId);
                    e.setCreatedBy(sessionUser.getId());
                    return e;
                });
        entity.setUrl(dto.getUrl());
        entity.setAccessToken(dto.getAccessToken());
        entity.setAttachmentDownloadUrl(dto.getAttachmentDownloadUrl());
        entity.setLive(dto.getLive());
        entity.setTestMobileNumber(dto.getTestMobileNumber());
        entity.setUpdatedBy(sessionUser.getId());
        whatsAppCredentialRepository.save(entity);
    }

    @Override
    public Optional<SmsCredential> getSmsCredential(Long userId) {
        if (userId == null) return Optional.empty();
        return smsCredentialRepository.findByUserId(userId).map(e -> SmsCredential.builder()
                .url(e.getUrl())
                .userName(e.getUserName())
                .password(e.getPassword())
                .live(Boolean.TRUE.equals(e.getLive()))
                .testMobileNumber(e.getTestMobileNumber())
                .build());
    }

    @Override
    public Optional<WhatsAppCredential> getWhatsAppCredential(Long userId) {
        if (userId == null) return Optional.empty();
        return whatsAppCredentialRepository.findByUserId(userId).map(e -> WhatsAppCredential.builder()
                .url(e.getUrl())
                .accessToken(e.getAccessToken())
                .attachmentDownloadUrl(e.getAttachmentDownloadUrl())
                .live(Boolean.TRUE.equals(e.getLive()))
                .testMobileNumber(e.getTestMobileNumber())
                .build());
    }
}

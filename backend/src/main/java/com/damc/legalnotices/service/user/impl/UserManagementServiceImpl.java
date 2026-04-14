package com.damc.legalnotices.service.user.impl;

import com.damc.legalnotices.dto.user.UserRequestDto;
import com.damc.legalnotices.dto.user.UserResponseDto;
import com.damc.legalnotices.dto.user.UserUpdateDto;
import com.damc.legalnotices.entity.user.LoginDetailEntity;
import com.damc.legalnotices.repository.user.LoginDetailRepository;
import com.damc.legalnotices.service.user.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final LoginDetailRepository loginDetailRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto request) {
        
        if (loginDetailRepository.existsByLoginName(request.getLoginName())) {
            throw new IllegalArgumentException("Login name already exists: " + request.getLoginName());
        }
        if (loginDetailRepository.existsByUserEmail(request.getUserEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getUserEmail());
        }
        LoginDetailEntity entity = new LoginDetailEntity();
        entity.setDisplayName(request.getDisplayName());
        entity.setUserCode(request.getUserCode());
        entity.setLoginName(request.getLoginName());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setUserEmail(request.getUserEmail());
        entity.setUserMobileSms(request.getUserMobileSms());
        entity.setUserMobileWhatsapp(request.getUserMobileWhatsapp());
        entity.setAccessLevel(request.getAccessLevel());
        entity.setEnabled(true);
        LoginDetailEntity saved = loginDetailRepository.save(entity);
        log.info("Created user: {}", saved.getLoginName());
        return toDto(saved);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        return toDto(findById(id));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return loginDetailRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto request) {
        LoginDetailEntity entity = findById(id);
        if (request.getDisplayName() != null) entity.setDisplayName(request.getDisplayName());
        if (request.getUserEmail() != null) entity.setUserEmail(request.getUserEmail());
        if (request.getUserMobileSms() != null) entity.setUserMobileSms(request.getUserMobileSms());
        if (request.getUserMobileWhatsapp() != null) entity.setUserMobileWhatsapp(request.getUserMobileWhatsapp());
        if (request.getAccessLevel() != null) entity.setAccessLevel(request.getAccessLevel());
        if (request.getEnabled() != null) entity.setEnabled(request.getEnabled());
        return toDto(loginDetailRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        LoginDetailEntity entity = findById(id);
        entity.setEnabled(false);
        loginDetailRepository.save(entity);
        log.info("Disabled user id: {}", id);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String newPassword) {
        LoginDetailEntity entity = findById(id);
        entity.setPassword(passwordEncoder.encode(newPassword));
        loginDetailRepository.save(entity);
        log.info("Password changed for user id: {}", id);
    }

    private LoginDetailEntity findById(Long id) {
        return loginDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    private UserResponseDto toDto(LoginDetailEntity e) {
        return UserResponseDto.builder()
                .id(e.getId())
                .displayName(e.getDisplayName())
                .loginName(e.getLoginName())
                .userEmail(e.getUserEmail())
                .userMobileSms(e.getUserMobileSms())
                .userMobileWhatsapp(e.getUserMobileWhatsapp())
                .accessLevel(e.getAccessLevel())
                .enabled(e.getEnabled())
                .lastLoginDate(e.getLastLoginDate())
                .build();
    }
}

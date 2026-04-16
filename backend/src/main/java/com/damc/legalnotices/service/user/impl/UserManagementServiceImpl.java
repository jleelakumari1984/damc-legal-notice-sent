package com.damc.legalnotices.service.user.impl;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.DatatableDto;
import com.damc.legalnotices.dto.user.UserListDto;
import com.damc.legalnotices.dto.user.UserRequestDto;
import com.damc.legalnotices.dto.user.UserResponseDto;
import com.damc.legalnotices.dto.user.UserUpdateDto;
import com.damc.legalnotices.entity.user.UserActivityLogEntity;
import com.damc.legalnotices.entity.user.UserEntity;
import com.damc.legalnotices.enums.UserActivityType;
import com.damc.legalnotices.repository.user.UserActivityLogRepository;
import com.damc.legalnotices.repository.user.UserRepository;
import com.damc.legalnotices.service.user.UserManagementService;
import com.damc.legalnotices.util.converter.UserEntityDaoConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityDaoConverter daoConverter;
    private final UserActivityLogRepository activityLogRepository;

    @Override
    @Transactional
    public UserResponseDto createUser(LoginUserDao sessionUser, UserRequestDto request) {

        if (userRepository.existsByLoginName(request.getLoginName())) {
            throw new IllegalArgumentException("Login name already exists: " + request.getLoginName());
        }
        if (userRepository.existsByUserEmail(request.getUserEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getUserEmail());
        }
        UserEntity entity = new UserEntity();
        entity.setDisplayName(request.getDisplayName());
        entity.setLoginName(request.getLoginName());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setUserEmail(request.getUserEmail());
        entity.setUserMobileSms(request.getUserMobileSms());
        entity.setUserMobileWhatsapp(request.getUserMobileWhatsapp());
        entity.setAccessLevel(request.getAccessLevel());
        entity.setEnabled(true);
        entity.setCreatedAt(LocalDateTime.now());
        UserEntity saved = userRepository.save(entity);
        log.info("Created user: {}", saved.getLoginName());
        logActivity(saved.getId(), sessionUser.getId(), UserActivityType.CREATE,
                "User created: loginName=" + saved.getLoginName() + ", accessLevel=" + saved.getAccessLevel());
        return daoConverter.toDto(saved);
    }

    @Override
    public UserResponseDto getUserById(LoginUserDao sessionUser, Long id) {
        return daoConverter.toDto(findById(id));
    }

    @Override
    public DataTableDao<List<UserResponseDto>> getAllUsers(LoginUserDao sessionUser,
            DatatableDto<UserListDto> request) {
        UserListDto filter = request.getFilter();
        Page<UserEntity> page;
        Pageable pageable = request == null || request.isAllData() ? Pageable.unpaged() : request.getPagination("id");
        if (filter != null
                && (filter.getSearch() != null || filter.getAccessLevel() != null || filter.getEnabled() != null)) {
            String search = (filter.getSearch() != null && !filter.getSearch().isBlank()) ? filter.getSearch().trim()
                    : null;
            page = userRepository.findAllWithCreditsFiltered(search, filter.getAccessLevel(), filter.getEnabled(),
                    pageable);

        } else {
            page = userRepository.findAllWithCredits(pageable);
        }
        return DataTableDao.<List<UserResponseDto>>builder()
                .draw(request.getDraw())
                .recordsTotal(page.getTotalElements())
                .recordsFiltered(page.getNumberOfElements())
                .data(page.getContent().stream().map(daoConverter::toDto).toList())
                .build();
    }

    @Override
    public boolean loginNameExists(String loginName, Long excludeId) {
        if (excludeId != null) {
            return userRepository.existsByLoginNameAndIdNot(loginName, excludeId);
        }
        return userRepository.existsByLoginName(loginName);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(LoginUserDao sessionUser, Long id, UserUpdateDto request) {
        UserEntity entity = findById(id);
        if (request.getLoginName() != null) {
            if (userRepository.existsByLoginNameAndIdNot(request.getLoginName(), id)) {
                throw new IllegalArgumentException("Login name already exists: " + request.getLoginName());
            }
            entity.setLoginName(request.getLoginName());
        }
        if (request.getDisplayName() != null)
            entity.setDisplayName(request.getDisplayName());
        if (request.getUserEmail() != null)
            entity.setUserEmail(request.getUserEmail());
        if (request.getUserMobileSms() != null)
            entity.setUserMobileSms(request.getUserMobileSms());
        if (request.getUserMobileWhatsapp() != null)
            entity.setUserMobileWhatsapp(request.getUserMobileWhatsapp());
        if (request.getAccessLevel() != null)
            entity.setAccessLevel(request.getAccessLevel());
        if (request.getEnabled() != null)
            entity.setEnabled(request.getEnabled());
        entity.setUpdatedAt(LocalDateTime.now());
        UserEntity updated = userRepository.save(entity);
        logActivity(id, sessionUser.getId(), UserActivityType.UPDATE, buildUpdateDescription(request));
        return daoConverter.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteUser(LoginUserDao sessionUser, Long id) {
        UserEntity entity = findById(id);
        entity.setEnabled(false);
        userRepository.save(entity);
        log.info("Disabled user id: {}", id);
        logActivity(id, sessionUser.getId(), UserActivityType.DELETE, "User disabled (soft delete), userId=" + id);
    }

    @Override
    @Transactional
    public void changePassword(LoginUserDao sessionUser, Long id, String newPassword) {
        UserEntity entity = findById(id);
        entity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(entity);
        log.info("Password changed for user id: {}", id);
        logActivity(id, sessionUser.getId(), UserActivityType.CHANGE_PASSWORD, "Password changed for userId=" + id);
    }

    private UserEntity findById(Long id) {
        return userRepository.findByIdWithCredit(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Override
    public UserResponseDto toggleUserStatus(LoginUserDao sessionUser, Long id) {
        UserEntity entity = findById(id);
        if (entity.getEnabled() == null || entity.getEnabled().equals(true)) {
            entity.setEnabled(false);
        } else {
            entity.setEnabled(true);
        }
        userRepository.save(entity);
        log.info("User status updated for user id: {}", id);
        logActivity(id, sessionUser.getId(), UserActivityType.TOGGLE_STATUS,
                "User status toggled to " + (Boolean.TRUE.equals(entity.getEnabled()) ? "ENABLED" : "DISABLED")
                        + " for userId=" + id);
        return daoConverter.toDto(entity);
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

    private String buildUpdateDescription(UserUpdateDto request) {
        StringBuilder sb = new StringBuilder("Fields updated:");
        if (request.getDisplayName() != null)
            sb.append(" displayName='").append(request.getDisplayName()).append("'");
        if (request.getUserEmail() != null)
            sb.append(" userEmail='").append(request.getUserEmail()).append("'");
        if (request.getUserMobileSms() != null)
            sb.append(" mobileSms='").append(request.getUserMobileSms()).append("'");
        if (request.getUserMobileWhatsapp() != null)
            sb.append(" mobileWhatsapp='").append(request.getUserMobileWhatsapp()).append("'");
        if (request.getAccessLevel() != null)
            sb.append(" accessLevel=").append(request.getAccessLevel());
        if (request.getEnabled() != null)
            sb.append(" enabled=").append(request.getEnabled());
        return sb.toString();
    }

}

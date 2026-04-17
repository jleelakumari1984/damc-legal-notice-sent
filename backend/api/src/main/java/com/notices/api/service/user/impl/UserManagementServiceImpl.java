package com.notices.api.service.user.impl;

import com.notices.domain.dao.DataTableDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dao.user.UserDao;
import com.notices.domain.dto.DatatableDto;
import com.notices.domain.dto.user.UserListDto;
import com.notices.domain.dto.user.UserRequestDto;
import com.notices.domain.dto.user.UserUpdateDto;
import com.notices.domain.entity.user.UserActivityLogEntity;
import com.notices.domain.entity.user.UserEntity;
import com.notices.domain.enums.UserAccessLevelEnum;
import com.notices.domain.enums.UserActivityType;
import com.notices.domain.repository.user.UserActivityLogRepository;
import com.notices.domain.repository.user.UserRepository;
import com.notices.api.annotation.RequiresAccess;
import com.notices.api.service.user.UserManagementService;
import com.notices.domain.util.converter.UserEntityDaoConverter;

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
@RequiresAccess(level = UserAccessLevelEnum.SUPER_ADMIN) // all user-management endpoints require admin (level 1)
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityDaoConverter daoConverter;
    private final UserActivityLogRepository activityLogRepository;

    @Override
    @Transactional
    public UserDao createUser(LoginUserDao sessionUser, UserRequestDto request) {

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
        return daoConverter.toDao(saved);
    }

    @Override
    public UserDao getUserById(LoginUserDao sessionUser, Long id) {
        return daoConverter.toDao(findById(id));
    }

    @Override
    public DataTableDao<List<UserDao>> getAllUsers(LoginUserDao sessionUser,
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
        return DataTableDao.<List<UserDao>>builder()
                .draw(request.getDraw())
                .recordsTotal(page.getTotalElements())
                .recordsFiltered(page.getNumberOfElements())
                .data(page.getContent().stream().map(daoConverter::toDao).toList())
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
    public UserDao updateUser(LoginUserDao sessionUser, Long id, UserUpdateDto request) {
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
        return daoConverter.toDao(updated);
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
    public UserDao toggleUserStatus(LoginUserDao sessionUser, Long id) {
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
        return daoConverter.toDao(entity);
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

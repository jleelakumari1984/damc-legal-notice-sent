package com.notices.api.controller.user;

import com.notices.api.annotation.RequiresAccess;
import com.notices.domain.dto.user.LoginNameCheckDto;
import com.notices.domain.dao.DataTableDao;
import com.notices.domain.dao.user.UserDao;
import com.notices.domain.dto.DatatableDto;
import com.notices.domain.dto.user.UserListDto;
import com.notices.domain.dto.user.UserPasswordUpdateDto;
import com.notices.domain.dto.user.UserRequestDto;
import com.notices.domain.dto.user.UserUpdateDto;
import com.notices.domain.enums.UserAccessLevelEnum;
import com.notices.api.service.BaseService;
import com.notices.api.service.user.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@RequiresAccess(level = UserAccessLevelEnum.SUPER_ADMIN) // all user-management endpoints require admin (level 1)
public class UserManagementController {

    private final UserManagementService userManagementService;
    private final BaseService baseService;

    @PostMapping
    public ResponseEntity<UserDao> createUser(@Valid @RequestBody UserRequestDto request) {
        log.info("Create user request for login name: {}", request.getLoginName());
        return ResponseEntity.ok(userManagementService.createUser(baseService.getSessionUser(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDao> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.getUserById(baseService.getSessionUser(), id));
    }

    @PostMapping("/list")
    public ResponseEntity<DataTableDao<List<UserDao>>> getAllUsers(
            @RequestBody DatatableDto<UserListDto> request) {
        return ResponseEntity.ok(userManagementService.getAllUsers(baseService.getSessionUser(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDao> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserUpdateDto request) {
        log.info("Update user request for id: {}", id);
        return ResponseEntity.ok(userManagementService.updateUser(baseService.getSessionUser(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Delete (disable) user request for id: {}", id);
        userManagementService.deleteUser(baseService.getSessionUser(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
            @Valid @RequestBody UserPasswordUpdateDto request) {
        userManagementService.changePassword(baseService.getSessionUser(), id, request.getPassword());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-enabled")
    public ResponseEntity<UserDao> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.toggleUserStatus(baseService.getSessionUser(), id));
    }

    @PostMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> loginNameExists(@Valid @RequestBody LoginNameCheckDto request) {
        boolean exists = userManagementService.loginNameExists(request.getLoginName(), request.getExcludeId());
        return ResponseEntity.ok(Map.of("exists", exists));
    }

}

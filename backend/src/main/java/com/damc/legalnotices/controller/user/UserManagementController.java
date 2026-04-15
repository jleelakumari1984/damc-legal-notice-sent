package com.damc.legalnotices.controller.user;

import com.damc.legalnotices.annotation.RequiresAccess;
import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dto.user.UserListDto;
import com.damc.legalnotices.dto.user.UserPasswordUpdateDto;
import com.damc.legalnotices.dto.user.UserRequestDto;
import com.damc.legalnotices.dto.user.UserResponseDto;
import com.damc.legalnotices.dto.user.UserUpdateDto;
import com.damc.legalnotices.enums.UserAccessLevelEnum;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.user.UserManagementService;
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
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto request) {
        log.info("Create user request for login name: {}", request.getLoginName());
        return ResponseEntity.ok(userManagementService.createUser(baseService.getSessionUser(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.getUserById(baseService.getSessionUser(), id));
    }

    @PostMapping("/list")
    public ResponseEntity<DataTableDao<List<UserResponseDto>>> getAllUsers(
            @RequestBody UserListDto request) {
        return ResponseEntity.ok(userManagementService.getAllUsers(baseService.getSessionUser(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id,
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
    public ResponseEntity<UserResponseDto> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.toggleUserStatus(baseService.getSessionUser(), id));
    }

}

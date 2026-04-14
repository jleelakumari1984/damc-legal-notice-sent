package com.damc.legalnotices.service.user;

import com.damc.legalnotices.dto.user.UserRequestDto;
import com.damc.legalnotices.dto.user.UserResponseDto;
import com.damc.legalnotices.dto.user.UserUpdateDto;

import java.util.List;

public interface UserManagementService {

    UserResponseDto createUser(UserRequestDto request);

    UserResponseDto getUserById(Long id);

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUser(Long id, UserUpdateDto request);

    void deleteUser(Long id);

    void changePassword(Long id, String newPassword);
}

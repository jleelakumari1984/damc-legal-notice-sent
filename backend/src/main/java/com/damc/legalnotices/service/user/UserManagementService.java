package com.damc.legalnotices.service.user;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.user.UserListDto;
import com.damc.legalnotices.dto.user.UserRequestDto;
import com.damc.legalnotices.dto.user.UserResponseDto;
import com.damc.legalnotices.dto.user.UserUpdateDto;

import java.util.List;

public interface UserManagementService {

    UserResponseDto createUser(LoginUserDao  sessionUser, UserRequestDto request);

    UserResponseDto getUserById(LoginUserDao  sessionUser, Long id);

    DataTableDao<List<UserResponseDto>> getAllUsers(LoginUserDao sessionUser, UserListDto request);

    UserResponseDto updateUser(LoginUserDao  sessionUser, Long id, UserUpdateDto request);

    void deleteUser(LoginUserDao  sessionUser, Long id);

    void changePassword(LoginUserDao  sessionUser, Long id, String newPassword);

    UserResponseDto toggleUserStatus(LoginUserDao sessionUser, Long id);
}

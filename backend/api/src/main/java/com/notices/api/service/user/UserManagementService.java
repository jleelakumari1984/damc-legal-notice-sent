package com.notices.api.service.user;

import com.notices.domain.dao.DataTableDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dao.user.UserDao;
import com.notices.domain.dto.DatatableDto;
import com.notices.domain.dto.user.UserListDto;
import com.notices.domain.dto.user.UserRequestDto;
import com.notices.domain.dto.user.UserUpdateDto;

import java.util.List;

public interface UserManagementService {

    UserDao createUser(LoginUserDao sessionUser, UserRequestDto request);

    UserDao getUserById(LoginUserDao sessionUser, Long id);

    DataTableDao<List<UserDao>> getAllUsers(LoginUserDao sessionUser, DatatableDto<UserListDto> request);

    UserDao updateUser(LoginUserDao sessionUser, Long id, UserUpdateDto request);

    void deleteUser(LoginUserDao sessionUser, Long id);

    void changePassword(LoginUserDao sessionUser, Long id, String newPassword);

    UserDao toggleUserStatus(LoginUserDao sessionUser, Long id);

    boolean loginNameExists(String loginName, Long excludeId);
}

package com.notices.api.dao;

import java.util.Collection;
import java.util.List;

import com.notices.domain.dao.user.LoginUserDao;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class SessionUserDao implements UserDetails {
    private final LoginUserDao userDao;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_ACCESS_"
                + (userDao != null ? (userDao.getAccessLevel() == null ? 0L : userDao.getAccessLevel()) : 0L);
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return userDao != null ? userDao.getPassword() : null;
    }

    @Override
    public String getUsername() {
        return userDao != null ? userDao.getLoginName() : null;
    }

    public Long getId() {
        return userDao != null ? userDao.getId() : null;
    }

}

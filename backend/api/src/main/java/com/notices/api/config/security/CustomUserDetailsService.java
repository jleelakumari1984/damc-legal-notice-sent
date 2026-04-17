package com.notices.api.config.security;

import com.notices.api.dao.SessionUserDao;

import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.entity.user.UserEntity;
import com.notices.domain.repository.user.UserRepository;
import com.notices.domain.util.converter.EntityDaoConverter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EntityDaoConverter entityDaoConverter;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByLoginNameAndEnabledTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username"));

        LoginUserDao userDao = entityDaoConverter.toLoginUserDao(user);

        return new SessionUserDao(userDao);
    }
}

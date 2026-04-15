package com.damc.legalnotices.config.security;

import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dao.user.SessionUserDao;
import com.damc.legalnotices.entity.user.UserEntity;
import com.damc.legalnotices.repository.user.UserRepository;
import com.damc.legalnotices.util.converter.EntityDaoConverter;

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

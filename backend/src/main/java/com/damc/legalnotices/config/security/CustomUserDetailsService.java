package com.damc.legalnotices.config.security;

import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dao.user.SessionUserDao;
import com.damc.legalnotices.entity.user.LoginDetailEntity;
import com.damc.legalnotices.repository.user.LoginDetailRepository;
import com.damc.legalnotices.util.EntityDaoConverter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginDetailRepository loginDetailRepository;
    private final EntityDaoConverter entityDaoConverter;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginDetailEntity user = loginDetailRepository.findByLoginNameAndEnabledTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username"));

        
        LoginUserDao userDao = entityDaoConverter.toLoginUserDao(user);

        return new SessionUserDao(userDao);
    }
}

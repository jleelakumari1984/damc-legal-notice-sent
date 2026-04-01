package com.damc.legalnotices.security;

import com.damc.legalnotices.entity.LoginDetail;
import com.damc.legalnotices.repository.LoginDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginDetailRepository loginDetailRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginDetail user = loginDetailRepository.findByLoginNameAndEnabledTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username"));

        String role = "ROLE_ACCESS_" + (user.getAccessLevel() == null ? 0L : user.getAccessLevel());
        return new User(user.getLoginName(), user.getPassword(), List.of(new SimpleGrantedAuthority(role)));
    }
}

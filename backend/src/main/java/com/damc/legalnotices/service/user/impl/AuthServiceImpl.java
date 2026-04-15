package com.damc.legalnotices.service.user.impl;

import com.damc.legalnotices.dao.user.LoginDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.user.LoginDto;
import com.damc.legalnotices.entity.user.LoginDetailEntity;
import com.damc.legalnotices.repository.user.LoginDetailRepository;
import com.damc.legalnotices.service.user.AuthService;
import com.damc.legalnotices.util.JwtUtil;
import com.damc.legalnotices.util.converter.EntityDaoConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final LoginDetailRepository loginDetailRepository;
    private final JwtUtil jwtUtil;
    private final EntityDaoConverter entityDaoConverter;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginDao login(LoginDto requestDto) {
        try {
            log.info("Authenticating user: {} ,{}", requestDto.getUsername(), passwordEncoder.encode("abc123"));
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword()));
        } catch (Exception ex) {
            log.warn("Authentication failed for user: {} {}", requestDto.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        LoginDetailEntity loginDetail = loginDetailRepository.findByLoginNameAndEnabledTrue(requestDto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        LoginUserDao userDao = entityDaoConverter.toLoginUserDao(loginDetail);
        // User springUser = new User(loginDetail.getLoginName(),
        // loginDetail.getPassword(), Collections.emptyList());
        String token = jwtUtil.generateToken(userDao);
        log.info("JWT issued for user: {}", loginDetail.getLoginName());
        return new LoginDao(token, userDao);
    }
}

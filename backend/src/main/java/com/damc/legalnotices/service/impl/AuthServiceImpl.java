package com.damc.legalnotices.service.impl;

import com.damc.legalnotices.dao.LoginUserDao;
import com.damc.legalnotices.dto.LoginRequestDto;
import com.damc.legalnotices.dto.LoginResponseDto;
import com.damc.legalnotices.entity.LoginDetailEntity;
import com.damc.legalnotices.repository.LoginDetailRepository;
import com.damc.legalnotices.service.AuthService;
import com.damc.legalnotices.util.EntityDaoConverter;
import com.damc.legalnotices.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
    public LoginResponseDto login(LoginRequestDto requestDto) {
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

        User springUser = new User(loginDetail.getLoginName(), loginDetail.getPassword(), Collections.emptyList());
        String token = jwtUtil.generateToken(springUser);
        LoginUserDao userDao = entityDaoConverter.toLoginUserDao(loginDetail);
        log.info("JWT issued for user: {}", loginDetail.getLoginName());
        return new LoginResponseDto(token, userDao);
    }
}

package com.notices.api.service.user.impl;

import com.notices.api.service.user.AuthService;
import com.notices.api.util.JwtUtil;

import com.notices.domain.dao.user.LoginDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.user.LoginDto;
import com.notices.domain.dto.user.SwitchSessionDto;
import com.notices.domain.entity.user.UserEntity;
import com.notices.domain.repository.user.UserRepository;
import com.notices.domain.util.converter.EntityDaoConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final EntityDaoConverter entityDaoConverter;
    private final PasswordEncoder passwordEncoder;

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

        UserEntity loginDetail = userRepository.findByLoginNameAndEnabledTrue(requestDto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        LoginUserDao userDao = entityDaoConverter.toLoginUserDao(loginDetail);
        // User springUser = new User(loginDetail.getLoginName(),
        // loginDetail.getPassword(), Collections.emptyList());
        if (userDao.isAdmin()) {
            userDao.setCanSwitchSession(true);
        }
        String token = jwtUtil.generateToken(userDao);
        log.info("JWT issued for user: {}", loginDetail.getLoginName());
        return new LoginDao(token, userDao);
    }

    @Override
    public LoginDao switchSession(LoginUserDao sessionUser, SwitchSessionDto request) {
        boolean canSwitch = sessionUser.isAdmin()
                || Boolean.TRUE.equals(sessionUser.getCanSwitchSession());
        if (!canSwitch) {
            throw new AccessDeniedException("You do not have permission to switch sessions");
        }
        UserEntity targetDetail = userRepository.findByIdWithCredit(request.getTargetUserId())
                .orElseThrow(() -> new BadCredentialsException(
                        "Target user not found with id: " + request.getTargetUserId()));
        LoginUserDao targetUserDao = entityDaoConverter.toLoginUserDao(targetDetail);
        targetUserDao.setCanSwitchSession(true); // allow switching back to original session
        String token = jwtUtil.generateToken(targetUserDao);
        log.info("Session switched to user: {} by: {}", targetDetail.getLoginName(), sessionUser.getLoginName());
        return new LoginDao(token, targetUserDao);
    }
}

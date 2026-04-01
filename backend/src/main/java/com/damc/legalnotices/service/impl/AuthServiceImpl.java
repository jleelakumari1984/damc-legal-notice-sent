package com.damc.legalnotices.service.impl;

import com.damc.legalnotices.dao.LoginUserDao;
import com.damc.legalnotices.dto.LoginRequestDto;
import com.damc.legalnotices.dto.LoginResponseDto;
import com.damc.legalnotices.entity.LoginDetail;
import com.damc.legalnotices.repository.LoginDetailRepository;
import com.damc.legalnotices.service.AuthService;
import com.damc.legalnotices.util.EntityDaoConverter;
import com.damc.legalnotices.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final LoginDetailRepository loginDetailRepository;
    private final JwtUtil jwtUtil;
    private final EntityDaoConverter entityDaoConverter;

    @Override
    public LoginResponseDto login(LoginRequestDto requestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword())
            );
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid username or password");
        }

        LoginDetail loginDetail = loginDetailRepository.findByLoginNameAndEnabledTrue(requestDto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        User springUser = new User(loginDetail.getLoginName(), loginDetail.getPassword(), Collections.emptyList());
        String token = jwtUtil.generateToken(springUser);
        LoginUserDao userDao = entityDaoConverter.toLoginUserDao(loginDetail);
        return new LoginResponseDto(token, userDao);
    }
}

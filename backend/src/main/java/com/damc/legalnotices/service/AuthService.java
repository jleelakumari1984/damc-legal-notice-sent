package com.damc.legalnotices.service;

import com.damc.legalnotices.dto.LoginRequestDto;
import com.damc.legalnotices.dto.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto requestDto);
}

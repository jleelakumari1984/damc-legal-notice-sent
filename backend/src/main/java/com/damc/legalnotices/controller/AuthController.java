package com.damc.legalnotices.controller;

import com.damc.legalnotices.dto.LoginRequestDto;
import com.damc.legalnotices.dto.LoginResponseDto;
import com.damc.legalnotices.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }
}

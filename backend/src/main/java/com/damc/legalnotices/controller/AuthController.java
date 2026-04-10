package com.damc.legalnotices.controller;

import com.damc.legalnotices.dto.LoginRequestDto;
import com.damc.legalnotices.dto.LoginResponseDto;
import com.damc.legalnotices.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        log.info("Login attempt for user: {}", requestDto.getUsername());
        LoginResponseDto response = authService.login(requestDto);
        log.info("Login successful for user: {}", requestDto.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Service is running");
    }

}

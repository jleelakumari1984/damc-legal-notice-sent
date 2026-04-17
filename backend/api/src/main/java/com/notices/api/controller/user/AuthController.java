package com.notices.api.controller.user;

import com.notices.domain.dao.user.LoginDao;
import com.notices.domain.dto.user.LoginDto;
import com.notices.domain.dto.user.SwitchSessionDto;
import com.notices.api.service.BaseService;
import com.notices.api.service.user.AuthService;
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
    private final BaseService baseService;

    @PostMapping("/login")
    public ResponseEntity<LoginDao> login(@Valid @RequestBody LoginDto requestDto) {
        log.info("Login attempt for user: {}", requestDto.getUsername());
        LoginDao response = authService.login(requestDto);
        log.info("Login successful for user: {}", requestDto.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Service is running");
    }

    @PostMapping("/switch-session")
    public ResponseEntity<LoginDao> switchSession(@Valid @RequestBody SwitchSessionDto request) {
        return ResponseEntity.ok(authService.switchSession(baseService.getSessionUser(), request));
    }

}

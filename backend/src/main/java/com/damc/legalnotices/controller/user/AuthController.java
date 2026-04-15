package com.damc.legalnotices.controller.user;

import com.damc.legalnotices.dao.user.LoginDao;
import com.damc.legalnotices.dto.user.LoginDto;
import com.damc.legalnotices.dto.user.SwitchSessionDto;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.user.AuthService;
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

package com.damc.legalnotices.controller.user;

import com.damc.legalnotices.dao.user.UserSmsCredentialDao;
import com.damc.legalnotices.dao.user.UserWhatsAppCredentialDao;
import com.damc.legalnotices.dto.user.UserSmsCredentialDto;
import com.damc.legalnotices.dto.user.UserWhatsAppCredentialDto;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.user.UserCredentialService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/endpoint")
@RequiredArgsConstructor
public class UserCredentialController {

    private final BaseService baseService;
    private final UserCredentialService userCredentialService;

    @GetMapping("/sms/{userId}")
    public ResponseEntity<UserSmsCredentialDao> getSmsCredential(@PathVariable Long userId) {
        return ResponseEntity.ok(userCredentialService.getSmsCredential(baseService.getSessionUser(), userId));
    }

    @PutMapping("/sms/{userId}")
    public ResponseEntity<Void> saveSmsCredential(
            @PathVariable Long userId,
            @Valid @RequestBody UserSmsCredentialDto request) {
        userCredentialService.saveSmsCredential(baseService.getSessionUser(), userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/whatsapp/{userId}")
    public ResponseEntity<UserWhatsAppCredentialDao> getWhatsAppCredential(@PathVariable Long userId) {
        return ResponseEntity.ok(userCredentialService.getWhatsAppCredential(baseService.getSessionUser(), userId));
    }

    @PutMapping("/whatsapp/{userId}")
    public ResponseEntity<Void> saveWhatsAppCredential(
            @PathVariable Long userId,
            @Valid @RequestBody UserWhatsAppCredentialDto request) {
        userCredentialService.saveWhatsAppCredential(baseService.getSessionUser(), userId, request);
        return ResponseEntity.ok().build();
    }
}

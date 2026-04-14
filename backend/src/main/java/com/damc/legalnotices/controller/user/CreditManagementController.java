package com.damc.legalnotices.controller.user;

import com.damc.legalnotices.dao.user.CreditTransactionDao;
import com.damc.legalnotices.dto.user.CreditAdjustDto;
import com.damc.legalnotices.entity.user.LoginDetailEntity;
import com.damc.legalnotices.repository.user.LoginDetailRepository;
import com.damc.legalnotices.service.user.CreditManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditManagementController {

    private final CreditManagementService creditManagementService;
    private final LoginDetailRepository loginDetailRepository;

    @PostMapping
    public ResponseEntity<CreditTransactionDao> createCredit(
            @Valid @RequestBody CreditAdjustDto request,
            @AuthenticationPrincipal UserDetails principal) {
        Long performedBy = resolveUserId(principal);
        log.info("Create credit request for userId: {} channel: {} by: {}",
                request.getUserId(), request.getChannel(), principal.getUsername());
        return ResponseEntity.ok(creditManagementService.createCredit(request, performedBy));
    }

    @GetMapping
    public ResponseEntity<List<CreditTransactionDao>> getAllTransactions() {
        return ResponseEntity.ok(creditManagementService.getAllTransactions());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CreditTransactionDao>> getTransactionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(creditManagementService.getTransactionsByUserId(userId));
    }

    private Long resolveUserId(UserDetails principal) {
        return loginDetailRepository.findByLoginNameAndEnabledTrue(principal.getUsername())
                .map(LoginDetailEntity::getId)
                .orElse(0L);
    }
}

package com.damc.legalnotices.controller.user;

import com.damc.legalnotices.annotation.RequiresAccess;
import com.damc.legalnotices.dao.user.CreditTransactionDao;
import com.damc.legalnotices.dto.user.CreditAdjustDto;
import com.damc.legalnotices.enums.UserAccessLevelEnum;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.user.CreditManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    private final BaseService baseService;

    @RequiresAccess(level = UserAccessLevelEnum.SUPER_ADMIN)
    @PostMapping
    public ResponseEntity<CreditTransactionDao> createCredit(
            @Valid @RequestBody CreditAdjustDto request) {
        Long performedBy = baseService.getSessionUser().getId();
        log.info("Create credit request for userId: {} channel: {} by: {}",
                request.getUserId(), request.getChannel(), baseService.getSessionUser().getUserDao().getDisplayName());
        return ResponseEntity.ok(creditManagementService.createCredit(request, performedBy));
    }

    @RequiresAccess(level = UserAccessLevelEnum.SUPER_ADMIN)
    @GetMapping
    public ResponseEntity<List<CreditTransactionDao>> getAllTransactions() {
        return ResponseEntity.ok(creditManagementService.getAllTransactions());
    }

    @RequiresAccess(level = UserAccessLevelEnum.SUPER_ADMIN)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CreditTransactionDao>> getTransactionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(creditManagementService.getTransactionsByUserId(userId));
    }

    @GetMapping("/user/me")
    public ResponseEntity<List<CreditTransactionDao>> getTransactionsByCurrentUser() {
        Long userId = baseService.getSessionUser().getId();
        return ResponseEntity.ok(creditManagementService.getTransactionsByUserId(userId));
    }

}

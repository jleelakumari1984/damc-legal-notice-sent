package com.damc.legalnotices.controller.user;

import com.damc.legalnotices.annotation.RequiresAccess;
import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.user.CreditTransactionDao;
import com.damc.legalnotices.dto.DatatableDto;
import com.damc.legalnotices.dto.user.CreditAdjustDto;
import com.damc.legalnotices.dto.user.CreditTransactionListDto;
import com.damc.legalnotices.enums.UserAccessLevelEnum;
import com.damc.legalnotices.service.BaseService;
import com.damc.legalnotices.service.user.CreditManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users/credits")
@RequiredArgsConstructor
public class CreditManagementController {

    private final CreditManagementService creditManagementService;
    private final BaseService baseService;

    @RequiresAccess(level = UserAccessLevelEnum.SUPER_ADMIN)
    @PostMapping
    public ResponseEntity<CreditTransactionDao> createCredit(
            @Valid @RequestBody CreditAdjustDto request) {
        log.info("Create credit request for userId: {} channel: {} by: {}",
                request.getUserId(), request.getChannel(), baseService.getSessionUser().getDisplayName());
        return ResponseEntity.ok(creditManagementService.createCredit(baseService.getSessionUser(), request));
    }

    @RequiresAccess(level = UserAccessLevelEnum.SUPER_ADMIN)
    @PostMapping("/trans/list")
    public ResponseEntity<DataTableDao<List<CreditTransactionDao>>> getAllTransactions(
            @RequestBody DatatableDto<CreditTransactionListDto> request) {
        return ResponseEntity.ok(creditManagementService.getAllTransactions(baseService.getSessionUser(), request));
    }

    @RequiresAccess(level = UserAccessLevelEnum.SUPER_ADMIN)
    @PostMapping("/trans/{userId}")
    public ResponseEntity<DataTableDao<List<CreditTransactionDao>>> getTransactionsByUser(
            @PathVariable Long userId, @RequestBody DatatableDto<CreditTransactionListDto> request) {
        if (request.getFilter() == null) {
            request.setFilter(new CreditTransactionListDto());
        }
        request.getFilter().setUserid(userId);
        return getAllTransactions(request);
    }

    @PostMapping("/trans/me")
    public ResponseEntity<DataTableDao<List<CreditTransactionDao>>> getTransactionsByCurrentUser(
            @RequestBody DatatableDto<CreditTransactionListDto> request) {
        Long userId = baseService.getSessionUser().getId();
        return getTransactionsByUser(userId, request);
    }

}

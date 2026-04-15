package com.damc.legalnotices.service.user.impl;

import com.damc.legalnotices.dao.user.CreditTransactionDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.user.CreditAdjustDto;
import com.damc.legalnotices.entity.user.CreditTransactionEntity;
import com.damc.legalnotices.entity.user.LoginDetailEntity;
import com.damc.legalnotices.repository.user.CreditTransactionRepository;
import com.damc.legalnotices.repository.user.LoginDetailRepository;
import com.damc.legalnotices.service.user.CreditManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditManagementServiceImpl implements CreditManagementService {

    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final LoginDetailRepository loginDetailRepository;
    private final CreditTransactionRepository creditTransactionRepository;

    @Override
    @Transactional
    public CreditTransactionDao createCredit(LoginUserDao  sessionUser, CreditAdjustDto request) {
        LoginDetailEntity user = findUser(request.getUserId());
        CreditTransactionEntity tx = new CreditTransactionEntity();
        tx.setUser(user);
        tx.setChannel(request.getChannel().toUpperCase());
        tx.setCredits(request.getCredits());
        tx.setPricePerUnit(request.getPricePerUnit());
        tx.setType(request.getType().toUpperCase());
        tx.setDescription(request.getDescription());
        tx.setStatus("COMPLETED");
        tx.setCreatedBy(sessionUser.getId());
        CreditTransactionEntity saved = creditTransactionRepository.save(tx);
        log.info("Credit transaction created for user {}: channel={}, credits={}, type={}",
                user.getLoginName(), saved.getChannel(), saved.getCredits(), saved.getType());
        return toDto(saved);
    }

    @Override
    public List<CreditTransactionDao> getAllTransactions(LoginUserDao  sessionUser) {
        return creditTransactionRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public List<CreditTransactionDao> getTransactionsByUserId(Long userId) {
        findUser(userId);
        return creditTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDto).toList();
    }

    private LoginDetailEntity findUser(Long id) {
        return loginDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    private CreditTransactionDao toDto(CreditTransactionEntity e) {
        return CreditTransactionDao.builder()
                .id(e.getId())
                .userId(e.getUser().getId())
                .userName(e.getUser().getDisplayName())
                .channel(e.getChannel())
                .credits(e.getCredits())
                .pricePerUnit(e.getPricePerUnit())
                .type(e.getType())
                .description(e.getDescription())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt() != null ? e.getCreatedAt().format(ISO_FMT) : null)
                .build();
    }
}

package com.damc.legalnotices.service.user.impl;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.user.CreditTransactionDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.user.CreditAdjustDto;
import com.damc.legalnotices.dto.user.CreditTransactionListDto;
import com.damc.legalnotices.entity.user.UserCreditEntity;
import com.damc.legalnotices.entity.user.UserCreditTransactionEntity;
import com.damc.legalnotices.entity.user.UserEntity;
import com.damc.legalnotices.repository.user.UserCreditRepository;
import com.damc.legalnotices.repository.user.UserCreditTransactionRepository;
import com.damc.legalnotices.repository.user.UserRepository;
import com.damc.legalnotices.service.user.CreditManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditManagementServiceImpl implements CreditManagementService {

    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final UserRepository userRepository;
    private final UserCreditTransactionRepository userCreditTransactionRepository;
    private final UserCreditRepository userCreditRepository;

    @Override
    @Transactional
    public CreditTransactionDao createCredit(LoginUserDao sessionUser, CreditAdjustDto request) {
        UserEntity user = findUser(request.getUserId());
        UserCreditTransactionEntity tx = new UserCreditTransactionEntity();
        tx.setUser(user);
        tx.setChannel(request.getChannel().toString());
        tx.setCredits(request.getCredits());
        tx.setPricePerUnit(request.getPricePerUnit());
        tx.setType(request.getType().toString());
        tx.setDescription(request.getDescription());
        tx.setStatus("COMPLETED");
        tx.setCreatedBy(sessionUser.getId());
        UserCreditTransactionEntity saved = userCreditTransactionRepository.save(tx);
        UserCreditEntity creditEntity = user.getCredits();
        if (creditEntity == null) {
            creditEntity = new UserCreditEntity();
            creditEntity.setUser(user);
            creditEntity.setMailCredits(0L);
            creditEntity.setSmsCredits(0L);
            creditEntity.setWhatsappCredits(0L);
        }
        switch (request.getChannel()) {
            case MAIL -> creditEntity.setMailCredits(creditEntity.getMailCredits() + request.getCredits());
            case SMS -> creditEntity.setSmsCredits(creditEntity.getSmsCredits() + request.getCredits());
            case WHATSAPP -> creditEntity.setWhatsappCredits(creditEntity.getWhatsappCredits() + request.getCredits());
        }
        userCreditRepository.save(creditEntity);
        log.info("Credit transaction created for user {}: channel={}, credits={}, type={}",
                user.getLoginName(), saved.getChannel(), saved.getCredits(), saved.getType());
        return toDto(saved);
    }

    @Override
    public DataTableDao<List<CreditTransactionDao>> getAllTransactions(LoginUserDao sessionUser,
            CreditTransactionListDto request) {
        Page<UserCreditTransactionEntity> page = null;
        if (request.getUserid() != null && request.getUserid() > 0L) {
            findUser(request.getUserid());
            page = userCreditTransactionRepository.findByUserId(request.getUserid(), request.getPagination());
        } else {
            page = userCreditTransactionRepository.findAll(request.getPagination());
        }
        return DataTableDao.<List<CreditTransactionDao>>builder()
                .draw(request.getDraw())
                .recordsTotal(page.getTotalElements())
                .recordsFiltered(page.getNumberOfElements())
                .data(page.getContent().stream().map(this::toDto).toList())
                .build();
    }

    private UserEntity findUser(Long id) {
        return userRepository.findByIdWithCredit(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    private CreditTransactionDao toDto(UserCreditTransactionEntity e) {
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

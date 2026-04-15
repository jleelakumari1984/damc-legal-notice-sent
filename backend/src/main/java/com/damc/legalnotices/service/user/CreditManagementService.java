package com.damc.legalnotices.service.user;

import com.damc.legalnotices.dao.user.CreditTransactionDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.user.CreditAdjustDto;

import java.util.List;

public interface CreditManagementService {

    CreditTransactionDao createCredit(LoginUserDao  sessionUser, CreditAdjustDto request);

    List<CreditTransactionDao> getAllTransactions(LoginUserDao  sessionUser);

    List<CreditTransactionDao> getTransactionsByUserId(Long userId);
}

package com.damc.legalnotices.service.user;

import com.damc.legalnotices.dao.user.CreditTransactionDao;
import com.damc.legalnotices.dto.user.CreditAdjustDto;

import java.util.List;

public interface CreditManagementService {

    CreditTransactionDao createCredit(CreditAdjustDto request, Long performedByUserId);

    List<CreditTransactionDao> getAllTransactions();

    List<CreditTransactionDao> getTransactionsByUserId(Long userId);
}

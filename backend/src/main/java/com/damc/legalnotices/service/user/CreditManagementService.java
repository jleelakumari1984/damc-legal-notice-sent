package com.damc.legalnotices.service.user;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.user.CreditTransactionDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.user.CreditAdjustDto;
import com.damc.legalnotices.dto.user.CreditTransactionListDto;

import java.util.List;

public interface CreditManagementService {

    CreditTransactionDao createCredit(LoginUserDao sessionUser, CreditAdjustDto request);

    DataTableDao<List<CreditTransactionDao>> getAllTransactions(LoginUserDao sessionUser, CreditTransactionListDto request);
}

package com.notices.api.service.user;

import com.notices.domain.dao.DataTableDao;
import com.notices.domain.dao.user.CreditTransactionDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.DatatableDto;
import com.notices.domain.dto.user.CreditAdjustDto;
import com.notices.domain.dto.user.CreditTransactionListDto;

import java.util.List;

public interface CreditManagementService {

    CreditTransactionDao createCredit(LoginUserDao sessionUser, CreditAdjustDto request);

    DataTableDao<List<CreditTransactionDao>> getAllTransactions(LoginUserDao sessionUser,
            DatatableDto<CreditTransactionListDto> request);
}

package com.notices.api.service.excel;

import com.notices.api.dto.excel.ExcelPreviewDto;
import com.notices.domain.dao.excel.ExcelPreviewDao;
import com.notices.domain.dao.user.LoginUserDao;


public interface ExcelService {

    ExcelPreviewDao previewExcel(LoginUserDao  sessionUser, ExcelPreviewDto request);

}

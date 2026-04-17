package com.damc.legalnotices.service.excel;

import com.damc.legalnotices.dao.excel.ExcelPreviewDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.excel.ExcelPreviewDto;


public interface ExcelService {

    ExcelPreviewDao previewExcel(LoginUserDao  sessionUser, ExcelPreviewDto request);

}

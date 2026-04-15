package com.damc.legalnotices.service.excel;

import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.excel.ExcelPreviewDto;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {

    ExcelPreviewDto previewExcel(LoginUserDao  sessionUser, MultipartFile zipFile);

}

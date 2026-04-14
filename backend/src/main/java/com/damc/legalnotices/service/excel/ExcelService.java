package com.damc.legalnotices.service.excel;

import com.damc.legalnotices.dto.excel.ExcelPreviewDto;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface ExcelService {

    ExcelPreviewDto previewExcel(MultipartFile zipFile);

    ExcelPreviewDto parseExcelPreview(Path excelPath);
}

package com.notices.api.dto.excel;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelPreviewDto {

    @NotNull(message = "noticeSno is required")
    private Long noticeSno;

    @NotNull(message = "zipFile is required")
    private MultipartFile zipFile;
}
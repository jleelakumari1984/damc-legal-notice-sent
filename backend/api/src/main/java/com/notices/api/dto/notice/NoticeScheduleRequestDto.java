package com.notices.api.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class NoticeScheduleRequestDto {

    @NotNull(message = "noticeSno is required")
    private Long noticeSno;

    @NotNull(message = "sendSms is required")
    private Boolean sendSms;

    @NotNull(message = "sendWhatsapp is required")
    private Boolean sendWhatsapp;

    @NotNull(message = "zipFile is required")
    private MultipartFile zipFile;
}

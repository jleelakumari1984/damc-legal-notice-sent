package com.damc.legalnotices.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeWhatsappRejectDto {

    @NotNull
    private String rejectReason;

}

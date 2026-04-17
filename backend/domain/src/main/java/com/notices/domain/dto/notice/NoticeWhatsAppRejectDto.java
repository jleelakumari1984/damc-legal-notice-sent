package com.notices.domain.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeWhatsAppRejectDto {

    @NotNull
    private String rejectReason;

}

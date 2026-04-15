package com.damc.legalnotices.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeSmsRejectDto {

    @NotNull
    private String rejectReason;

}

package com.damc.legalnotices.dto.notice;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeNameCheckDto {

    @NotBlank
    private String name;

    private Long excludeId; // for update: exclude the current notice type's own id
}

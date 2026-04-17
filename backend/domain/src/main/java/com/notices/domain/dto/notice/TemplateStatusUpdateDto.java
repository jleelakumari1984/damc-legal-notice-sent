package com.notices.domain.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemplateStatusUpdateDto {

    @NotNull
    private Integer status;
}

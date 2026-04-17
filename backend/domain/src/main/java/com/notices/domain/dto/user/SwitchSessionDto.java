package com.notices.domain.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwitchSessionDto {

    @NotNull
    private Long targetUserId;
}

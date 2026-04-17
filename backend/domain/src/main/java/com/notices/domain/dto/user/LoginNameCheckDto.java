package com.notices.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginNameCheckDto {

    @NotBlank
    private String loginName;

    private Long excludeId; // for update: exclude the current user's own id
}

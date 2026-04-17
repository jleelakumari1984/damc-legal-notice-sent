package com.notices.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordUpdateDto {

    @NotBlank
    @Size(min = 6)
    private String password;
}

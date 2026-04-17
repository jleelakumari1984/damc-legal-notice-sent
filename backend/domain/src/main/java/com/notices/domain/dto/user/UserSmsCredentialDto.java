package com.notices.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSmsCredentialDto {

    @NotBlank
    private String url;

    @NotBlank
    private String userName;

    private String password;

    @NotNull
    private Boolean live;

    private String testMobileNumber;
}

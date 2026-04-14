package com.damc.legalnotices.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

    @NotBlank
    @Size(max = 100)
    private String displayName;

    @Size(max = 20)
    private String userCode;

    @NotBlank
    @Size(min = 3, max = 50)
    private String loginName;

    @NotBlank
    @Size(min = 6)
    private String password;

    @Email
    private String userEmail;

    @Size(max = 15)
    private String userMobileSms;

    @Size(max = 15)
    private String userMobileWhatsapp;

    @NotNull
    private Long accessLevel;
}

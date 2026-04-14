package com.damc.legalnotices.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {

    @Size(max = 100)
    private String displayName;

    @Email
    private String userEmail;

    @Size(max = 15)
    private String userMobileSms;

    @Size(max = 15)
    private String userMobileWhatsapp;

    private Long accessLevel;

    private Boolean enabled;
}

package com.notices.domain.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {

    @Size(min = 3, max = 50)
    private String loginName;

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

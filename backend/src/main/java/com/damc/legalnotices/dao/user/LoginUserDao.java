package com.damc.legalnotices.dao.user;

import com.damc.legalnotices.enums.UserAccessLevelEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginUserDao {
    private Long id;
    @JsonIgnore
    private String password;
    private String displayName;
    private String loginName;
    private String userEmail;
    private String mobileSms;
    private String mobileWhatsapp;
    private Long accessLevel;
    private Boolean canSwitchSession;

    public boolean isSuperAdmin() {
        return accessLevel != null && accessLevel == UserAccessLevelEnum.SUPER_ADMIN.getLevel();
    }

    public boolean isAdmin() {
        return accessLevel != null && accessLevel <= UserAccessLevelEnum.ADMIN.getLevel();
    }
}

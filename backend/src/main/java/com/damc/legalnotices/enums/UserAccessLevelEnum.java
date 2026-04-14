package com.damc.legalnotices.enums;

public enum UserAccessLevelEnum {
    SUPER_ADMIN(1),
    ADMIN(2),
    USER(3);

    private final int level;

    UserAccessLevelEnum(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}

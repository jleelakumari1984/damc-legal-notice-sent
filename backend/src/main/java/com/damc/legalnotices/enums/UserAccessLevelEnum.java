package com.damc.legalnotices.enums;

public enum UserAccessLevelEnum {
    SUPER_ADMIN(1L),
    ADMIN(2L),
    USER(3L);

    private final long level;

    UserAccessLevelEnum(long level) {
        this.level = level;
    }

    public long getLevel() {
        return level;
    }
}

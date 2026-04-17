package com.notices.domain.enums;

public enum TemplateStatus {
    INACTIVE(0),
    ACTIVE(1);

    private final int value;

    TemplateStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

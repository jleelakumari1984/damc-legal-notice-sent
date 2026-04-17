package com.notices.domain.enums;

public enum TemplateApproveStatus {
    PENDING(0),
    APPROVED(1),
    REJECTED(3);

    private final int value;

    TemplateApproveStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

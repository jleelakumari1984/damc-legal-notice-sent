package com.damc.legalnotices.dao.notification;

import java.time.Instant;

import org.thymeleaf.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendSmsDao {
    private String sendTo;

    private String message;

    private Instant sendAt;

    @JsonIgnore
    private Integer sendStatus;

    private String sendResponse;

    private String ackId;

    private String receivedStatus;

    private Instant receivedAt;

    private Long createdBy;

    private Instant createdAt;

    private String errorMessage;

    public String getReceivedStatus() {
        if (receivedStatus != null && !StringUtils.isEmptyOrWhitespace(receivedStatus)) {
            return receivedStatus;
        }
        switch (sendStatus) {
            case 0:
                return "Pending";
            case 1:
                return "Sent";
            case 2:
                return "Scheduled";
            default:
                return "Unknown";
        }
    }
}

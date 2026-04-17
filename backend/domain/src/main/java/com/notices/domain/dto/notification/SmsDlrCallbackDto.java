package com.notices.domain.dto.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsDlrCallbackDto {

    @JsonProperty("messageid")
    private String messageId;

    @JsonProperty("dlrstatus")
    private String dlrStatus;

    @JsonProperty("msisdn")
    private String msisdn;

    @JsonProperty("senderid")
    private String senderId;

    @JsonProperty("submittime")
    private String submitTime;

    @JsonProperty("delivtime")
    private String delivTime;

    @JsonProperty("dlrcode")
    private String dlrCode;
}

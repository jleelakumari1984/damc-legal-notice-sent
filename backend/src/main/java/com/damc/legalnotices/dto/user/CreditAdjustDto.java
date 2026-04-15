package com.damc.legalnotices.dto.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import com.damc.legalnotices.enums.CreditChannelType;
import com.damc.legalnotices.enums.CreditTransactionType;

@Getter
@Setter
public class CreditAdjustDto {

    @NotNull
    private Long userId;

    @NotNull
    private CreditChannelType channel;

    @NotNull
    @Min(1)
    private Long credits;

    private BigDecimal pricePerUnit;

    @NotNull
    private CreditTransactionType type;

    private String description;
}

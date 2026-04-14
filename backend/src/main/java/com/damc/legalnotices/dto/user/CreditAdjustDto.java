package com.damc.legalnotices.dto.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreditAdjustDto {

    @NotNull
    private Long userId;

    @NotBlank
    private String channel;

    @NotNull
    @Min(1)
    private Long credits;

    private BigDecimal pricePerUnit;

    @NotBlank
    private String type;

    private String description;
}

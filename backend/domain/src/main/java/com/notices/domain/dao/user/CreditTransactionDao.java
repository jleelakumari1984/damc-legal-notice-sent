package com.notices.domain.dao.user;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CreditTransactionDao {
    private Long id;
    private Long userId;
    private String userName;
    private String channel;
    private Long credits;
    private BigDecimal pricePerUnit;
    private String type;
    private String description;
    private String status;
    private String createdAt;
}

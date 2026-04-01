package com.damc.legalnotices.dao;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessTemplateDao {
    private Long id;
    private String name;
}

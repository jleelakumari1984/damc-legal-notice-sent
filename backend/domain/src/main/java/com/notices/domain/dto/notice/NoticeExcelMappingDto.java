package com.notices.domain.dto.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeExcelMappingDto {

    @NotNull(message = "noticeId is required")
    private Long noticeId;

    @NotBlank(message = "excelFieldName is required")
    private String excelFieldName;

    private Integer isAgreement;

    private Integer isCustomerName;

    private Integer isMobile;

    private Integer isMandatory;

    private Integer isAttachment;

    public String getDbFieldName() {
        return excelFieldName.toLowerCase().replaceAll("\\s+", "_").replaceAll("[^a-z0-9_]", "");
    }
}

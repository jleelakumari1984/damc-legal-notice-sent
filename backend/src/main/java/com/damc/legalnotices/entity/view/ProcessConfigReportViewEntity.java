package com.damc.legalnotices.entity.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Getter
@Entity
@Immutable
@Table(name = "process_config_report")
public class ProcessConfigReportViewEntity {

    @Id
    @Column(name = "sno")
    private Long sno;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_user_name")
    private String createdUserName;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;

    @Column(name = "step_name")
    private String stepName;

    @Column(name = "excel_map_count")
    private Long excelMapCount;

    @Column(name = "sms_map_count")
    private Long smsMapCount;

    @Column(name = "whatsapp_map_count")
    private Long whatsappMapCount;

    @Column(name = "mail_map_count")
    private Long mailMapCount;
}

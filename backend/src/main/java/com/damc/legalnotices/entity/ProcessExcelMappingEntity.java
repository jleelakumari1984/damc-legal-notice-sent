package com.damc.legalnotices.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "process_excel_mapping")
public class ProcessExcelMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sno")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "process_id")
    private MasterProcessTemplateDetailEntity process;

    @Column(name = "excel_field_name", nullable = false)
    private String excelFieldName;

    @Column(name = "db_field_name")
    private String dbFieldName;

    @Column(name = "is_key")
    private Integer isKey;

    @Column(name = "is_mobile")
    private Integer isMobile;
    
    @Column(name = "is_mandatory")
    private Integer isMandatory;

    @Column(name = "is_attachment")
    private Integer isAttachment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

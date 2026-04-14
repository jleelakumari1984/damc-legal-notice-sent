package com.damc.legalnotices.entity.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "status_report_whatsapp")
public class StatusReportWhatsappEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sno")
    private Long id;

    @Column(name = "request_params", columnDefinition = "tinytext")
    private String requestParams;

    @Column(name = "request_body", columnDefinition = "tinytext")
    private String requestBody;

    @Column(name = "status", length = 45)
    private String status = "0";

    @Column(name = "created_date", insertable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "process_date")
    private LocalDateTime processDate;

    @Column(name = "complete_date")
    private LocalDateTime completeDate;

    @Column(name = "description", length = 45)
    private String description;
}

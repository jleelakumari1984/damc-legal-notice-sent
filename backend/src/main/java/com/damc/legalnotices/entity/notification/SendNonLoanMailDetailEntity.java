package com.damc.legalnotices.entity.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "send_non_loan_mail_details")
public class SendNonLoanMailDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sno")
    private Long id;

    @Column(name = "type", length = 45)
    private String type;

    @Column(name = "schedule_sno")
    private Long scheduleSno;

    @Column(name = "process_sno")
    private Long processSno;

    @Column(name = "mail_template_sno")
    private Long mailTemplateSno;

    @Column(name = "send_to", columnDefinition = "longtext")
    private String sendTo;

    @Column(name = "send_cc", columnDefinition = "longtext")
    private String sendCc;

    @Column(name = "subject", length = 300)
    private String subject;

    @Column(name = "message", columnDefinition = "longtext")
    private String message;

    @Column(name = "number_of_message")
    private Integer numberOfMessage;

    @Column(name = "send_at")
    private Instant sendAt;

    @Column(name = "send_status")
    private Integer sendStatus;

    @Column(name = "send_response", columnDefinition = "longtext")
    private String sendResponse;

    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

package com.damc.legalnotices.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "send_loan_whatsapp_details")
public class SendLoanWhatsappDetailEntity {

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

    @Column(name = "whatsapp_template_sno")
    private Long whatsappTemplateSno;

    @Column(name = "loan_no")
    private Long loanNo;

    @Column(name = "send_to", length = 15)
    private String sendTo;

    @Column(name = "message", columnDefinition = "longtext")
    private String message;

    @Column(name = "send_at")
    private Instant sendAt;

    @Column(name = "send_status")
    private Integer sendStatus;

    @Column(name = "send_response", columnDefinition = "longtext")
    private String sendResponse;

    @Column(name = "ack_id")
    private String ackId;

    @Column(name = "received_status", length = 50)
    private String receivedStatus;

    @Column(name = "received_at")
    private Instant receivedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;
}

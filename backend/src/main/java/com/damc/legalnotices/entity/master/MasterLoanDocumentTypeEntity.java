package com.damc.legalnotices.entity.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "master_loan_document_types")
public class MasterLoanDocumentTypeEntity {

    @Id
    @Column(name = "sno")
    private Long id;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "access_level_sno")
    private Integer accessLevelSno;

    @Column(name = "access_level_order")
    private Integer accessLevelOrder;

    @Column(name = "name")
    private String name;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "doc_short_name")
    private String docShortName;

    @Column(name = "`order`")
    private Integer order;

    @Column(name = "mandatory")
    private Boolean mandatory;

    @Column(name = "mandatory_second_upload")
    private Boolean mandatorySecondUpload;

    @Column(name = "depend_doc_sno")
    private Long dependDocSno;

    @Column(name = "wait_days")
    private Integer waitDays;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "has_envelope")
    private Boolean hasEnvelope;

    @Column(name = "has_ack_card")
    private Boolean hasAckCard;
}

package com.damc.legalnotices.entity.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "master_process_doc_config_details")
public class MasterProcessDocConfigDetailEntity {

    @Id
    @Column(name = "sno")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doc_sno")
    private MasterLoanDocumentTypeEntity document;

    @ManyToOne
    @JoinColumn(name = "process_sno")
    private MasterProcessTemplateDetailEntity process;

    @ManyToOne
    @JoinColumn(name = "hearing_stage_sno")
    private MasterHearingStageEntity hearingStage;

    @Column(name = "template_path")
    private String templatePath;

    @Column(name = "envelope_path")
    private String envelopePath;

    @Column(name = "post_card_path")
    private String postCardPath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

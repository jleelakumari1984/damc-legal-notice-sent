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
@Table(name = "master_process_mail_config_details")
public class MasterProcessMailConfigDetailEntity {

    @Id
    @Column(name = "sno")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "process_sno")
    private MasterProcessTemplateDetailEntity process;

    @ManyToOne
    @JoinColumn(name = "hearing_stage_sno")
    private MasterHearingStageEntity hearingStage;

    @Column(name = "sent_level")
    private Integer sentLevel;

    @Column(name = "mail_subject")
    private String mailSubject;

    @Column(name = "template_path")
    private String templatePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status")
    private Integer status;
}

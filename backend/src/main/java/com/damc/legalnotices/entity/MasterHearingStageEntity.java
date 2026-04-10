package com.damc.legalnotices.entity;

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
@Table(name = "master_hearing_stages")
public class MasterHearingStageEntity {

    @Id
    @Column(name = "sno")
    private Long id;

    @Column(name = "stage_series")
    private String stageSeries;

    @Column(name = "stage_number")
    private Integer stageNumber;

    @Column(name = "stage_title")
    private String stageTitle;

    @Column(name = "description")
    private String description;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

package com.notices.domain.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {

    @OneToOne(mappedBy = "user")
    private UserCreditEntity credits;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sno")
    private Long id;

    @Column(name = "access_level")
    private Long accessLevel;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "login_name")
    private String loginName;

    @Column(name = "password")
    private String password;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_mobile_sms")
    private String userMobileSms;

    @Column(name = "user_mobile_whatsapp")
    private String userMobileWhatsapp;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

}

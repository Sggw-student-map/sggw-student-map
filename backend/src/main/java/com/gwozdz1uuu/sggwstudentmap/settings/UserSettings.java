package com.gwozdz1uuu.sggwstudentmap.settings;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_settings")
@Data
public class UserSettings {
    @Id
    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "private_account")
    private Boolean privateAccount = false;

    @Column(name = "gdpr_consent")
    private Boolean gdprConsent = true;
}
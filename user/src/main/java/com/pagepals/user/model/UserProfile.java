package com.pagepals.user.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="user_profile")
public class UserProfile {

    @Id
    private long id;

    @Column(nullable = false)
    private String pseudo;

    @Column(length = 1000)
    private String bio;

    private String photoProfil;

    @Column(nullable = false)
    private LocalDate dateInscription;
}

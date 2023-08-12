package com.example.wpob.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class Users extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email; // login id

    @Column(name = "password")
    private String password; // bcrypt

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt = null;

    public static Users create(String email, String password) {
        return Users.builder()
                .email(email)
                .password(password)
                .build();
    }

}

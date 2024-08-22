package com.checkout.server.db.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tokens")
@Data
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;
}
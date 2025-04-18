package com.example.GerenciadorPortfolios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String role; // "gerente" ou "funcionario"

    // Campos para simular API externa
    private Long externalId;
    private boolean active = true;
}
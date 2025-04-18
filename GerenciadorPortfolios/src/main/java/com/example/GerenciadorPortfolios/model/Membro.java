package com.example.GerenciadorPortfolios.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "membros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String atribuicao;

    @Column(nullable = false)
    private String identificadorExterno;

    @ManyToMany(mappedBy = "membros")
    private Set<Projeto> projetos = new HashSet<>();
}
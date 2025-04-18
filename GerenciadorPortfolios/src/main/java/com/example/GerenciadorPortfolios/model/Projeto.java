package com.example.GerenciadorPortfolios.model;


import com.example.GerenciadorPortfolios.model.enums.ClassificacaoRisco;
import com.example.GerenciadorPortfolios.model.enums.StatusProjeto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projetos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Projeto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "previsao_termino")
    private LocalDate previsaoTermino;

    @Column(name = "data_real_termino")
    private LocalDate dataRealTermino;

    @Column(nullable = false)
    private BigDecimal orcamentoTotal;

    @Column(length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    private StatusProjeto status;

    @Transient
    private ClassificacaoRisco classificacaoRisco;

    @ManyToOne
    @JoinColumn(name = "gerente_id", nullable = false)
    private Membro gerente;

    @ManyToMany
    @JoinTable(
            name = "projeto_membros",
            joinColumns = @JoinColumn(name = "projeto_id"),
            inverseJoinColumns = @JoinColumn(name = "membro_id")
    )
    private Set<Membro> membros = new HashSet<>();

    @PostLoad
    private void calcularClassificacaoRisco() {
        long mesesDuracao = java.time.temporal.ChronoUnit.MONTHS.between(
                dataInicio, previsaoTermino
        );

        if (orcamentoTotal.compareTo(new BigDecimal("100000")) <= 0 && mesesDuracao <= 3) {
            classificacaoRisco = ClassificacaoRisco.BAIXO;
        } else if ((orcamentoTotal.compareTo(new BigDecimal("100000")) > 0 &&
                orcamentoTotal.compareTo(new BigDecimal("500000")) <= 0) ||
                (mesesDuracao > 3 && mesesDuracao <= 6)) {
            classificacaoRisco = ClassificacaoRisco.MEDIO;
        } else {
            classificacaoRisco = ClassificacaoRisco.ALTO;
        }
    }
}
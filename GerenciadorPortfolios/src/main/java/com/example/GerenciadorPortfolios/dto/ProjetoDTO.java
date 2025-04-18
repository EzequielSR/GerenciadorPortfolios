package com.example.GerenciadorPortfolios.dto;

import com.example.GerenciadorPortfolios.model.enums.ClassificacaoRisco;
import com.example.GerenciadorPortfolios.model.enums.StatusProjeto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoDTO {
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotNull(message = "Data de início é obrigatória")
    @FutureOrPresent(message = "Data de início deve ser hoje ou no futuro")
    private LocalDate dataInicio;

    @NotNull(message = "Previsão de término é obrigatória")
    @Future(message = "Previsão de término deve ser no futuro")
    private LocalDate previsaoTermino;

    private LocalDate dataRealTermino;

    @NotNull(message = "Orçamento total é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Orçamento deve ser maior que zero")
    private BigDecimal orcamentoTotal;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    private StatusProjeto status;

    private ClassificacaoRisco classificacaoRisco;

    @NotNull(message = "Gerente é obrigatório")
    private Long gerenteId;

    @Size(min = 1, max = 10, message = "Projeto deve ter entre 1 e 10 membros")
    private Set<Long> membrosIds;
}
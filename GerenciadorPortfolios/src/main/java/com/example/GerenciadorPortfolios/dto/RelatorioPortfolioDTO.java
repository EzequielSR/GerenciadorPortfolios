package com.example.GerenciadorPortfolios.dto;

import com.example.GerenciadorPortfolios.model.enums.StatusProjeto;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioPortfolioDTO {
    private Map<StatusProjeto, Long> quantidadeProjetosPorStatus;
    private Map<StatusProjeto, BigDecimal> totalOrcadoPorStatus;
    private Double mediaDuracaoProjetosEncerrados;
    private Long totalMembrosUnicosAlocados;
}

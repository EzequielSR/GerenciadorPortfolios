package com.example.GerenciadorPortfolios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembroDTO {
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Atribuição é obrigatória")
    private String atribuicao;

    private String identificadorExterno;
}
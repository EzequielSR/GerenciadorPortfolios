package com.example.GerenciadorPortfolios.model.enums;

public enum ClassificacaoRisco {
        BAIXO("Baixo Risco"),
        MEDIO("Médio Risco"),
        ALTO("Alto Risco");

        private final String descricao;

        ClassificacaoRisco(String descricao) {
                this.descricao = descricao;
        }

        public String getDescricao() {
                return descricao;
        }
}

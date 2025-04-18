package com.example.GerenciadorPortfolios.config;

import com.example.GerenciadorPortfolios.model.Membro;
import com.example.GerenciadorPortfolios.model.Projeto;
import com.example.GerenciadorPortfolios.model.enums.StatusProjeto;
import com.example.GerenciadorPortfolios.repository.MembroRepository;
import com.example.GerenciadorPortfolios.repository.ProjetoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataInitializer {

    private final MembroRepository membroRepository;
    private final ProjetoRepository projetoRepository;

    @PostConstruct
    @Transactional
    public void init() {
        try {
            if (membroRepository.count() == 0 && projetoRepository.count() == 0) {
                criarDadosIniciais();

                // Teste de consulta
                testarConsultas();
            }
        } catch (Exception e) {
            System.err.println("Erro ao inicializar dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void criarDadosIniciais() {
        // Cria membros
        Membro gerente = Membro.builder()
                .nome("João Silva")
                .atribuicao("gerente")
                .identificadorExterno("ger-001")
                .build();

        Membro funcionario1 = Membro.builder()
                .nome("Maria Souza")
                .atribuicao("funcionario")
                .identificadorExterno("func-001")
                .build();

        Membro funcionario2 = Membro.builder()
                .nome("Carlos Oliveira")
                .atribuicao("funcionario")
                .identificadorExterno("func-002")
                .build();

        List<Membro> membrosSalvos = membroRepository.saveAll(List.of(
                gerente, funcionario1, funcionario2
        ));

        // Cria projetos
        Projeto projeto1 = Projeto.builder()
                .nome("Sistema de Gestão de Projetos")
                .dataInicio(LocalDate.now())
                .previsaoTermino(LocalDate.now().plusMonths(3))
                .orcamentoTotal(new BigDecimal("150000.00"))
                .descricao("Desenvolvimento do sistema de gestão interno")
                .status(StatusProjeto.EM_ANALISE)
                .gerente(gerente)
                .membros(new HashSet<>(Set.of(funcionario1, funcionario2)))
                .build();

        Projeto projeto2 = Projeto.builder()
                .nome("Portal do Cliente")
                .dataInicio(LocalDate.now().minusDays(15))
                .previsaoTermino(LocalDate.now().plusMonths(2))
                .dataRealTermino(LocalDate.now().plusMonths(1)) // Adicionado para teste de média
                .orcamentoTotal(new BigDecimal("80000.00"))
                .descricao("Desenvolvimento do portal para clientes")
                .status(StatusProjeto.ENCERRADO) // Alterado para testar a consulta
                .gerente(gerente)
                .membros(new HashSet<>(Set.of(funcionario1)))
                .build();

        projetoRepository.saveAll(List.of(projeto1, projeto2));
    }

    private void testarConsultas() {
        System.out.println("Testando consultas...");

        // Testa consulta de média
        Double media = projetoRepository.calcularMediaDuracaoProjetosEncerrados();
        System.out.println("Média de duração (dias): " + media);

        // Testa outras consultas
        System.out.println("Projetos por status: " + projetoRepository.countProjetosByStatus());
        System.out.println("Total orçado por status: " + projetoRepository.sumOrcamentoByStatus());
    }
}

package com.example.GerenciadorPortfolios.service;

import com.example.GerenciadorPortfolios.dto.ProjetoDTO;
import com.example.GerenciadorPortfolios.dto.RelatorioPortfolioDTO;
import com.example.GerenciadorPortfolios.exception.OperacaoNaoPermitidaException;
import com.example.GerenciadorPortfolios.exception.RecursoNaoEncontradoException;
import com.example.GerenciadorPortfolios.exception.TransicaoStatusInvalidaException;
import com.example.GerenciadorPortfolios.exception.ValidacaoException;
import com.example.GerenciadorPortfolios.mapper.ProjetoMapper;
import com.example.GerenciadorPortfolios.model.Membro;
import com.example.GerenciadorPortfolios.model.Projeto;
import com.example.GerenciadorPortfolios.model.enums.StatusProjeto;
import com.example.GerenciadorPortfolios.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjetoService {
    private final ProjetoRepository projetoRepository;
    private final MembroService membroService;
    private final ProjetoMapper projetoMapper;

    @Transactional(readOnly = true)
    public Page<ProjetoDTO> listarTodos(String nome, Pageable pageable) {
        Page<Projeto> projetos = nome != null ?
                projetoRepository.findByNomeContainingIgnoreCase(nome, pageable) :
                projetoRepository.findAll(pageable);
        return projetos.map(projetoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @SneakyThrows({RecursoNaoEncontradoException.class})
    public ProjetoDTO buscarPorId(Long id){
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado com ID: " + id));
        return projetoMapper.toDTO(projeto);
    }

    @Transactional
    @SneakyThrows
    public ProjetoDTO criar(ProjetoDTO projetoDTO) {
        validarTransicaoStatus(null, projetoDTO.getStatus());

        Projeto projeto = projetoMapper.toEntity(projetoDTO);
        projeto.setStatus(StatusProjeto.EM_ANALISE);

        Membro gerente = membroService.buscarMembroPorId(projetoDTO.getGerenteId());
        validarGerente(gerente);
        projeto.setGerente(gerente);

        if (projetoDTO.getMembrosIds() != null && !projetoDTO.getMembrosIds().isEmpty()) {
            Set<Membro> membros = projetoDTO.getMembrosIds().stream()
                    .map(membroService::buscarMembroPorId)
                    .peek(this::validarMembro)
                    .collect(Collectors.toSet());
            projeto.setMembros(membros);
        }

        Projeto projetoSalvo = projetoRepository.save(projeto);
        return projetoMapper.toDTO(projetoSalvo);
    }

    @Transactional
    @SneakyThrows
    public ProjetoDTO atualizar(Long id, ProjetoDTO projetoDTO)  {
        Projeto projetoExistente = projetoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado com ID: " + id));

        validarEdicaoProjeto(projetoExistente);
        validarTransicaoStatus(projetoExistente.getStatus(), projetoDTO.getStatus());

        projetoMapper.toEntity(projetoDTO, projetoExistente);

        if (projetoDTO.getGerenteId() != null) {
            Membro gerente = membroService.buscarMembroPorId(projetoDTO.getGerenteId());
            validarGerente(gerente);
            projetoExistente.setGerente(gerente);
        }

        if (projetoDTO.getMembrosIds() != null) {
            Set<Membro> membros = projetoDTO.getMembrosIds().stream()
                    .map(membroService::buscarMembroPorId)
                    .peek(this::validarMembro)
                    .collect(Collectors.toSet());
            projetoExistente.setMembros(membros);
        }

        Projeto projetoAtualizado = projetoRepository.save(projetoExistente);
        return projetoMapper.toDTO(projetoAtualizado);
    }


    @Transactional
    @SneakyThrows
    public void excluir(Long id) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado com ID: " + id));

        if (projeto.getStatus().ordinal() >= StatusProjeto.INICIADO.ordinal() &&
                projeto.getStatus() != StatusProjeto.CANCELADO) {
            throw new OperacaoNaoPermitidaException("Não é possível excluir projetos com status " + projeto.getStatus().getDescricao());
        }

        projetoRepository.delete(projeto);
    }

    @Transactional
    @SneakyThrows
    public ProjetoDTO atualizarStatus(Long id, StatusProjeto novoStatus) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado com ID: " + id));

        validarTransicaoStatus(projeto.getStatus(), novoStatus);

        projeto.setStatus(novoStatus);

        if (novoStatus == StatusProjeto.ENCERRADO) {
            projeto.setDataRealTermino(LocalDate.now());
        }

        Projeto projetoAtualizado = projetoRepository.save(projeto);
        return projetoMapper.toDTO(projetoAtualizado);
    }

    @Transactional(readOnly = true)
    public RelatorioPortfolioDTO gerarRelatorioPortfolio() {
        Map<StatusProjeto, Long> quantidadePorStatus = projetoRepository.countProjetosByStatus().stream()
                .collect(Collectors.toMap(
                        map -> StatusProjeto.valueOf((String) map.get("status")),
                        map -> (Long) map.get("quantidade")
                ));

        Map<StatusProjeto, BigDecimal> totalOrcadoPorStatus = projetoRepository.sumOrcamentoByStatus().stream()
                .collect(Collectors.toMap(
                        map -> StatusProjeto.valueOf((String) map.get("status")),
                        map -> (BigDecimal) map.get("total")
                ));

        Double mediaDuracao = projetoRepository.calcularMediaDuracaoProjetosEncerrados();
        Long totalMembrosUnicos = projetoRepository.countMembrosUnicosAlocados();

        return RelatorioPortfolioDTO.builder()
                .quantidadeProjetosPorStatus(quantidadePorStatus)
                .totalOrcadoPorStatus(totalOrcadoPorStatus)
                .mediaDuracaoProjetosEncerrados(mediaDuracao)
                .totalMembrosUnicosAlocados(totalMembrosUnicos)
                .build();
    }
    @SneakyThrows
    private void validarTransicaoStatus(StatusProjeto statusAtual, StatusProjeto novoStatus) {
        if (novoStatus == null) {
            throw new ValidacaoException("Status do projeto é obrigatório");
        }

        if (statusAtual == null && novoStatus != StatusProjeto.EM_ANALISE) {
            throw new TransicaoStatusInvalidaException("Novos projetos devem iniciar com status 'Em Análise'");
        }

        if (statusAtual != null && novoStatus == StatusProjeto.CANCELADO) {
            return; // Cancelamento é sempre permitido
        }

        if (statusAtual != null && novoStatus.ordinal() != statusAtual.ordinal() + 1) {
            throw new TransicaoStatusInvalidaException(
                    String.format("Transição de status inválida: %s → %s. Deve seguir a sequência correta.",
                            statusAtual.getDescricao(), novoStatus.getDescricao()));
        }
    }

    private void validarEdicaoProjeto(Projeto projeto) throws OperacaoNaoPermitidaException {
        if (projeto.getStatus() == StatusProjeto.ENCERRADO ||
                projeto.getStatus() == StatusProjeto.CANCELADO) {
            throw new OperacaoNaoPermitidaException(
                    "Não é possível editar projetos com status " + projeto.getStatus().getDescricao());
        }
    }

    private void validarGerente(Membro membro) throws ValidacaoException {
        if (!"gerente".equalsIgnoreCase(membro.getAtribuicao())) {
            throw new ValidacaoException("O gerente do projeto deve ter a atribuição 'Gerente'");
        }
    }
    @SneakyThrows
    private void validarMembro(Membro membro) {
        if (!"funcionario".equalsIgnoreCase(membro.getAtribuicao())) {
            throw new RuntimeException("Apenas membros com atribuição 'Funcionário' podem ser associados a projetos");
        }

        Long projetosAtivos = projetoRepository.countProjetosAtivosPorMembro(membro.getId());
        if (projetosAtivos >= 3) {
            throw new RuntimeException(
                    String.format("Membro %s já está alocado em %d projetos ativos. Limite máximo é 3.",
                            membro.getNome(), projetosAtivos));
        }
    }
}
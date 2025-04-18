package com.example.GerenciadorPortfolios;

import com.example.GerenciadorPortfolios.dto.ProjetoDTO;
import com.example.GerenciadorPortfolios.exception.OperacaoNaoPermitidaException;
import com.example.GerenciadorPortfolios.exception.RecursoNaoEncontradoException;
import com.example.GerenciadorPortfolios.exception.TransicaoStatusInvalidaException;
import com.example.GerenciadorPortfolios.exception.ValidacaoException;
import com.example.GerenciadorPortfolios.mapper.ProjetoMapper;
import com.example.GerenciadorPortfolios.model.Membro;
import com.example.GerenciadorPortfolios.model.Projeto;
import com.example.GerenciadorPortfolios.model.enums.StatusProjeto;
import com.example.GerenciadorPortfolios.repository.ProjetoRepository;
import com.example.GerenciadorPortfolios.service.MembroService;
import com.example.GerenciadorPortfolios.service.ProjetoService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjetoServiceTest {


    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private MembroService membroService;

    @Mock
    private ProjetoMapper projetoMapper;

    @InjectMocks
    private ProjetoService projetoService;

    private Projeto projeto;
    private ProjetoDTO projetoDTO;
    private Membro gerente;
    private Membro membro;

    @BeforeEach
    void setUp() {
        gerente = new Membro();
        gerente.setId(1L);
        gerente.setNome("Gerente Projeto");
        gerente.setAtribuicao("gerente");

        membro = new Membro();
        membro.setId(2L);
        membro.setNome("Funcionário");
        membro.setAtribuicao("funcionario");

        projeto = new Projeto();
        projeto.setId(1L);
        projeto.setNome("Projeto Teste");
        projeto.setDataInicio(LocalDate.now());
        projeto.setPrevisaoTermino(LocalDate.now().plusMonths(2));
        projeto.setOrcamentoTotal(new BigDecimal("50000"));
        projeto.setDescricao("Descrição teste");
        projeto.setStatus(StatusProjeto.EM_ANALISE);
        projeto.setGerente(gerente);
        projeto.setMembros(new HashSet<>(Set.of(membro)));

        projetoDTO = new ProjetoDTO();
        projetoDTO.setNome("Projeto Teste");
        projetoDTO.setDataInicio(LocalDate.now());
        projetoDTO.setPrevisaoTermino(LocalDate.now().plusMonths(2));
        projetoDTO.setOrcamentoTotal(new BigDecimal("50000"));
        projetoDTO.setDescricao("Descrição teste");
        projetoDTO.setStatus(StatusProjeto.EM_ANALISE);
        projetoDTO.setGerenteId(1L);
        projetoDTO.setMembrosIds(Set.of(2L));
    }

    @Test
    void criarProjeto_DeveRetornarProjetoCriado() throws TransicaoStatusInvalidaException, ValidacaoException {
        when(projetoMapper.toEntity(projetoDTO)).thenReturn(projeto);
        when(membroService.buscarMembroPorId(1L)).thenReturn(gerente);
        when(membroService.buscarMembroPorId(2L)).thenReturn(membro);
        when(projetoRepository.save(projeto)).thenReturn(projeto);
        when(projetoMapper.toDTO(projeto)).thenReturn(projetoDTO);

        ProjetoDTO result = projetoService.criar(projetoDTO);

        assertNotNull(result);
        assertEquals(projetoDTO.getNome(), result.getNome());
        verify(projetoRepository, times(1)).save(projeto);
    }

    @Test
    void criarProjeto_ComAtribuicaoInvalida_DeveLancarExcecao() {
        projetoDTO.setStatus(StatusProjeto.EM_ANALISE);
        projetoDTO.setGerenteId(1L);
        gerente.setAtribuicao("invalido");

        Projeto projetoMock = new Projeto();
        when(projetoMapper.toEntity(projetoDTO)).thenReturn(projetoMock);
        when(membroService.buscarMembroPorId(1L)).thenReturn(gerente);

        assertThrows(ValidacaoException.class, () -> projetoService.criar(projetoDTO));
    }

    @Test
    void atualizarStatus_TransicaoValida_DeveAtualizarStatus() throws TransicaoStatusInvalidaException, ValidacaoException, RecursoNaoEncontradoException {
        projeto.setStatus(StatusProjeto.EM_ANALISE);
        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto));
        when(projetoRepository.save(projeto)).thenReturn(projeto);
        when(projetoMapper.toDTO(projeto)).thenReturn(projetoDTO);

        ProjetoDTO result = projetoService.atualizarStatus(1L, StatusProjeto.ANALISE_REALIZADA);

        assertNotNull(result);
        verify(projetoRepository, times(1)).save(projeto);
    }

    @Test
    void atualizarStatus_TransicaoInvalida_DeveLancarExcecao() {
        projeto.setStatus(StatusProjeto.EM_ANALISE);
        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto));

        assertThrows(TransicaoStatusInvalidaException.class,
                () -> projetoService.atualizarStatus(1L, StatusProjeto.INICIADO));
    }

    @Test
    void excluirProjeto_ComStatusIniciado_DeveLancarExcecao() {
        projeto.setStatus(StatusProjeto.INICIADO);
        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto));

        assertThrows(OperacaoNaoPermitidaException.class,
                () -> projetoService.excluir(1L));
    }

    @Test
    void gerarRelatorioPortfolio_DeveRetornarDadosCorretos() {
        when(projetoRepository.countProjetosByStatus()).thenReturn(List.of(
                Map.of("status", "EM_ANALISE", "quantidade", 5L),
                Map.of("status", "EM_ANDAMENTO", "quantidade", 3L)
        ));
        when(projetoRepository.sumOrcamentoByStatus()).thenReturn(List.of(
                Map.of("status", "EM_ANALISE", "total", new BigDecimal("100000")),
                Map.of("status", "EM_ANDAMENTO", "total", new BigDecimal("300000"))
        ));
        when(projetoRepository.calcularMediaDuracaoProjetosEncerrados()).thenReturn(60.5);
        when(projetoRepository.countMembrosUnicosAlocados()).thenReturn(15L);

        var relatorio = projetoService.gerarRelatorioPortfolio();

        assertNotNull(relatorio);
        assertEquals(2, relatorio.getQuantidadeProjetosPorStatus().size());
        assertEquals(60.5, relatorio.getMediaDuracaoProjetosEncerrados());
    }
}
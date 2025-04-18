package com.example.GerenciadorPortfolios.controller;

import com.example.GerenciadorPortfolios.dto.ProjetoDTO;
import com.example.GerenciadorPortfolios.dto.RelatorioPortfolioDTO;
import com.example.GerenciadorPortfolios.model.enums.StatusProjeto;
import com.example.GerenciadorPortfolios.service.ProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/projetos")
@RequiredArgsConstructor
@Tag(name = "Projetos", description = "API para gerenciamento de projetos")
public class ProjetoController {
    private final ProjetoService projetoService;

    @GetMapping
    @Operation(summary = "Listar projetos", description = "Retorna uma lista paginada de projetos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projetos listados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    public ResponseEntity<Page<ProjetoDTO>> listarTodos(
            @Parameter(description = "Nome do projeto para filtro") @RequestParam(required = false) String nome,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(projetoService.listarTodos(nome, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar projeto por ID", description = "Retorna um projeto específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projeto encontrado"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    public ResponseEntity<ProjetoDTO> buscarPorId(
            @Parameter(description = "ID do projeto") @PathVariable Long id) {
        return ResponseEntity.ok(projetoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar projeto", description = "Cria um novo projeto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    public ResponseEntity<ProjetoDTO> criar(
            @Parameter(description = "Dados do projeto") @Valid @RequestBody ProjetoDTO projetoDTO) {
        ProjetoDTO projetoCriado = projetoService.criar(projetoDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(projetoCriado.getId())
                .toUri();
        return ResponseEntity.created(location).body(projetoCriado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar projeto", description = "Atualiza um projeto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projeto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    public ResponseEntity<ProjetoDTO> atualizar(
            @Parameter(description = "ID do projeto") @PathVariable Long id,
            @Parameter(description = "Dados atualizados do projeto") @Valid @RequestBody ProjetoDTO projetoDTO) {
        return ResponseEntity.ok(projetoService.atualizar(id, projetoDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir projeto", description = "Exclui um projeto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Projeto excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Operação não permitida"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID do projeto") @PathVariable Long id) {
        projetoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar status do projeto", description = "Atualiza o status de um projeto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Transição de status inválida"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    public ResponseEntity<ProjetoDTO> atualizarStatus(
            @Parameter(description = "ID do projeto") @PathVariable Long id,
            @Parameter(description = "Novo status do projeto") @RequestParam StatusProjeto status) {
        return ResponseEntity.ok(projetoService.atualizarStatus(id, status));
    }

    @GetMapping("/relatorio")
    @Operation(summary = "Gerar relatório do portfólio", description = "Gera um relatório resumido do portfólio de projetos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    public ResponseEntity<RelatorioPortfolioDTO> gerarRelatorio() {
        return ResponseEntity.ok(projetoService.gerarRelatorioPortfolio());
    }
}
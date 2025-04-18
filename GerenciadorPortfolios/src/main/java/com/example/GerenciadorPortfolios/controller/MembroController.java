package com.example.GerenciadorPortfolios.controller;

import com.example.GerenciadorPortfolios.dto.MembroDTO;
import com.example.GerenciadorPortfolios.model.Membro;
import com.example.GerenciadorPortfolios.service.MembroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/membros")
@RequiredArgsConstructor
@Tag(name = "Membros", description = "API para gerenciamento de membros da equipe")
public class MembroController {
    private final MembroService membroService;

    private MembroDTO convertToDTO(Membro membro) {
        if(membro == null){ return  null;}

        MembroDTO membroDTO = new MembroDTO();
        membroDTO.setId(membro.getId());
        membroDTO.setNome(membro.getNome());
        membroDTO.setAtribuicao(membro.getAtribuicao());
        membroDTO.setIdentificadorExterno(membro.getIdentificadorExterno());

        return membroDTO;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar membro", description = "Cria um novo membro na API externa e no sistema local")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Membro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    public ResponseEntity<MembroDTO> criar(@Valid @RequestBody MembroDTO membroDTO) {
        Membro membro = membroService.criarMembro(membroDTO);
        MembroDTO membroCriado = convertToDTO(membro);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(membroCriado.getId())
                .toUri();
        return ResponseEntity.created(location).body(membroCriado);
    }

    @GetMapping("/externo/{identificador}")
    @Operation(summary = "Consultar membro externo", description = "Consulta um membro na API externa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membro encontrado"),
            @ApiResponse(responseCode = "404", description = "Membro não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<MembroDTO> consultarExterno(
            @Parameter(description = "Identificador externo do membro")
            @PathVariable String identificador) {
        return ResponseEntity.ok(membroService.consultarMembroExterno(identificador));
    }
}
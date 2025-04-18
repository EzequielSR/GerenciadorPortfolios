package com.example.GerenciadorPortfolios.service;

import com.example.GerenciadorPortfolios.dto.MembroDTO;
import com.example.GerenciadorPortfolios.model.Membro;
import com.example.GerenciadorPortfolios.repository.MembroRepository;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MembroService {
    private final MembroRepository membroRepository;
    private final RestTemplate restTemplate;

    // URL da API externa mockada (em um cenário real, seria configurável)
    private static final String API_MEMBROS_URL = "https://api.mocki.io/v2/123456/membros";

    public Membro buscarMembroPorId(Long id) {
        return membroRepository.findById(id)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Membro não encontrado com ID: " + id));
    }

    public Membro criarMembro(MembroDTO membroDTO) {
        // Primeiro cria na API externa
        MembroDTO membroCriadoExterno = criarMembroExterno(membroDTO);

        // Depois salva localmente
        Membro membro = Membro.builder()
                .nome(membroCriadoExterno.getNome())
                .atribuicao(membroCriadoExterno.getAtribuicao())
                .identificadorExterno(membroCriadoExterno.getIdentificadorExterno())
                .build();

        return membroRepository.save(membro);
    }

    private MembroDTO criarMembroExterno(MembroDTO membroDTO) {
        // Simula a chamada para API externa
        // Em produção, seria: restTemplate.postForObject(API_MEMBROS_URL, membroDTO, MembroDTO.class);

        // Mock da resposta
        return MembroDTO.builder()
                .nome(membroDTO.getNome())
                .atribuicao(membroDTO.getAtribuicao())
                .identificadorExterno(UUID.randomUUID().toString())
                .build();
    }

    public MembroDTO consultarMembroExterno(String identificadorExterno) {
        // Simula a consulta na API externa
        // Em produção, seria: restTemplate.getForObject(API_MEMBROS_URL + "/" + identificadorExterno, MembroDTO.class);

        // Mock da resposta
        return MembroDTO.builder()
                .nome("Membro Mock")
                .atribuicao("funcionario")
                .identificadorExterno(identificadorExterno)
                .build();
    }
}
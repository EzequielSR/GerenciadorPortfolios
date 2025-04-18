package com.example.GerenciadorPortfolios.mapper;


import com.example.GerenciadorPortfolios.dto.ProjetoDTO;
import com.example.GerenciadorPortfolios.model.Membro;
import com.example.GerenciadorPortfolios.model.Projeto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjetoMapper {

    ProjetoMapper INSTANCE = Mappers.getMapper(ProjetoMapper.class);

    @Mapping(target = "gerente", ignore = true)
    @Mapping(target = "membros", ignore = true)
    @Mapping(target = "id", ignore = true)
    Projeto toEntity(ProjetoDTO projetoDTO);

    @Mapping(target = "gerenteId", source = "gerente.id")
    @Mapping(target = "membrosIds", source = "membros", qualifiedByName = "mapMembrosToIds")
    ProjetoDTO toDTO(Projeto projeto);

    @Named("mapMembrosToIds")
    default Set<Long> mapMembrosToIds(Set<Membro> membros) {
        if (membros == null) {
            return Set.of();
        }
        return membros.stream()
                .map(Membro::getId)
                .collect(Collectors.toSet());
    }

    default void toEntity(ProjetoDTO projetoDTO, Projeto projeto) {
        if (projetoDTO == null) {
            return;
        }

        projeto.setNome(projetoDTO.getNome());
        projeto.setDataInicio(projetoDTO.getDataInicio());
        projeto.setPrevisaoTermino(projetoDTO.getPrevisaoTermino());
        projeto.setDataRealTermino(projetoDTO.getDataRealTermino());
        projeto.setOrcamentoTotal(projetoDTO.getOrcamentoTotal());
        projeto.setDescricao(projetoDTO.getDescricao());
        projeto.setStatus(projetoDTO.getStatus());
    }
}

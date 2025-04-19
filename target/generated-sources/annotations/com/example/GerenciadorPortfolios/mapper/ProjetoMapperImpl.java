package com.example.GerenciadorPortfolios.mapper;

import com.example.GerenciadorPortfolios.dto.ProjetoDTO;
import com.example.GerenciadorPortfolios.model.Membro;
import com.example.GerenciadorPortfolios.model.Projeto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-18T20:05:34-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ProjetoMapperImpl implements ProjetoMapper {

    @Override
    public Projeto toEntity(ProjetoDTO projetoDTO) {
        if ( projetoDTO == null ) {
            return null;
        }

        Projeto.ProjetoBuilder projeto = Projeto.builder();

        projeto.nome( projetoDTO.getNome() );
        projeto.dataInicio( projetoDTO.getDataInicio() );
        projeto.previsaoTermino( projetoDTO.getPrevisaoTermino() );
        projeto.dataRealTermino( projetoDTO.getDataRealTermino() );
        projeto.orcamentoTotal( projetoDTO.getOrcamentoTotal() );
        projeto.descricao( projetoDTO.getDescricao() );
        projeto.status( projetoDTO.getStatus() );
        projeto.classificacaoRisco( projetoDTO.getClassificacaoRisco() );

        return projeto.build();
    }

    @Override
    public ProjetoDTO toDTO(Projeto projeto) {
        if ( projeto == null ) {
            return null;
        }

        ProjetoDTO.ProjetoDTOBuilder projetoDTO = ProjetoDTO.builder();

        projetoDTO.gerenteId( projetoGerenteId( projeto ) );
        projetoDTO.membrosIds( mapMembrosToIds( projeto.getMembros() ) );
        projetoDTO.id( projeto.getId() );
        projetoDTO.nome( projeto.getNome() );
        projetoDTO.dataInicio( projeto.getDataInicio() );
        projetoDTO.previsaoTermino( projeto.getPrevisaoTermino() );
        projetoDTO.dataRealTermino( projeto.getDataRealTermino() );
        projetoDTO.orcamentoTotal( projeto.getOrcamentoTotal() );
        projetoDTO.descricao( projeto.getDescricao() );
        projetoDTO.status( projeto.getStatus() );
        projetoDTO.classificacaoRisco( projeto.getClassificacaoRisco() );

        return projetoDTO.build();
    }

    private Long projetoGerenteId(Projeto projeto) {
        if ( projeto == null ) {
            return null;
        }
        Membro gerente = projeto.getGerente();
        if ( gerente == null ) {
            return null;
        }
        Long id = gerente.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}

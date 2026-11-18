package br.com.fiap.ms.atendimentos.dto;

import br.com.fiap.ms.atendimentos.entities.Atendimento;
import br.com.fiap.ms.atendimentos.entities.StatusAtendimento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AtendimentoResponseDTO {

    private Long id;
    private Long ocorrenciaId;
    private String enderecoOcorrencia;
    private Double indicePrioridade;
    private String equipeResponsavel;
    private String descricaoServico;
    private LocalDate dataAbertura;
    private LocalDate dataPrevista;
    private LocalDate dataConclusao;
    private StatusAtendimento status;

    public AtendimentoResponseDTO(Atendimento atendimento) {
        id = atendimento.getId();
        ocorrenciaId = atendimento.getOcorrenciaId();
        enderecoOcorrencia = atendimento.getEnderecoOcorrencia();
        indicePrioridade = atendimento.getIndicePrioridade();
        equipeResponsavel = atendimento.getEquipeResponsavel();
        descricaoServico = atendimento.getDescricaoServico();
        dataAbertura = atendimento.getDataAbertura();
        dataPrevista = atendimento.getDataPrevista();
        dataConclusao = atendimento.getDataConclusao();
        status = atendimento.getStatus();
    }
}

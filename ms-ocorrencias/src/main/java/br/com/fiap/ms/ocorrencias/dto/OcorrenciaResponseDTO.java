package br.com.fiap.ms.ocorrencias.dto;

import br.com.fiap.ms.ocorrencias.entities.Ocorrencia;
import br.com.fiap.ms.ocorrencias.entities.StatusOcorrencia;
import br.com.fiap.ms.ocorrencias.entities.TipoBarreira;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OcorrenciaResponseDTO {

    private Long id;
    private TipoBarreira tipoBarreira;
    private String descricao;
    private String logradouro;
    private String bairro;
    private String cep;
    private Integer gravidade;
    private Integer fatorEntorno;
    private Integer confirmacoes;
    private Double indicePrioridade;
    private StatusOcorrencia status;
    private LocalDate dataRegistro;

    public OcorrenciaResponseDTO(Ocorrencia ocorrencia) {
        id = ocorrencia.getId();
        tipoBarreira = ocorrencia.getTipoBarreira();
        descricao = ocorrencia.getDescricao();
        logradouro = ocorrencia.getLogradouro();
        bairro = ocorrencia.getBairro();
        cep = ocorrencia.getCep();
        gravidade = ocorrencia.getGravidade();
        fatorEntorno = ocorrencia.getFatorEntorno();
        confirmacoes = ocorrencia.getConfirmacoes();
        indicePrioridade = ocorrencia.getIndicePrioridade();
        status = ocorrencia.getStatus();
        dataRegistro = ocorrencia.getDataRegistro();
    }
}

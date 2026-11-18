package br.com.fiap.ms.atendimentos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dados da ocorrência recebidos do ms-ocorrencias pelo OpenFeign.
 * Contém apenas os campos usados pelo ms-atendimentos.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OcorrenciaDTO {

    private Long id;
    private String tipoBarreira;
    private String logradouro;
    private String bairro;
    private Double indicePrioridade;
    private String status;
}

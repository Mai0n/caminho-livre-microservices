package br.com.fiap.ms.atendimentos.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Ordem de serviço da equipe da prefeitura para resolver uma ocorrência.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_atendimento")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID da ocorrência no ms-ocorrencias (cada microsserviço tem seu próprio banco)
    @Column(name = "ocorrencia_id")
    private Long ocorrenciaId;

    // Copiados do ms-ocorrencias no momento da abertura do atendimento
    @Column(name = "endereco_ocorrencia")
    private String enderecoOcorrencia;

    @Column(name = "indice_prioridade")
    private Double indicePrioridade;

    @Column(name = "equipe_responsavel")
    private String equipeResponsavel;

    @Column(name = "descricao_servico")
    private String descricaoServico;

    @Column(name = "data_abertura")
    private LocalDate dataAbertura;

    @Column(name = "data_prevista")
    private LocalDate dataPrevista;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @Enumerated(EnumType.STRING)
    private StatusAtendimento status;
}

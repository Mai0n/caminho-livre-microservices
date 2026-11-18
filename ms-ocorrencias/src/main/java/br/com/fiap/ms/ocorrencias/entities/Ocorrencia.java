package br.com.fiap.ms.ocorrencias.entities;

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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_ocorrencia")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_barreira")
    private TipoBarreira tipoBarreira;

    private String descricao;
    private String logradouro;
    private String bairro;
    private String cep;

    private Integer gravidade;

    @Column(name = "fator_entorno")
    private Integer fatorEntorno;

    private Integer confirmacoes;

    @Column(name = "indice_prioridade")
    private Double indicePrioridade;

    @Enumerated(EnumType.STRING)
    private StatusOcorrencia status;

    @Column(name = "data_registro")
    private LocalDate dataRegistro;
}

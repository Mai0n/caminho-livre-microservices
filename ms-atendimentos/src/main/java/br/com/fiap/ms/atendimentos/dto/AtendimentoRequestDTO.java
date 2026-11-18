package br.com.fiap.ms.atendimentos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Dados de entrada para abrir ou atualizar um atendimento.
 * O status e as datas de abertura e conclusão são definidos pelo sistema.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AtendimentoRequestDTO {

    @NotNull(message = "Campo ocorrência é obrigatório")
    @Positive(message = "O ID da ocorrência deve ser maior que zero")
    @Schema(example = "1")
    private Long ocorrenciaId;

    @NotBlank(message = "Campo equipe responsável é obrigatório")
    @Size(min = 3, max = 100, message = "O campo equipe responsável deve ter entre 3 e 100 caracteres")
    @Schema(example = "Equipe de Obras Viárias - Centro")
    private String equipeResponsavel;

    @NotBlank(message = "Campo descrição do serviço é obrigatório")
    @Size(min = 10, max = 255, message = "O campo descrição do serviço deve ter entre 10 e 255 caracteres")
    @Schema(example = "Construção de rampa de acessibilidade na esquina")
    private String descricaoServico;

    @NotNull(message = "Campo data prevista é obrigatório")
    @FutureOrPresent(message = "A data prevista não pode estar no passado")
    @Schema(example = "2026-12-01")
    private LocalDate dataPrevista;
}

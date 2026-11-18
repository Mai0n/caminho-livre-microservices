package br.com.fiap.ms.ocorrencias.dto;

import br.com.fiap.ms.ocorrencias.entities.TipoBarreira;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dados de entrada para registrar ou atualizar uma ocorrência.
 * O status, as confirmações e o índice de prioridade são definidos pelo sistema.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OcorrenciaRequestDTO {

    @NotNull(message = "Campo tipo de barreira é obrigatório")
    @Schema(example = "RAMPA_AUSENTE")
    private TipoBarreira tipoBarreira;

    @NotBlank(message = "Campo descrição é obrigatório")
    @Size(min = 10, max = 255, message = "O campo descrição deve ter entre 10 e 255 caracteres")
    @Schema(example = "Não há rampa na esquina. Cadeirantes precisam descer pela rua.")
    private String descricao;

    @NotBlank(message = "Campo logradouro é obrigatório")
    @Size(min = 3, max = 150, message = "O campo logradouro deve ter entre 3 e 150 caracteres")
    @Schema(example = "Avenida do Hospital")
    private String logradouro;

    @NotBlank(message = "Campo bairro é obrigatório")
    @Size(min = 2, max = 100, message = "O campo bairro deve ter entre 2 e 100 caracteres")
    @Schema(example = "Centro")
    private String bairro;

    @NotBlank(message = "Campo CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "O CEP deve estar no formato 00000-000")
    @Schema(example = "01001-000")
    private String cep;

    @NotNull(message = "Campo gravidade é obrigatório")
    @Min(value = 1, message = "A gravidade deve ser no mínimo 1")
    @Max(value = 5, message = "A gravidade deve ser no máximo 5")
    @Schema(example = "4")
    private Integer gravidade;

    @NotNull(message = "Campo fator de entorno é obrigatório")
    @Min(value = 1, message = "O fator de entorno deve ser no mínimo 1")
    @Max(value = 3, message = "O fator de entorno deve ser no máximo 3")
    @Schema(example = "3")
    private Integer fatorEntorno;
}

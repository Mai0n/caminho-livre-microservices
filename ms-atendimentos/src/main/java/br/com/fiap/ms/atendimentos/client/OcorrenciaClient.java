package br.com.fiap.ms.atendimentos.client;

import br.com.fiap.ms.atendimentos.dto.OcorrenciaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Comunicação síncrona com o ms-ocorrencias.
 * Sem URL fixa: o endereço é obtido no Eureka pelo nome do serviço e o
 * Load Balancer escolhe uma das instâncias disponíveis.
 */
@FeignClient("ms-ocorrencias")
public interface OcorrenciaClient {

    // ABERTA -> EM_ATENDIMENTO
    @RequestMapping(method = RequestMethod.PUT,
            value = "/api/v1/ocorrencias/{id}/em-atendimento") //endpoint da requisição
    OcorrenciaDTO iniciarAtendimento(@PathVariable("id") Long id);

    // EM_ATENDIMENTO -> RESOLVIDA
    @RequestMapping(method = RequestMethod.PUT,
            value = "/api/v1/ocorrencias/{id}/resolvida") //endpoint da requisição
    OcorrenciaDTO resolverOcorrencia(@PathVariable("id") Long id);

    // EM_ATENDIMENTO -> ABERTA
    @RequestMapping(method = RequestMethod.PUT,
            value = "/api/v1/ocorrencias/{id}/reaberta") //endpoint da requisição
    OcorrenciaDTO reabrirOcorrencia(@PathVariable("id") Long id);
}

package br.com.fiap.ms.atendimentos.client;

import br.com.fiap.ms.atendimentos.dto.OcorrenciaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("ms-ocorrencias")
public interface OcorrenciaClient {

    @RequestMapping(method = RequestMethod.PUT,
            value = "/api/v1/ocorrencias/{id}/em-atendimento")
    OcorrenciaDTO iniciarAtendimento(@PathVariable("id") Long id);

    @RequestMapping(method = RequestMethod.PUT,
            value = "/api/v1/ocorrencias/{id}/resolvida")
    OcorrenciaDTO resolverOcorrencia(@PathVariable("id") Long id);

    @RequestMapping(method = RequestMethod.PUT,
            value = "/api/v1/ocorrencias/{id}/reaberta")
    OcorrenciaDTO reabrirOcorrencia(@PathVariable("id") Long id);
}

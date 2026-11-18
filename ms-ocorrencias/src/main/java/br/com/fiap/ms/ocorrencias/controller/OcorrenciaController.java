package br.com.fiap.ms.ocorrencias.controller;

import br.com.fiap.ms.ocorrencias.dto.OcorrenciaRequestDTO;
import br.com.fiap.ms.ocorrencias.dto.OcorrenciaResponseDTO;
import br.com.fiap.ms.ocorrencias.service.OcorrenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ocorrencias")
public class OcorrenciaController {

    @Autowired
    private OcorrenciaService ocorrenciaService;

    @GetMapping("/port")
    public ResponseEntity<String> port(@Value("${local.server.port}") String porta) {
        return ResponseEntity.ok("Instância respondeu na porta " + porta);
    }

    @GetMapping
    public ResponseEntity<List<OcorrenciaResponseDTO>> getAllOcorrencias() {

        List<OcorrenciaResponseDTO> list = ocorrenciaService.findAllOcorrencias();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OcorrenciaResponseDTO> getOcorrenciaById(@PathVariable Long id) {

        OcorrenciaResponseDTO ocorrenciaDTO = ocorrenciaService.findOcorrenciaById(id);

        return ResponseEntity.ok(ocorrenciaDTO);
    }

    @PostMapping
    public ResponseEntity<OcorrenciaResponseDTO> createOcorrencia(@RequestBody @Valid
                                                                  OcorrenciaRequestDTO requestDTO) {

        OcorrenciaResponseDTO ocorrenciaDTO = ocorrenciaService.saveOcorrencia(requestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(ocorrenciaDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(ocorrenciaDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OcorrenciaResponseDTO> updateOcorrencia(@PathVariable Long id,
                                                                  @RequestBody @Valid
                                                                  OcorrenciaRequestDTO requestDTO) {

        OcorrenciaResponseDTO ocorrenciaDTO = ocorrenciaService.updateOcorrencia(id, requestDTO);

        return ResponseEntity.ok(ocorrenciaDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOcorrencia(@PathVariable Long id) {

        ocorrenciaService.deleteOcorrenciaById(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<OcorrenciaResponseDTO> confirmarOcorrencia(@PathVariable Long id) {

        OcorrenciaResponseDTO ocorrenciaDTO = ocorrenciaService.confirmarOcorrencia(id);

        return ResponseEntity.ok(ocorrenciaDTO);
    }

    @PutMapping("/{id}/em-atendimento")
    public ResponseEntity<OcorrenciaResponseDTO> iniciarAtendimento(@PathVariable Long id) {

        OcorrenciaResponseDTO ocorrenciaDTO = ocorrenciaService.iniciarAtendimento(id);

        return ResponseEntity.ok(ocorrenciaDTO);
    }

    @PutMapping("/{id}/resolvida")
    public ResponseEntity<OcorrenciaResponseDTO> resolverOcorrencia(@PathVariable Long id) {

        OcorrenciaResponseDTO ocorrenciaDTO = ocorrenciaService.resolverOcorrencia(id);

        return ResponseEntity.ok(ocorrenciaDTO);
    }

    @PutMapping("/{id}/reaberta")
    public ResponseEntity<OcorrenciaResponseDTO> reabrirOcorrencia(@PathVariable Long id) {

        OcorrenciaResponseDTO ocorrenciaDTO = ocorrenciaService.reabrirOcorrencia(id);

        return ResponseEntity.ok(ocorrenciaDTO);
    }
}

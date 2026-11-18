package br.com.fiap.ms.atendimentos.controller;

import br.com.fiap.ms.atendimentos.dto.AtendimentoRequestDTO;
import br.com.fiap.ms.atendimentos.dto.AtendimentoResponseDTO;
import br.com.fiap.ms.atendimentos.exceptions.RegraNegocioException;
import br.com.fiap.ms.atendimentos.exceptions.ResourceNotFoundException;
import br.com.fiap.ms.atendimentos.service.AtendimentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/atendimentos")
public class AtendimentoController {

    @Autowired
    private AtendimentoService atendimentoService;

    @GetMapping("/port")
    public ResponseEntity<String> port(@Value("${local.server.port}") String porta) {
        return ResponseEntity.ok("Instância respondeu na porta " + porta);
    }

    @GetMapping
    public ResponseEntity<List<AtendimentoResponseDTO>> getAllAtendimentos() {

        List<AtendimentoResponseDTO> list = atendimentoService.findAllAtendimentos();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoResponseDTO> getAtendimentoById(@PathVariable Long id) {

        AtendimentoResponseDTO atendimentoDTO = atendimentoService.findAtendimentoById(id);

        return ResponseEntity.ok(atendimentoDTO);
    }

    @PostMapping
    public ResponseEntity<AtendimentoResponseDTO> createAtendimento(@RequestBody @Valid
                                                                    AtendimentoRequestDTO requestDTO) {

        AtendimentoResponseDTO atendimentoDTO = atendimentoService.saveAtendimento(requestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(atendimentoDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(atendimentoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtendimentoResponseDTO> updateAtendimento(@PathVariable Long id,
                                                                    @RequestBody @Valid
                                                                    AtendimentoRequestDTO requestDTO) {

        AtendimentoResponseDTO atendimentoDTO = atendimentoService.updateAtendimento(id, requestDTO);

        return ResponseEntity.ok(atendimentoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAtendimento(@PathVariable Long id) {

        atendimentoService.deleteAtendimentoById(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/concluir")
    @CircuitBreaker(name = "atualizarOcorrencia",
            fallbackMethod = "fallbackConcluirAtendimentoPendente")
    public ResponseEntity<AtendimentoResponseDTO> concluirAtendimento(@PathVariable Long id) {

        AtendimentoResponseDTO atendimentoDTO = atendimentoService.concluirAtendimento(id);

        return ResponseEntity.ok(atendimentoDTO);
    }

    public ResponseEntity<AtendimentoResponseDTO> fallbackConcluirAtendimentoPendente(Long id, Throwable e) {

        if (e instanceof ResourceNotFoundException resourceNotFoundException) {
            throw resourceNotFoundException;
        }
        if (e instanceof RegraNegocioException regraNegocioException) {
            throw regraNegocioException;
        }

        log.error("Falha ao concluir atendimento {}. Ativando fallback. Erro: {}", id, e.getMessage());
        AtendimentoResponseDTO atendimentoDTO = atendimentoService.alterarStatusParaConclusaoPendente(id);

        return ResponseEntity.status(503).body(atendimentoDTO);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AtendimentoResponseDTO> cancelarAtendimento(@PathVariable Long id) {

        AtendimentoResponseDTO atendimentoDTO = atendimentoService.cancelarAtendimento(id);

        return ResponseEntity.ok(atendimentoDTO);
    }
}

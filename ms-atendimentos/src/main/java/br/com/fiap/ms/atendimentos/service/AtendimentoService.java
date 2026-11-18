package br.com.fiap.ms.atendimentos.service;

import br.com.fiap.ms.atendimentos.client.OcorrenciaClient;
import br.com.fiap.ms.atendimentos.dto.AtendimentoRequestDTO;
import br.com.fiap.ms.atendimentos.dto.AtendimentoResponseDTO;
import br.com.fiap.ms.atendimentos.dto.OcorrenciaDTO;
import br.com.fiap.ms.atendimentos.entities.Atendimento;
import br.com.fiap.ms.atendimentos.entities.StatusAtendimento;
import br.com.fiap.ms.atendimentos.exceptions.RegraNegocioException;
import br.com.fiap.ms.atendimentos.exceptions.ResourceNotFoundException;
import br.com.fiap.ms.atendimentos.exceptions.ServicoIndisponivelException;
import br.com.fiap.ms.atendimentos.repositories.AtendimentoRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private OcorrenciaClient ocorrenciaClient;

    @Transactional(readOnly = true)
    public List<AtendimentoResponseDTO> findAllAtendimentos() {

        List<Atendimento> atendimentos = atendimentoRepository.findAll();

        return atendimentos.stream().map(AtendimentoResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public AtendimentoResponseDTO findAtendimentoById(Long id) {

        Atendimento atendimento = buscarAtendimento(id);

        return new AtendimentoResponseDTO(atendimento);
    }

    /**
     * Abre um atendimento para a ocorrência.
     * Comunicação síncrona: o ms-ocorrencias altera a ocorrência de ABERTA para EM_ATENDIMENTO.
     */
    @Transactional
    public AtendimentoResponseDTO saveAtendimento(AtendimentoRequestDTO requestDTO) {

        OcorrenciaDTO ocorrenciaDTO;
        try {
            ocorrenciaDTO = ocorrenciaClient.iniciarAtendimento(requestDTO.getOcorrenciaId());
        } catch (FeignException e) {
            throw tratarFalhaMsOcorrencias(e, requestDTO.getOcorrenciaId());
        }

        Atendimento atendimento = new Atendimento();
        copyDtoToAtendimento(requestDTO, atendimento);
        atendimento.setOcorrenciaId(ocorrenciaDTO.getId());
        atendimento.setEnderecoOcorrencia(ocorrenciaDTO.getLogradouro() + " - " + ocorrenciaDTO.getBairro());
        atendimento.setIndicePrioridade(ocorrenciaDTO.getIndicePrioridade());

        // Valores definidos pelo sistema na abertura
        atendimento.setStatus(StatusAtendimento.AGENDADO);
        atendimento.setDataAbertura(LocalDate.now());

        atendimento = atendimentoRepository.save(atendimento);
        return new AtendimentoResponseDTO(atendimento);
    }

    @Transactional
    public AtendimentoResponseDTO updateAtendimento(Long id, AtendimentoRequestDTO requestDTO) {

        try {
            Atendimento atendimento = atendimentoRepository.getReferenceById(id);

            if (atendimento.getStatus() != StatusAtendimento.AGENDADO) {
                throw new RegraNegocioException(String.format(
                        "Atendimento id: %d está %s e não pode ser alterado", id, atendimento.getStatus()));
            }

            if (!atendimento.getOcorrenciaId().equals(requestDTO.getOcorrenciaId())) {
                throw new RegraNegocioException("Não é permitido alterar a ocorrência de um atendimento. "
                        + "Cancele este atendimento e abra outro para a nova ocorrência.");
            }

            copyDtoToAtendimento(requestDTO, atendimento);
            atendimento = atendimentoRepository.save(atendimento);
            return new AtendimentoResponseDTO(atendimento);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }
    }

    @Transactional
    public void deleteAtendimentoById(Long id) {

        Atendimento atendimento = buscarAtendimento(id);

        if (atendimento.getStatus() == StatusAtendimento.AGENDADO
                || atendimento.getStatus() == StatusAtendimento.CONCLUSAO_PENDENTE) {
            throw new RegraNegocioException(String.format(
                    "Atendimento id: %d está %s e não pode ser excluído. Conclua ou cancele o atendimento antes.",
                    id, atendimento.getStatus()));
        }

        atendimentoRepository.deleteById(id);
    }

    /**
     * Conclui o atendimento.
     * Comunicação síncrona: o ms-ocorrencias altera a ocorrência de EM_ATENDIMENTO para RESOLVIDA.
     * Protegido pelo Circuit Breaker no AtendimentoController.
     */
    @Transactional
    public AtendimentoResponseDTO concluirAtendimento(Long id) {

        Atendimento atendimento = buscarAtendimentoEmAndamento(id);

        try {
            ocorrenciaClient.resolverOcorrencia(atendimento.getOcorrenciaId());
        } catch (FeignException e) {
            throw tratarFalhaMsOcorrencias(e, atendimento.getOcorrenciaId());
        }

        atendimento.setStatus(StatusAtendimento.CONCLUIDO);
        atendimento.setDataConclusao(LocalDate.now());
        atendimento = atendimentoRepository.save(atendimento);
        return new AtendimentoResponseDTO(atendimento);
    }

    /**
     * Estratégia do fallback quando o ms-ocorrencias está indisponível:
     * o atendimento fica com CONCLUSAO_PENDENTE e pode ser concluído de novo depois.
     */
    @Transactional
    public AtendimentoResponseDTO alterarStatusParaConclusaoPendente(Long id) {

        Atendimento atendimento = buscarAtendimento(id);

        atendimento.setStatus(StatusAtendimento.CONCLUSAO_PENDENTE);
        atendimento = atendimentoRepository.save(atendimento);
        return new AtendimentoResponseDTO(atendimento);
    }

    /**
     * Cancela o atendimento.
     * Comunicação síncrona: o ms-ocorrencias altera a ocorrência de EM_ATENDIMENTO para ABERTA.
     */
    @Transactional
    public AtendimentoResponseDTO cancelarAtendimento(Long id) {

        Atendimento atendimento = buscarAtendimentoEmAndamento(id);

        try {
            ocorrenciaClient.reabrirOcorrencia(atendimento.getOcorrenciaId());
        } catch (FeignException e) {
            throw tratarFalhaMsOcorrencias(e, atendimento.getOcorrenciaId());
        }

        atendimento.setStatus(StatusAtendimento.CANCELADO);
        atendimento = atendimentoRepository.save(atendimento);
        return new AtendimentoResponseDTO(atendimento);
    }

    private Atendimento buscarAtendimento(Long id) {

        return atendimentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );
    }

    // Atendimento ainda não finalizado: AGENDADO ou CONCLUSAO_PENDENTE
    private Atendimento buscarAtendimentoEmAndamento(Long id) {

        Atendimento atendimento = buscarAtendimento(id);

        if (atendimento.getStatus() != StatusAtendimento.AGENDADO
                && atendimento.getStatus() != StatusAtendimento.CONCLUSAO_PENDENTE) {
            throw new RegraNegocioException(String.format(
                    "Atendimento id: %d já está %s e não pode ser alterado", id, atendimento.getStatus()));
        }

        return atendimento;
    }

    // Converte as respostas de erro do ms-ocorrencias em exceções da aplicação
    private RuntimeException tratarFalhaMsOcorrencias(FeignException e, Long ocorrenciaId) {

        if (e instanceof FeignException.NotFound) {
            // 404 do ms-ocorrencias: não existe ocorrência com esse ID
            return new ResourceNotFoundException("Ocorrência não encontrada. ID: " + ocorrenciaId);
        }

        if (e instanceof FeignException.Conflict) {
            // 409 do ms-ocorrencias: o status atual da ocorrência não permite a operação
            return new RegraNegocioException(String.format(
                    "O status atual da ocorrência id: %d não permite esta operação", ocorrenciaId));
        }

        // outros erros (500/503/timeout/sem instâncias etc.)
        return new ServicoIndisponivelException("Falha ao comunicar com ms-ocorrencias. "
                + "Tente novamente mais tarde.");
    }

    private void copyDtoToAtendimento(AtendimentoRequestDTO requestDTO, Atendimento atendimento) {

        atendimento.setEquipeResponsavel(requestDTO.getEquipeResponsavel());
        atendimento.setDescricaoServico(requestDTO.getDescricaoServico());
        atendimento.setDataPrevista(requestDTO.getDataPrevista());
    }
}

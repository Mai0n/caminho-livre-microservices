package br.com.fiap.ms.ocorrencias.service;

import br.com.fiap.ms.ocorrencias.dto.OcorrenciaRequestDTO;
import br.com.fiap.ms.ocorrencias.dto.OcorrenciaResponseDTO;
import br.com.fiap.ms.ocorrencias.entities.Ocorrencia;
import br.com.fiap.ms.ocorrencias.entities.StatusOcorrencia;
import br.com.fiap.ms.ocorrencias.exceptions.RegraNegocioException;
import br.com.fiap.ms.ocorrencias.exceptions.ResourceNotFoundException;
import br.com.fiap.ms.ocorrencias.repositories.OcorrenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class OcorrenciaService {

    // Regra do índice de prioridade do Caminho Livre
    private static final double BONUS_POR_CONFIRMACAO = 0.5;
    private static final double BONUS_MAXIMO = 3.0;

    @Autowired
    private OcorrenciaRepository ocorrenciaRepository;

    @Transactional(readOnly = true)
    public List<OcorrenciaResponseDTO> findAllOcorrencias() {

        List<Ocorrencia> ocorrencias = ocorrenciaRepository.findAllByOrderByIndicePrioridadeDesc();

        return ocorrencias.stream().map(OcorrenciaResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public OcorrenciaResponseDTO findOcorrenciaById(Long id) {

        Ocorrencia ocorrencia = buscarOcorrencia(id);

        return new OcorrenciaResponseDTO(ocorrencia);
    }

    @Transactional
    public OcorrenciaResponseDTO saveOcorrencia(OcorrenciaRequestDTO requestDTO) {

        Ocorrencia ocorrencia = new Ocorrencia();
        copyDtoToOcorrencia(requestDTO, ocorrencia);

        // Valores definidos pelo sistema no cadastro
        ocorrencia.setStatus(StatusOcorrencia.ABERTA);
        ocorrencia.setConfirmacoes(0);
        ocorrencia.setDataRegistro(LocalDate.now());
        ocorrencia.setIndicePrioridade(calcularIndicePrioridade(ocorrencia));

        ocorrencia = ocorrenciaRepository.save(ocorrencia);
        return new OcorrenciaResponseDTO(ocorrencia);
    }

    @Transactional
    public OcorrenciaResponseDTO updateOcorrencia(Long id, OcorrenciaRequestDTO requestDTO) {

        try {
            Ocorrencia ocorrencia = ocorrenciaRepository.getReferenceById(id);
            copyDtoToOcorrencia(requestDTO, ocorrencia);
            ocorrencia.setIndicePrioridade(calcularIndicePrioridade(ocorrencia));
            ocorrencia = ocorrenciaRepository.save(ocorrencia);
            return new OcorrenciaResponseDTO(ocorrencia);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }
    }

    @Transactional
    public void deleteOcorrenciaById(Long id) {

        Ocorrencia ocorrencia = buscarOcorrencia(id);

        if (ocorrencia.getStatus() == StatusOcorrencia.EM_ATENDIMENTO) {
            throw new RegraNegocioException("Não é possível excluir a ocorrência ID: " + id
                    + ". Ela possui um atendimento em andamento.");
        }

        ocorrenciaRepository.deleteById(id);
    }

    /**
     * Outro morador confirma que a barreira existe: +0,5 no índice (máximo +3).
     */
    @Transactional
    public OcorrenciaResponseDTO confirmarOcorrencia(Long id) {

        Ocorrencia ocorrencia = buscarOcorrencia(id);

        if (ocorrencia.getStatus() == StatusOcorrencia.RESOLVIDA) {
            throw new RegraNegocioException("Não é possível confirmar a ocorrência ID: " + id
                    + ". Ela já foi resolvida.");
        }

        ocorrencia.setConfirmacoes(ocorrencia.getConfirmacoes() + 1);
        ocorrencia.setIndicePrioridade(calcularIndicePrioridade(ocorrencia));
        ocorrencia = ocorrenciaRepository.save(ocorrencia);
        return new OcorrenciaResponseDTO(ocorrencia);
    }

    /**
     * Chamado pelo ms-atendimentos (OpenFeign) quando um atendimento é aberto.
     * ABERTA -> EM_ATENDIMENTO
     */
    @Transactional
    public OcorrenciaResponseDTO iniciarAtendimento(Long id) {

        return alterarStatus(id, StatusOcorrencia.ABERTA, StatusOcorrencia.EM_ATENDIMENTO);
    }

    /**
     * Chamado pelo ms-atendimentos (OpenFeign) quando o atendimento é concluído.
     * EM_ATENDIMENTO -> RESOLVIDA
     */
    @Transactional
    public OcorrenciaResponseDTO resolverOcorrencia(Long id) {

        return alterarStatus(id, StatusOcorrencia.EM_ATENDIMENTO, StatusOcorrencia.RESOLVIDA);
    }

    /**
     * Chamado pelo ms-atendimentos (OpenFeign) quando o atendimento é cancelado.
     * EM_ATENDIMENTO -> ABERTA
     */
    @Transactional
    public OcorrenciaResponseDTO reabrirOcorrencia(Long id) {

        return alterarStatus(id, StatusOcorrencia.EM_ATENDIMENTO, StatusOcorrencia.ABERTA);
    }

    private OcorrenciaResponseDTO alterarStatus(Long id, StatusOcorrencia statusEsperado,
                                                StatusOcorrencia novoStatus) {

        Ocorrencia ocorrencia = buscarOcorrencia(id);

        if (ocorrencia.getStatus() != statusEsperado) {
            throw new RegraNegocioException("Não é possível alterar o status da ocorrência ID: " + id
                    + " de " + ocorrencia.getStatus() + " para " + novoStatus
                    + ". Status esperado: " + statusEsperado);
        }

        ocorrencia.setStatus(novoStatus);
        ocorrencia = ocorrenciaRepository.save(ocorrencia);
        return new OcorrenciaResponseDTO(ocorrencia);
    }

    private Ocorrencia buscarOcorrencia(Long id) {

        return ocorrenciaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );
    }

    /**
     * Índice = gravidade x fator de entorno + bônus de confirmações (0,5 cada, até 3).
     * Exemplo: rampa ausente (4) perto de hospital (3) com 5 confirmações = 4 x 3 + 2,5 = 14,5
     */
    private Double calcularIndicePrioridade(Ocorrencia ocorrencia) {

        double bonus = Math.min(ocorrencia.getConfirmacoes() * BONUS_POR_CONFIRMACAO, BONUS_MAXIMO);

        return ocorrencia.getGravidade() * ocorrencia.getFatorEntorno() + bonus;
    }

    private void copyDtoToOcorrencia(OcorrenciaRequestDTO requestDTO, Ocorrencia ocorrencia) {

        ocorrencia.setTipoBarreira(requestDTO.getTipoBarreira());
        ocorrencia.setDescricao(requestDTO.getDescricao());
        ocorrencia.setLogradouro(requestDTO.getLogradouro());
        ocorrencia.setBairro(requestDTO.getBairro());
        ocorrencia.setCep(requestDTO.getCep());
        ocorrencia.setGravidade(requestDTO.getGravidade());
        ocorrencia.setFatorEntorno(requestDTO.getFatorEntorno());
    }
}

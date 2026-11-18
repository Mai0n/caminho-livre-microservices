package br.com.fiap.ms.ocorrencias.repositories;

import br.com.fiap.ms.ocorrencias.entities.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {

    // Fila de ocorrências: maior prioridade primeiro
    List<Ocorrencia> findAllByOrderByIndicePrioridadeDesc();
}

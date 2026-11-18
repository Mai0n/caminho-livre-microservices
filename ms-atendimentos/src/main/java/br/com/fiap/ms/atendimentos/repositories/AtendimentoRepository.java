package br.com.fiap.ms.atendimentos.repositories;

import br.com.fiap.ms.atendimentos.entities.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {
}

package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DesafioRepository extends JpaRepository<Desafio, Long> {

    // Procura desafios que estão ativos e dentro do prazo atual (FUTURÍSTICO: O banco faz o trabalho pesado)
    @Query("SELECT d FROM Desafio d WHERE d.ativo = true AND d.dataInicio <= :agora AND d.dataFim >= :agora")
    List<Desafio> findDesafiosAtivos(LocalDateTime agora);
}
package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TreinoManualRepository extends JpaRepository<TreinoManual, Long> {

    // Procura os treinos manuais do utilizador, ordenados do mais recente para o mais antigo
    List<TreinoManual> findByUsuarioOrderByDataHoraTreinoDesc(Usuario usuario);
}
package org.example;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Atividade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Double distancia; // em km
    private Integer tempoMinutos;
    private Integer pontosGanhos;
    private LocalDateTime dataAtividade;

    @ManyToOne // Várias atividades pertencem a UM usuário
    private Usuario usuario;

    // Construtores, Getters e Setters...
}
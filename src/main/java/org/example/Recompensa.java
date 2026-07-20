package org.example;

import jakarta.persistence.*;

@Entity
public class Recompensa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Integer custoPontos;
    private String descricao;

    // A MÁGICA RELACIONAL: Liga o prêmio à marca cadastrada (Ex: Nike)
    @ManyToOne
    @JoinColumn(name = "parceria_id")
    private Parceria parceria;

    // ==========================================
    // CONSTRUTORES
    // ==========================================
    public Recompensa() {}

    public Recompensa(String nome, Integer custo, String desc) {
        this.nome = nome;
        this.custoPontos = custo;
        this.descricao = desc;
    }

    // ==========================================
    // GETTERS E SETTERS (Essenciais para o Thymeleaf!)
    // ==========================================

    // O Getter que estava a faltar e causou o Erro 500!
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCustoPontos() {
        return custoPontos;
    }

    public void setCustoPontos(Integer custoPontos) {
        this.custoPontos = custoPontos;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // --- NOVOS GETTERS E SETTERS DA PARCERIA ---
    public Parceria getParceria() {
        return parceria;
    }

    public void setParceria(Parceria parceria) {
        this.parceria = parceria;
    }
}
package org.example;

import jakarta.persistence.*;

@Entity
public class Parceria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeEmpresa;
    private String logotipoUrl;

    // Construtor vazio (obrigatório)
    public Parceria() {}

    // ==========================================
    // GETTERS E SETTERS OBRIGATÓRIOS
    // ==========================================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getLogotipoUrl() {
        return logotipoUrl;
    }

    public void setLogotipoUrl(String logotipoUrl) {
        this.logotipoUrl = logotipoUrl;
    }
}
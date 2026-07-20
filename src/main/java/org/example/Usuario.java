package org.example;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(length = 500)
    private String stravaToken;

    @Column(columnDefinition = "integer default 0")
    private Integer stridePoints = 0;

    @Column(columnDefinition = "double precision default 50.0")
    private Double metaDistanciaKm = 50.0;

    @Column(columnDefinition = "integer default 0")
    private Integer pontosStravaAcumulados = 0;

    // ==========================================
    // NOVA MÁGICA: GUARDAR A DISTÂNCIA PARA O RANKING
    // ==========================================
    @Column(columnDefinition = "double precision default 0.0")
    private Double distanciaTotal = 0.0;

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getStravaToken() { return stravaToken; }
    public void setStravaToken(String stravaToken) { this.stravaToken = stravaToken; }
    public Integer getStridePoints() { return stridePoints; }
    public void setStridePoints(Integer stridePoints) { this.stridePoints = stridePoints; }
    public Double getMetaDistanciaKm() { return metaDistanciaKm; }
    public void setMetaDistanciaKm(Double metaDistanciaKm) { this.metaDistanciaKm = metaDistanciaKm; }
    public Integer getPontosStravaAcumulados() { return pontosStravaAcumulados; }
    public void setPontosStravaAcumulados(Integer pontosStravaAcumulados) { this.pontosStravaAcumulados = pontosStravaAcumulados; }

    // Getters e Setters da nova coluna
    public Double getDistanciaTotal() { return distanciaTotal != null ? distanciaTotal : 0.0; }
    public void setDistanciaTotal(Double distanciaTotal) { this.distanciaTotal = distanciaTotal; }
}
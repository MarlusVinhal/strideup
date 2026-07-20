package org.example;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "desafios")
public class Desafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDesafio tipo;

    @Column(nullable = false)
    private Double metaValor; // Pode ser 15.0 (km), 120.0 (minutos), 3.0 (corridas)

    @Column(nullable = false)
    private Integer recompensaSp; // Quantos SP o atleta ganha ao concluir

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    @Column(nullable = false)
    private LocalDateTime dataFim;

    @Column(columnDefinition = "boolean default true")
    private Boolean ativo = true;

    // Construtor vazio para o JPA
    public Desafio() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public TipoDesafio getTipo() { return tipo; }
    public void setTipo(TipoDesafio tipo) { this.tipo = tipo; }
    public Double getMetaValor() { return metaValor; }
    public void setMetaValor(Double metaValor) { this.metaValor = metaValor; }
    public Integer getRecompensaSp() { return recompensaSp; }
    public void setRecompensaSp(Integer recompensaSp) { this.recompensaSp = recompensaSp; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
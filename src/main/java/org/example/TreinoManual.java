package org.example;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TreinoManual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeLocalizacao;
    private Double latitude;
    private Double longitude;

    // Novos campos
    @Column(name = "desporto", length = 50)
    private String desporto;

    @Column(name = "descricao", length = 500)
    private String descricao;

    private String tipoAtividade;
    private Double distanciaKm;
    private Integer tempoMinutos;
    private Double elevacaoMetros;
    private LocalDateTime dataHoraTreino;
    private Integer pontosGerados;
    private LocalDateTime dataRegistro = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public TreinoManual() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeLocalizacao() { return nomeLocalizacao; }
    public void setNomeLocalizacao(String nomeLocalizacao) { this.nomeLocalizacao = nomeLocalizacao; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getDesporto() { return desporto; }
    public void setDesporto(String desporto) { this.desporto = desporto; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipoAtividade() { return tipoAtividade; }
    public void setTipoAtividade(String tipoAtividade) { this.tipoAtividade = tipoAtividade; }
    public Double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(Double distanciaKm) { this.distanciaKm = distanciaKm; }
    public Integer getTempoMinutos() { return tempoMinutos; }
    public void setTempoMinutos(Integer tempoMinutos) { this.tempoMinutos = tempoMinutos; }
    public Double getElevacaoMetros() { return elevacaoMetros; }
    public void setElevacaoMetros(Double elevacaoMetros) { this.elevacaoMetros = elevacaoMetros; }
    public LocalDateTime getDataHoraTreino() { return dataHoraTreino; }
    public void setDataHoraTreino(LocalDateTime dataHoraTreino) { this.dataHoraTreino = dataHoraTreino; }
    public Integer getPontosGerados() { return pontosGerados; }
    public void setPontosGerados(Integer pontosGerados) { this.pontosGerados = pontosGerados; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

}
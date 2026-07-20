package org.example.dto;

import java.time.LocalDateTime;

public class TreinoMobileDTO {

    private Double distanciaKm;
    private Integer tempoMinutos;
    private String desporto;
    private String descricao;
    private Double latitude;
    private Double longitude;
    private LocalDateTime dataHora;

    // Getters
    public Double getDistanciaKm() { return distanciaKm; }
    public Integer getTempoMinutos() { return tempoMinutos; }
    public String getDesporto() { return desporto; }
    public String getDescricao() { return descricao; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public LocalDateTime getDataHora() { return dataHora; }

    // Setters
    public void setDistanciaKm(Double distanciaKm) { this.distanciaKm = distanciaKm; }
    public void setTempoMinutos(Integer tempoMinutos) { this.tempoMinutos = tempoMinutos; }
    public void setDesporto(String desporto) { this.desporto = desporto; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
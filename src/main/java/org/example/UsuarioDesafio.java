package org.example;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_desafio", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "desafio_id"}) // Garante que o usuário só participa de um desafio uma vez
})
public class UsuarioDesafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desafio_id", nullable = false)
    private Desafio desafio;

    @Column(columnDefinition = "double precision default 0.0")
    private Double progressoAtual = 0.0; // Ex: Já correu 5km dos 15km pedidos

    @Column(columnDefinition = "boolean default false")
    private Boolean concluido = false;

    private LocalDateTime dataConclusao;

    public UsuarioDesafio() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Desafio getDesafio() { return desafio; }
    public void setDesafio(Desafio desafio) { this.desafio = desafio; }
    public Double getProgressoAtual() { return progressoAtual; }
    public void setProgressoAtual(Double progressoAtual) { this.progressoAtual = progressoAtual; }
    public Boolean getConcluido() { return concluido; }
    public void setConcluido(Boolean concluido) { this.concluido = concluido; }
    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }
}
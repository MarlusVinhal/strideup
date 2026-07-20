package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioDesafioRepository extends JpaRepository<UsuarioDesafio, Long> {
    List<UsuarioDesafio> findByUsuario(Usuario usuario);
    Optional<UsuarioDesafio> findByUsuarioAndDesafio(Usuario usuario, Desafio desafio);
}
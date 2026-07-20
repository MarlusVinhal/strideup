package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
// Só essa linha já dá ao Java o poder de Salvar, Deletar e Buscar usuários!

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // O Spring entende o inglês e cria o comando SQL de busca automaticamente!

    boolean existsByEmail(String email);
    boolean existsByNome(String nome);

    // NOVO: Ensina o Java a buscar um usuário inteiro pelo E-mail para o Login
    Optional<Usuario> findByEmail(String email);
}

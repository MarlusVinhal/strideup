package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/cadastro")
    public String exibirTelaCadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastrar")
    public String salvarUsuario(@ModelAttribute Usuario usuario) {

        if (usuarioRepository.existsByNome(usuario.getNome()) || usuarioRepository.existsByEmail(usuario.getEmail())) {
            return "redirect:/cadastro?erro=duplicado";
        }

        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        usuarioRepository.save(usuario);

        return "redirect:/login?sucesso=cadastro_realizado";
    }

    // NOTA: O método de Login foi removido daqui pois você já tem o LoginController!
}
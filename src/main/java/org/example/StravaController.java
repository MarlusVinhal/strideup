package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/strava") // Excelente prática! Define o prefixo para todas as rotas desta classe
public class StravaController {

    // Lê o ID configurado no application.properties
    @Value("${strava.client.id}")
    private String clientId;

    @Autowired
    private StravaService stravaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. ENVIA O USUÁRIO PARA O STRAVA (Acesso em /strava/conectar)
    @GetMapping("/conectar")
    public String iniciarAutorizacao() {
        String redirectUri = "http://localhost:8081/strava/callback";

        String urlStrava = "https://www.strava.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&approval_prompt=force" +
                "&scope=read,activity:read_all"; // Pede permissão para ler as atividades

        return "redirect:" + urlStrava;
    }

    // 2. RECEBE O USUÁRIO DE VOLTA DO STRAVA (Acesso em /strava/callback)
    @GetMapping("/callback")
    public String callbackStrava(@RequestParam(value = "code", required = false) String code,
                                 @RequestParam(value = "error", required = false) String error) {

        // Se o utilizador clicou em "Cancelar" na tela do Strava
        if (error != null || code == null) {
            return "redirect:/dashboard?erro=strava_negado";
        }

        // Descobre quem está logado no momento
        String emailUtilizadorLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUtilizadorLogado).orElse(null);

        if (usuario != null) {
            // AQUI ESTÁ A MÁGICA: Delegamos a troca de chaves para o Serviço!
            String accessToken = stravaService.trocarCodigoPorToken(code);

            if (accessToken != null) {
                // Guarda o token dinamicamente no perfil deste utilizador específico
                usuario.setStravaToken(accessToken);
                usuarioRepository.save(usuario);

                System.out.println("Token salvo com sucesso na conta de: " + emailUtilizadorLogado);

                return "redirect:/dashboard?sucesso=strava_conectado";
            }
        }

        // Se algo falhar na comunicação ou o utilizador não for encontrado
        return "redirect:/dashboard?erro=falha_comunicacao";
    }
}
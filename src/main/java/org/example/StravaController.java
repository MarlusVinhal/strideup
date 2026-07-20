package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/strava")
public class StravaController {

    @Value("${strava.client.id}")
    private String clientId;

    // Esta linha busca o valor do Render ou usa o localhost como reserva (fallback)
    @Value("${REDIRECT_URL:http://localhost:8081/strava/callback}")
    private String redirectUri;

    @Autowired
    private StravaService stravaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/conectar")
    public String iniciarAutorizacao() {
        // Agora usa a variável injetada que muda conforme o ambiente
        String urlStrava = "https://www.strava.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&approval_prompt=force" +
                "&scope=read,activity:read_all";

        return "redirect:" + urlStrava;
    }

    @GetMapping("/callback")
    public String callbackStrava(@RequestParam(value = "code", required = false) String code,
                                 @RequestParam(value = "error", required = false) String error) {

        if (error != null || code == null) {
            return "redirect:/dashboard?erro=strava_negado";
        }

        String emailUtilizadorLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUtilizadorLogado).orElse(null);

        if (usuario != null) {
            String accessToken = stravaService.trocarCodigoPorToken(code);

            if (accessToken != null) {
                usuario.setStravaToken(accessToken);
                usuarioRepository.save(usuario);
                return "redirect:/dashboard?sucesso=strava_conectado";
            }
        }
        return "redirect:/dashboard?erro=falha_comunicacao";
    }
}
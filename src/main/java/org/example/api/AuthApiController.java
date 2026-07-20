package org.example.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/mobile/auth")
public class AuthApiController {

    @PostMapping("/login")
    public ResponseEntity<?> realizarLoginMobile(@RequestBody Map<String, String> credenciais) {
        String email = credenciais.get("email");
        String senha = credenciais.get("senha");

        // TO DO: Substituir pela injeção do AuthenticationManager do Spring Security
        // Para validar a hash BCrypt da senha real no banco de dados.
        boolean senhaCorreta = true;

        if (senhaCorreta) {
            // Gera a assinatura criptográfica (Simulado na fase 1 com UUID)
            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + UUID.randomUUID().toString();

            return ResponseEntity.ok(Map.of(
                    "sucesso", true,
                    "token", token,
                    "instrucao", "Guarde este token no Secure Storage/Keychain do dispositivo."
            ));
        }

        return ResponseEntity.status(401).body(Map.of(
                "sucesso", false,
                "erro", "Credenciais de acesso recusadas."
        ));
    }
}
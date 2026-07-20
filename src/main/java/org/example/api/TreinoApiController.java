package org.example.api;

import org.example.DesafioService;
import org.example.TreinoManual;
import org.example.TreinoManualRepository;
import org.example.Usuario;
import org.example.UsuarioRepository;
import org.example.dto.TreinoMobileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile/treinos")
public class TreinoApiController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TreinoManualRepository treinoManualRepository;

    @Autowired
    private DesafioService desafioService;

    @PostMapping("/sincronizar")
    public ResponseEntity<?> sincronizarTreino(@RequestBody TreinoMobileDTO dto) {
        // Recupera o utilizador logado através do Token de segurança
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("erro", "Sessão inválida ou utilizador não encontrado."));
        }

        // Converte o DTO em Entidade para salvar no banco de dados
        TreinoManual treino = new TreinoManual();
        treino.setDistanciaKm(dto.getDistanciaKm());
        treino.setTempoMinutos(dto.getTempoMinutos());
        treino.setDesporto(dto.getDesporto() != null ? dto.getDesporto() : "Corrida");
        treino.setDescricao(dto.getDescricao());
        treino.setDataHoraTreino(dto.getDataHora() != null ? dto.getDataHora() : LocalDateTime.now());
        treino.setLatitude(dto.getLatitude());
        treino.setLongitude(dto.getLongitude());

        // Mantém a regra rigorosa da Economia Fechada (0 SP para o treino em si)
        treino.setPontosGerados(0);
        treino.setUsuario(usuario);

        // Persistência em Banco
        treinoManualRepository.save(treino);

        usuario.setDistanciaTotal(usuario.getDistanciaTotal() + dto.getDistanciaKm());
        usuarioRepository.save(usuario);

        // O Motor de Missões avalia se este treino mobile desbloqueou alguma recompensa
        desafioService.processarProgresso(usuario, dto.getDistanciaKm(), dto.getTempoMinutos(), 1);

        // Retorna um JSON puro confirmando o sucesso para o aplicativo
        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Treino de " + dto.getDistanciaKm() + "km sincronizado com a nuvem StrideUp!"
        ));
    }
}
package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DesafioService {

    @Autowired
    private DesafioRepository desafioRepository;

    @Autowired
    private UsuarioDesafioRepository usuarioDesafioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public void processarProgresso(Usuario usuario, double distanciaKm, int tempoMinutos, int qtdAtividades) {

        // 1. Busca todos os desafios que estão ativos e dentro do prazo na data de hoje
        List<Desafio> desafiosAtivos = desafioRepository.findDesafiosAtivos(LocalDateTime.now());

        for (Desafio desafio : desafiosAtivos) {

            // 2. Procura o rastreador deste utilizador para este desafio (ou cria um novo, se ele nunca tiver participado)
            UsuarioDesafio tracker = usuarioDesafioRepository.findByUsuarioAndDesafio(usuario, desafio)
                    .orElseGet(() -> {
                        UsuarioDesafio novoTracker = new UsuarioDesafio();
                        novoTracker.setUsuario(usuario);
                        novoTracker.setDesafio(desafio);
                        novoTracker.setProgressoAtual(0.0);
                        novoTracker.setConcluido(false);
                        return novoTracker;
                    });

            // Se o utilizador já concluiu este desafio e já ganhou o prémio, ignoramos e passamos para o próximo
            if (tracker.getConcluido()) {
                continue;
            }

            // 3. Adiciona o progresso com base na regra específica do desafio
            double incremento = 0.0;
            switch (desafio.getTipo()) {
                case DISTANCIA_ACUMULADA:
                    incremento = distanciaKm;
                    break;
                case TEMPO_ACUMULADO:
                    incremento = tempoMinutos;
                    break;
                case QUANTIDADE_ATIVIDADES:
                    incremento = qtdAtividades;
                    break;
            }

            if (incremento > 0) {
                tracker.setProgressoAtual(tracker.getProgressoAtual() + incremento);

                // 4. VERIFICAÇÃO DE VITÓRIA: Bateu a meta?
                if (tracker.getProgressoAtual() >= desafio.getMetaValor()) {
                    tracker.setProgressoAtual(desafio.getMetaValor()); // Crava em 100% para não passar do limite visual
                    tracker.setConcluido(true);
                    tracker.setDataConclusao(LocalDateTime.now());

                    // 5. Deposita a recompensa diretamente na conta do utilizador!
                    usuario.setStridePoints(usuario.getStridePoints() + desafio.getRecompensaSp());
                    usuarioRepository.save(usuario);

                    System.out.println("=================================================");
                    System.out.println("🎉 DESAFIO CONCLUÍDO POR: " + usuario.getNome());
                    System.out.println("🏆 Missão: " + desafio.getTitulo());
                    System.out.println("💰 Recompensa: +" + desafio.getRecompensaSp() + " SP");
                    System.out.println("=================================================");
                }

                // Salva o progresso no banco de dados
                usuarioDesafioRepository.save(tracker);
            }
        }
    }
}
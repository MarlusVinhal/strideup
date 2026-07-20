package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Controller
public class DashboardController {

    @Autowired
    private DesafioRepository desafioRepository;

    @Autowired
    private UsuarioDesafioRepository usuarioDesafioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private StravaService stravaService;

    @Autowired
    private RecompensaRepository recompensaRepository;

    @Autowired
    private TreinoManualRepository treinoManualRepository;

    @Autowired
    private DesafioService desafioService;

    private String calcularPace(double distanciaKm, int tempoMinutos) {
        if (distanciaKm <= 0) return "0'00\"";
        double paceDecimal = tempoMinutos / distanciaKm;
        int minutos = (int) paceDecimal;
        int segundos = (int) Math.round((paceDecimal - minutos) * 60);
        return String.format("%d'%02d\"", minutos, segundos);
    }

    private int calcularCalorias(double distanciaKm) {
        return (int) (distanciaKm * 65);
    }

    private String calcularPatente(int pontos) {
        if (pontos < 500) return "Iniciante";
        if (pontos < 1500) return "Amador";
        return "Elite";
    }

    private List<Map<String, Object>> gerarFeedUnificado(Usuario usuario, boolean stravaConectado) throws RuntimeException {
        List<Map<String, Object>> feedUnificado = new ArrayList<>();
        int marcadoresStravaLidos = 0;

        if (stravaConectado && usuario.getStravaToken() != null) {
            List<Map<String, Object>> corridasStrava = stravaService.buscarUltimasCorridas(usuario.getStravaToken());
            for (Map<String, Object> c : corridasStrava) {
                Map<String, Object> item = new HashMap<>();
                item.put("nome", c.get("name"));

                double dist = ((Number) c.get("distance")).doubleValue() / 1000.0;
                int marcador = (int) (dist / 10.0);
                int tempoMinutos = ((Number) c.get("moving_time")).intValue() / 60;

                item.put("distancia", dist);
                item.put("tempo", tempoMinutos);
                item.put("pace", calcularPace(dist, tempoMinutos));
                item.put("calorias", calcularCalorias(dist));

                item.put("desporto", "Corrida");
                item.put("descricao", "");
                item.put("pontos", 0);
                item.put("tipo", "STRAVA");

                Object elev = c.get("total_elevation_gain");
                item.put("elevacao", elev != null ? elev : 0.0);
                item.put("polyline", c.get("map") instanceof Map ? ((Map<?,?>) c.get("map")).get("summary_polyline") : null);
                item.put("latitude", null);
                item.put("longitude", null);

                feedUnificado.add(item);
                marcadoresStravaLidos += marcador;
            }

            int marcadoresSalvos = usuario.getPontosStravaAcumulados() != null ? usuario.getPontosStravaAcumulados() : 0;
            if (marcadoresStravaLidos > marcadoresSalvos) {
                int deltaMarcadores = marcadoresStravaLidos - marcadoresSalvos;
                double distanciaNovaStrava = deltaMarcadores * 10.0;
                int tempoNovoStrava = (int) (distanciaNovaStrava * 6);

                usuario.setPontosStravaAcumulados(marcadoresStravaLidos);
                usuarioRepository.save(usuario);

                desafioService.processarProgresso(usuario, distanciaNovaStrava, tempoNovoStrava, 1);
            }
        }

        List<TreinoManual> treinosManuais = treinoManualRepository.findByUsuarioOrderByDataHoraTreinoDesc(usuario);
        for (TreinoManual tm : treinosManuais) {
            Map<String, Object> item = new HashMap<>();
            String nomeLocal = (tm.getNomeLocalizacao() != null && !tm.getNomeLocalizacao().trim().isEmpty()) ? tm.getNomeLocalizacao() : "Local Não Informado";
            item.put("nome", nomeLocal);
            item.put("distancia", tm.getDistanciaKm());
            item.put("tempo", tm.getTempoMinutos());

            item.put("pace", calcularPace(tm.getDistanciaKm(), tm.getTempoMinutos()));
            item.put("calorias", calcularCalorias(tm.getDistanciaKm()));

            item.put("pontos", 0);
            item.put("tipo", "MANUAL");
            item.put("desporto", tm.getDesporto() != null ? tm.getDesporto() : "Corrida");
            item.put("descricao", tm.getDescricao() != null ? tm.getDescricao() : "");
            item.put("elevacao", tm.getElevacaoMetros() != null ? tm.getElevacaoMetros() : 0.0);
            item.put("latitude", tm.getLatitude());
            item.put("longitude", tm.getLongitude());
            item.put("polyline", null);

            feedUnificado.add(item);
        }

        double distanciaRealAcumulada = 0.0;
        for (Map<String, Object> item : feedUnificado) {
            distanciaRealAcumulada += (double) item.get("distancia");
        }

        if (usuario.getDistanciaTotal() == null || Math.abs(usuario.getDistanciaTotal() - distanciaRealAcumulada) > 0.1) {
            usuario.setDistanciaTotal(distanciaRealAcumulada);
            usuarioRepository.save(usuario);
        }

        return feedUnificado;
    }

    @GetMapping("/dashboard")
    public String exibirDashboard(Model model) {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
        if (usuario == null) return "redirect:/login";

        boolean stravaConectado = (usuario.getStravaToken() != null && !usuario.getStravaToken().isEmpty());
        List<Map<String, Object>> feedUnificado = new ArrayList<>();

        try {
            feedUnificado = gerarFeedUnificado(usuario, stravaConectado);
        } catch (RuntimeException e) {
            if ("TOKEN_EXPIRADO".equals(e.getMessage())) {
                usuario.setStravaToken(null);
                usuarioRepository.save(usuario);
                return "redirect:/dashboard?erro=strava_expirado";
            }
        }

        double distanciaTotalAtleta = usuario.getDistanciaTotal();
        int tempoTotalAtleta = feedUnificado.stream().mapToInt(t -> (int) t.get("tempo")).sum();

        Double meta = usuario.getMetaDistanciaKm();
        if (meta == null || meta == 0) meta = 50.0;
        double progresso = Math.min((distanciaTotalAtleta / meta) * 100.0, 100.0);

        // =========================================================================
        // NOVIDADE 1: LÓGICA DO RADAR DE MISSÕES
        // =========================================================================
        List<Desafio> desafiosAtivos = desafioRepository.findDesafiosAtivos(LocalDateTime.now());
        List<Map<String, Object>> radarDesafios = new ArrayList<>();

        for (Desafio d : desafiosAtivos) {
            UsuarioDesafio tracker = usuarioDesafioRepository.findByUsuarioAndDesafio(usuario, d).orElse(null);
            double progressoAtual = (tracker != null) ? tracker.getProgressoAtual() : 0.0;
            boolean concluido = (tracker != null) ? tracker.getConcluido() : false;

            if (!concluido) { // Só entra no Radar os desafios que estão pendentes
                Map<String, Object> map = new HashMap<>();
                map.put("titulo", d.getTitulo());

                double falta = d.getMetaValor() - progressoAtual;
                map.put("falta", falta);

                String unidade = d.getTipo() == TipoDesafio.DISTANCIA_ACUMULADA ? "km" : (d.getTipo() == TipoDesafio.TEMPO_ACUMULADO ? "min" : "treinos");
                map.put("unidade", unidade);

                double percentual = Math.min((progressoAtual / d.getMetaValor()) * 100.0, 100.0);
                map.put("percentual", percentual);

                // Inteligência Emocional (O sistema responde dependendo da proximidade)
                String mensagem;
                if (percentual == 0) mensagem = "Ação! Comece hoje e saia do zero.";
                else if (percentual < 50) mensagem = "Longo caminho, mas o progresso já começou.";
                else if (percentual < 85) mensagem = "Você passou da metade! Não desista agora.";
                else mensagem = "Reta final! A vitória é iminente.";
                map.put("mensagem", mensagem);

                radarDesafios.add(map);
            }
        }

        // Ordena para mostrar os desafios mais próximos do fim primeiro
        radarDesafios.sort((m1, m2) -> Double.compare((Double)m2.get("percentual"), (Double)m1.get("percentual")));

        // Limita o radar aos 2 desafios mais importantes
        if (radarDesafios.size() > 2) radarDesafios = radarDesafios.subList(0, 2);

        model.addAttribute("radarDesafios", radarDesafios);

        // =========================================================================
        // NOVIDADE 2: CÁPSULA DE CURIOSIDADES DINÂMICAS
        // =========================================================================
        String[] curiosidades = {
                "O corpo humano tem mais de 600 músculos, e a corrida recruta cerca de 200 deles simultaneamente para manter o equilíbrio.",
                "O algoritmo de Dijkstra, estudado em Sistemas de Informação para achar o caminho mais curto num grafo, é a base matemática que permite ao GPS traçar e calcular as rotas dos seus treinos de hoje.",
                "A gestão da energia muscular numa longa corrida é como a alocação de memória dinâmica em programação C: requer precisão para liberar recursos e impedir que o sistema trave antes da chegada.",
                "Para dar uma volta completa a correr pela malha urbana principal de Uberlândia, você precisaria cobrir um perímetro de várias dezenas de quilómetros. Um desafio digno de um maratonista local!",
                "Apenas 30 minutos de exercício aeróbico leve são suficientes para oxigenar o cérebro, facilitando a resolução de lógicas complexas e de matemática exata.",
                "Ouvir música entre 120 e 140 BPM durante o treino pode otimizar a sua biomecânica e aumentar o seu rendimento em até 15%."
        };
        model.addAttribute("curiosidadeDoDia", curiosidades[new Random().nextInt(curiosidades.length)]);

        // Variáveis padrão da View
        model.addAttribute("nomeUsuario", usuario.getNome());
        model.addAttribute("stridePointsTotais", usuario.getStridePoints());
        model.addAttribute("patenteAtleta", calcularPatente(usuario.getStridePoints()));
        model.addAttribute("stravaConectado", stravaConectado);
        model.addAttribute("distanciaTotal", distanciaTotalAtleta);
        model.addAttribute("tempoTotal", tempoTotalAtleta);
        model.addAttribute("totalAtividades", feedUnificado.size());
        model.addAttribute("metaAtual", meta);
        model.addAttribute("progressoMeta", progresso);

        return "dashboard";
    }

    @GetMapping("/treinos")
    public String exibirTreinos(Model model) {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
        if (usuario == null) return "redirect:/login";

        boolean stravaConectado = (usuario.getStravaToken() != null && !usuario.getStravaToken().isEmpty());

        try {
            model.addAttribute("corridas", gerarFeedUnificado(usuario, stravaConectado));
        } catch (RuntimeException e) {
            if ("TOKEN_EXPIRADO".equals(e.getMessage())) {
                usuario.setStravaToken(null);
                usuarioRepository.save(usuario);
                return "redirect:/treinos?erro=strava_expirado";
            }
        }

        model.addAttribute("stridePointsTotais", usuario.getStridePoints());
        model.addAttribute("patenteAtleta", calcularPatente(usuario.getStridePoints()));
        model.addAttribute("stravaConectado", stravaConectado);

        return "treinos";
    }

    @GetMapping("/desafios")
    public String exibirDesafios(Model model) {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
        if (usuario == null) return "redirect:/login";

        List<Desafio> desafiosAtivos = desafioRepository.findDesafiosAtivos(LocalDateTime.now());
        List<Map<String, Object>> desafiosView = new ArrayList<>();

        for (Desafio d : desafiosAtivos) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getId());
            map.put("titulo", d.getTitulo());
            map.put("descricao", d.getDescricao());
            map.put("meta", d.getMetaValor());
            map.put("recompensa", d.getRecompensaSp());

            String unidade = "";
            String icone = "";
            if (d.getTipo() == TipoDesafio.DISTANCIA_ACUMULADA) { unidade = "km"; icone = "fas fa-road"; }
            else if (d.getTipo() == TipoDesafio.TEMPO_ACUMULADO) { unidade = "min"; icone = "fas fa-stopwatch"; }
            else if (d.getTipo() == TipoDesafio.QUANTIDADE_ATIVIDADES) { unidade = "treinos"; icone = "fas fa-bolt"; }

            map.put("unidade", unidade);
            map.put("icone", icone);

            UsuarioDesafio tracker = usuarioDesafioRepository.findByUsuarioAndDesafio(usuario, d).orElse(null);
            double progressoAtual = (tracker != null) ? tracker.getProgressoAtual() : 0.0;
            boolean concluido = (tracker != null) ? tracker.getConcluido() : false;

            map.put("progressoAtual", progressoAtual);
            map.put("concluido", concluido);

            double percentual = Math.min((progressoAtual / d.getMetaValor()) * 100.0, 100.0);
            map.put("percentual", percentual);

            desafiosView.add(map);
        }

        model.addAttribute("desafios", desafiosView);
        model.addAttribute("stridePointsTotais", usuario.getStridePoints());
        model.addAttribute("patenteAtleta", calcularPatente(usuario.getStridePoints()));

        return "desafios";
    }

    @PostMapping("/dashboard/meta")
    public String atualizarMeta(@RequestParam Double novaMeta) {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
        if (usuario != null) {
            usuario.setMetaDistanciaKm(novaMeta);
            usuarioRepository.save(usuario);
        }
        return "redirect:/dashboard?sucesso_meta=true";
    }

    @PostMapping("/treinos/manual")
    public String registrarTreinoManual(@RequestParam Double distancia,
                                        @RequestParam Integer tempo,
                                        @RequestParam String localizacao,
                                        @RequestParam String desporto,
                                        @RequestParam String dataHora,
                                        @RequestParam(required = false) String descricao,
                                        @RequestParam(required = false) Double latitude,
                                        @RequestParam(required = false) Double longitude,
                                        @RequestParam(required = false, defaultValue = "0.0") Double elevacao) {

        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);

        if (usuario != null) {

            TreinoManual treino = new TreinoManual();
            treino.setDistanciaKm(distancia);
            treino.setTempoMinutos(tempo);
            treino.setNomeLocalizacao(localizacao);
            treino.setLatitude(latitude);
            treino.setLongitude(longitude);
            treino.setElevacaoMetros(elevacao);

            treino.setDesporto(desporto);
            treino.setDescricao(descricao);

            if (dataHora != null && !dataHora.isEmpty()) {
                treino.setDataHoraTreino(LocalDateTime.parse(dataHora));
            } else {
                treino.setDataHoraTreino(LocalDateTime.now());
            }

            treino.setPontosGerados(0);
            treino.setUsuario(usuario);
            treinoManualRepository.save(treino);

            usuario.setDistanciaTotal(usuario.getDistanciaTotal() + distancia);
            usuarioRepository.save(usuario);

            desafioService.processarProgresso(usuario, distancia, tempo, 1);

            return "redirect:/dashboard?sucesso_treino=true";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/ranking")
    public String exibirRanking(@RequestParam(required = false, defaultValue = "global") String filtro, Model model) {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioLogado = usuarioRepository.findByEmail(emailUsuario).orElse(null);
        if (usuarioLogado == null) return "redirect:/login";

        List<Usuario> rankingGlobal = usuarioRepository.findAll(Sort.by(Sort.Direction.DESC, "distanciaTotal"));
        List<Usuario> rankingExibicao = new ArrayList<>();

        String localAtual = "Sua Região";
        TreinoManual ultimoTreino = treinoManualRepository.findByUsuarioOrderByDataHoraTreinoDesc(usuarioLogado).stream().findFirst().orElse(null);

        if (ultimoTreino != null && ultimoTreino.getNomeLocalizacao() != null && !ultimoTreino.getNomeLocalizacao().isEmpty()) {
            localAtual = ultimoTreino.getNomeLocalizacao().split(",")[0].trim();
        }

        if ("local".equals(filtro) && !localAtual.equals("Sua Região")) {
            String finalLocalAtual = localAtual;
            for (Usuario u : rankingGlobal) {
                boolean correuNaCidade = treinoManualRepository.findByUsuarioOrderByDataHoraTreinoDesc(u)
                        .stream()
                        .anyMatch(t -> t.getNomeLocalizacao() != null && t.getNomeLocalizacao().contains(finalLocalAtual));

                if (correuNaCidade) {
                    rankingExibicao.add(u);
                }
            }
        } else {
            rankingExibicao = rankingGlobal;
        }

        model.addAttribute("ranking", rankingExibicao);
        model.addAttribute("emailUsuarioLogado", emailUsuario);
        model.addAttribute("filtroAtivo", filtro);
        model.addAttribute("localAtual", localAtual);

        int pontos = usuarioLogado.getStridePoints();
        model.addAttribute("stridePointsTotais", pontos);
        model.addAttribute("patenteAtleta", calcularPatente(pontos));

        return "ranking";
    }

    @GetMapping("/recompensas")
    public String exibirRecompensas(Model model) {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
        int pontos = (usuario != null) ? usuario.getStridePoints() : 0;
        model.addAttribute("recompensas", recompensaRepository.findAll());
        model.addAttribute("stridePointsTotais", pontos);
        model.addAttribute("patenteAtleta", calcularPatente(pontos));
        return "recompensas";
    }

    @PostMapping("/recompensas/resgatar")
    public String resgatarRecompensa(@RequestParam Long recompensaId, RedirectAttributes attributes) {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
        Recompensa recompensa = recompensaRepository.findById(recompensaId).orElse(null);

        if (usuario != null && recompensa != null) {
            if (usuario.getStridePoints() >= recompensa.getCustoPontos()) {
                usuario.setStridePoints(usuario.getStridePoints() - recompensa.getCustoPontos());
                usuarioRepository.save(usuario);

                String codigoCupom = "STRIDE-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

                attributes.addAttribute("sucesso_resgate", "true");
                attributes.addAttribute("premio", recompensa.getNome());
                attributes.addAttribute("cupom", codigoCupom);

                return "redirect:/recompensas";
            }
        }
        attributes.addAttribute("erro_resgate", "true");
        return "redirect:/recompensas";
    }
}
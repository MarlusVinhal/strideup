package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    @Autowired
    private ParceriaRepository parceriaRepository;

    @Autowired
    private RecompensaRepository recompensaRepository;

    @Autowired
    private DesafioRepository desafioRepository;

    private boolean isNaoAdmin() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return !emailLogado.equals("admin@strideup.com");
    }

    // ==========================================
    // PARCERIAS (MARCAS)
    // ==========================================
    @GetMapping("/admin/parcerias/nova")
    public String exibirFormularioParceria(@RequestParam(required = false) Long id, Model model) {
        if (isNaoAdmin()) return "redirect:/dashboard";

        Parceria parceria = (id != null) ? parceriaRepository.findById(id).orElse(new Parceria()) : new Parceria();
        model.addAttribute("parceria", parceria);
        model.addAttribute("listaParcerias", parceriaRepository.findAll()); // Envia a lista para poder editar
        return "nova-parceria";
    }

    @PostMapping("/admin/parcerias/salvar")
    public String salvarParceria(@ModelAttribute Parceria parceria) {
        if (isNaoAdmin()) return "redirect:/dashboard";
        parceriaRepository.save(parceria); // Se tiver ID ele atualiza, se não tiver ele cria
        return "redirect:/admin/parcerias/nova?sucesso=true";
    }

    // ==========================================
    // RECOMPENSAS (PRÊMIOS)
    // ==========================================
    @GetMapping("/admin/recompensas/nova")
    public String exibirFormularioRecompensa(@RequestParam(required = false) Long id, Model model) {
        if (isNaoAdmin()) return "redirect:/dashboard";

        Recompensa recompensa = (id != null) ? recompensaRepository.findById(id).orElse(new Recompensa()) : new Recompensa();
        model.addAttribute("recompensa", recompensa);
        model.addAttribute("listaParcerias", parceriaRepository.findAll()); // Para o select
        model.addAttribute("listaRecompensas", recompensaRepository.findAll()); // Para a lista de edição
        return "nova-recompensa";
    }

    @PostMapping("/admin/recompensas/salvar")
    public String salvarRecompensa(@ModelAttribute Recompensa recompensa) {
        if (isNaoAdmin()) return "redirect:/dashboard";
        recompensaRepository.save(recompensa);
        return "redirect:/admin/recompensas/nova?sucesso=true";
    }

    // ==========================================
    // DESAFIOS (MISSÕES)
    // ==========================================
    @GetMapping("/admin/desafios/novo")
    public String novoDesafio(@RequestParam(required = false) Long id, Model model) {
        if (isNaoAdmin()) return "redirect:/dashboard";

        Desafio desafio = (id != null) ? desafioRepository.findById(id).orElse(new Desafio()) : new Desafio();
        model.addAttribute("desafio", desafio);
        model.addAttribute("listaDesafios", desafioRepository.findAll()); // Para a lista de edição
        return "novo-desafio";
    }

    @PostMapping("/admin/desafios/salvar")
    public String salvarDesafio(@ModelAttribute Desafio desafio) {
        if (isNaoAdmin()) return "redirect:/dashboard";
        if (desafio.getAtivo() == null) desafio.setAtivo(true);
        desafioRepository.save(desafio);
        return "redirect:/admin/desafios/novo?sucesso=true";
    }
}
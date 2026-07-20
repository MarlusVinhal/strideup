package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Avisa o Java: "Quando o usuário acessar /login, mostre o arquivo login.html"
    @GetMapping("/login")
    public String exibirLogin() {
        return "login";
    }
}

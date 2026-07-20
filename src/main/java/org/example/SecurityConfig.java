package org.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desliga uma proteção complexa para facilitar nosso ambiente local
                .csrf(csrf -> csrf.disable())

                // Define quem pode entrar em quais telas e APIs
                .authorizeHttpRequests(auth -> auth
                        // Libera imagens, css, telas de login web E a nossa nova API de login mobile!
                        .requestMatchers("/cadastro", "/cadastrar", "/login", "/css/**", "/js/**", "/img/**", "/api/mobile/auth/**").permitAll()
                        // Qualquer outra rota (incluindo o envio do GPS) exige autenticação
                        .anyRequest().authenticated()
                )

                // 1. MODO WEB: Mantém a página HTML a funcionar para o computador
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )

                // 2. MODO API: Permite que o Postman e o App Mobile se autentiquem com cabeçalhos HTTP
                .httpBasic(org.springframework.security.config.Customizer.withDefaults())

                .logout(logout -> logout.permitAll());

        return http.build();
    }

    // A ferramenta que criptografa a senha na hora do cadastro e compara na hora do login
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
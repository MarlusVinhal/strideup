package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StravaService {

    // Lê as credenciais guardadas no seu application.properties
    @Value("${strava.client.id}")
    private String clientId;

    @Value("${strava.client.secret}")
    private String clientSecret;

    // 1. MÉTODO EXISTENTE: Busca as corridas e identifica tokens expirados
    public List<Map<String, Object>> buscarUltimasCorridas(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // O URL oficial da API do Strava para buscar as atividades do atleta (pedimos as últimas 3)
            String url = "https://www.strava.com/api/v3/athlete/activities?per_page=3";

            // Colocamos o Token no cabeçalho do pedido (como um crachá de identificação)
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Fazemos o pedido GET
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                System.out.println("Corridas importadas com sucesso!");
                return (List<Map<String, Object>>) response.getBody();
            }
        } catch (HttpClientErrorException e) {
            // O SISTEMA AUTO-CURÁVEL: Se o erro for 401 (Não Autorizado), significa que o token expirou!
            if (e.getStatusCode().value() == 401) {
                throw new RuntimeException("TOKEN_EXPIRADO");
            }
            System.out.println("Erro do cliente Strava: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao buscar treinos no Strava: " + e.getMessage());
        }
        return new ArrayList<>(); // Retorna uma lista vazia se algo correr mal
    }

    // 2. NOVO MÉTODO: O "Aperto de Mão" final do OAuth 2.0 (Troca o código temporário pelo token real)
    public String trocarCodigoPorToken(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://www.strava.com/oauth/token";

            // Monta o corpo da requisição exigido pelo Strava
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("code", code);
            body.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            // Dispara o POST para o Strava
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Sucesso! Retorna o token definitivo
                return (String) response.getBody().get("access_token");
            }
        } catch (Exception e) {
            System.out.println("Erro fatal ao trocar código por token: " + e.getMessage());
        }
        return null;
    }
}
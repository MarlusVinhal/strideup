# 🏃‍♂️ StrideUp

O **StrideUp** é uma plataforma de gamificação desenvolvida para incentivar a constância em exercícios físicos. Ao conectar o esforço do atleta a um sistema real de parcerias, o projeto transforma quilômetros percorridos em evolução de status e pontos virtuais, que podem ser trocados por recompensas reais (como descontos e isenções) na loja do aplicativo.

Este projeto foi desenvolvido como parte de um projeto acadêmico do Bacharelado em Sistemas de Informação.

---

## 🎯 A Regra de Negócio (Economia do App)
A lógica principal do StrideUp baseia-se na separação de evolução vitalícia e moeda de troca:
* **Patente (Status):** Calculada exclusivamente pela **distância total** acumulada do atleta (ex: Iniciante, Amador, Elite). O status nunca diminui.
* **Stride Points (SP):** Moeda virtual ganha através de treinos e desafios. Pode ser gasta na Loja de Recompensas sem afetar a patente do usuário.

---

## ✨ Principais Funcionalidades

* **Integração com Strava (OAuth2):** Sincronização automática de corridas e atividades registradas no Strava.
* **Registro de Treino Manual (GPS Integrado):** Permite inserir atividades offline utilizando a API do Photon/Nominatim para captura e validação da localização via coordenadas satélites.
* **Radar de Missões:** Sistema dinâmico de desafios (por distância, tempo ou quantidade de treinos) com inteligência que adapta as mensagens de motivação baseada no progresso atual.
* **Loja de Recompensas:** Catálogo de parceiros onde o usuário troca *Stride Points* por cupons exclusivos (ex: isenção de mensalidade em academias parceiras).
* **Ranking Híbrido:** Placares globais e locais, comparando o desempenho do atleta com outros usuários da mesma região.
* **Painel Administrativo:** Área restrita para cadastro de novas parcerias, recompensas e criação de desafios dinâmicos.
* **Suporte a Temas:** Interface totalmente adaptável com alternância nativa entre *Dark Mode* e *Light Mode*.

---

## 🛠️ Tecnologias e Arquitetura

O sistema foi construído sobre a arquitetura **MVC (Model-View-Controller)**, garantindo forte separação de responsabilidades.

**Back-end:**
* Java
* Spring Boot
* Spring Data JPA / Hibernate
* Spring Security (OAuth2)

**Front-end:**
* HTML5 / CSS3 / JavaScript (Vanilla)
* Thymeleaf (Template Engine)
* Bootstrap 5 (UI/UX)

**Banco de Dados e Infraestrutura:**
* PostgreSQL (Modelagem relacional com integridade referencial via `CASCADE`)

---

## 🚀 Como Executar o Projeto Localmente

### Pré-requisitos
* Java 17 ou superior
* Maven
* PostgreSQL instalado e rodando na porta padrão (5432)
* Uma conta de desenvolvedor no Strava (para obter Client ID e Secret)

### 1. Configuração do Banco de Dados
No terminal do PostgreSQL (`psql`), crie o banco de dados:
```sql
CREATE DATABASE strideup;

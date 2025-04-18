# 💼 Gerenciador de Portfólios

Sistema desenvolvido para gerenciar o portfólio de projetos de uma empresa, desde a fase de viabilidade até a conclusão. O sistema permite o acompanhamento do ciclo de vida dos projetos, gerenciamento de equipe, controle de orçamento, risco e geração de relatórios.

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
- **Spring Data JPA**
- **Spring Security**
- **Hibernate**
- **PostgreSQL**
- **Swagger / OpenAPI**
- **JUnit 5**
- **Mockito**
- **Lombok**
- **ModelMapper**

---

## 📋 Regras de Negócio

### Projetos

- CRUD completo de projetos com os seguintes campos:
  - Nome
  - Data de início
  - Previsão de término
  - Data real de término
  - Orçamento total
  - Descrição
  - Gerente responsável (relacionado à entidade **Membro**)
  - Status atual

### Classificação de Risco (calculada automaticamente)

| Risco       | Orçamento                            | Prazo                |
|-------------|--------------------------------------|----------------------|
| **Baixo**   | Até R$ 100.000                       | ≤ 3 meses            |
| **Médio**   | R$ 100.001 - R$ 500.000              | 3 a 6 meses          |
| **Alto**    | Acima de R$ 500.000                  | Superior a 6 meses   |

### Status do Projeto (ordem obrigatória)

> **em_análise → análise_realizada → análise_aprovada → iniciado → planejado → em_andamento → encerrado**

- **cancelado** pode ser aplicado a qualquer momento
- A transição de status respeita a sequência lógica (sem pular etapas)
- Projetos com status **iniciado**, **em andamento** ou **encerrado** **não podem ser excluídos**

### Membros

- Cadastro via API REST externa (mockada), enviando **nome** e **atribuição (cargo)**
- Apenas membros com atribuição `"funcionário"` podem ser associados a projetos
- Um projeto pode conter de **1 a 10 membros**
- Um membro pode participar de no máximo **3 projetos ativos** (com status diferente de encerrado ou cancelado)

### Relatório Resumido (endpoint exclusivo)

- Quantidade de projetos por status
- Total orçado por status
- Média de duração dos projetos encerrados
- Total de membros únicos alocados

---

## 🧱 Arquitetura e Boas Práticas

- Arquitetura **MVC**
- Separação clara entre as camadas:
  - **Controller**
  - **Service**
  - **Repository**
- Uso de **DTOs** e **ModelMapper** para mapeamento entre entidades e objetos de transporte
- Princípios de **Clean Code** e **SOLID**
- Tratamento global de exceções com `@ControllerAdvice`
- Implementação de **paginação e filtros** para listagem de projetos
- Segurança básica com **Spring Security** (usuário/senha em memória)
- Testes unitários cobrindo pelo menos **70% das regras de negócio**

---

## 🔐 Segurança

- Login básico com Spring Security
- Credenciais estão armazenadas em memória
- Proteção de endpoints sensíveis

---

## 🧪 Testes

- Framework de testes: **JUnit 5 + Mockito**
- Testes com foco em regras de negócio e cobertura mínima de 70%
- Mocks para dependências externas e base de dados

---

## 🧾 Documentação da API

- Disponível via **Swagger/OpenAPI**
- Acesse em: `http://localhost:8080/swagger-ui.html`

---

## 🛠 Como executar o projeto

### Pré-requisitos

- Java 21
- Maven
- PostgreSQL

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/EzequielSR/GerenciadorPortfolios.git
   ```
2. Crie o banco de dados PostgreSQL:
   ```sql
   CREATE DATABASE gerenciador_portfolios;
   ```
3. Configure o arquivo **applicattion.properties** com as credenciais do seu banco de dados.
4. Rode o projeto:
   ```bash
   ./mvnw spring-boot:run
   ```
6. Acesse o swagger para vizualidar a API (http://localhost:8080/swagger-ui.html)

---

## 📂 Estrutura do Projeto
```bash
src
├── main
│   ├── java
│   │   └── com.example.GerenciadorPortfolios
│   │       ├── config
│   │       ├── controller
│   │       ├── dto
│   │       ├── exception
|   |       ├── mapper
│   │       ├── model
│   │       ├── repository
│   │       ├── service
│   │       └── GerenciadorPortfoliosApplication.java
│   └── resources
│       ├── application.properties
│       └── ...
├── test
│   └── ...
|        └── ProjetoServiceTest.java
```

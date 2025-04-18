# 💼 Gerenciador de Portfólios de Projetos

Sistema completo de gerenciamento de portfólio de projetos, desenvolvido como parte de um desafio técnico em Java. Permite acompanhar o ciclo de vida de projetos, desde a análise de viabilidade até a finalização, com controle de orçamento, equipe e risco.

---

## 📚 Sumário

- [🚀 Tecnologias Utilizadas](#-tecnologias-utilizadas)  
- [🎯 Objetivo do Projeto](#-objetivo-do-projeto)  
- [📦 Funcionalidades](#-funcionalidades)  
- [📐 Arquitetura](#-arquitetura)  
- [🔐 Segurança](#-segurança)  
- [📄 Documentação da API](#-documentação-da-api)  
- [🧪 Testes](#-testes)  
- [📁 Como Rodar Localmente](#-como-rodar-localmente)  
- [🛠️ Melhorias Futuras](#️-melhorias-futuras)

---

## 🚀 Tecnologias Utilizadas

- Java 17  
- Spring Boot  
- Spring Data JPA  
- Hibernate  
- PostgreSQL  
- Spring Security  
- Swagger / OpenAPI  
- Lombok  
- JUnit 5  
- Mockito

---

## 🎯 Objetivo do Projeto

Construir uma API RESTful que permita:

- O cadastro, atualização, exclusão e consulta de projetos;
- Gerenciamento de status, orçamento, risco e equipe;
- Geração de relatório gerencial do portfólio de projetos;
- Integração com API externa para cadastro de membros;
- Aplicação das melhores práticas de desenvolvimento com arquitetura limpa, testes e segurança.

---

## 📦 Funcionalidades

- ✅ CRUD completo de Projetos
- ✅ Cálculo automático de **nível de risco** com base em orçamento e duração
- ✅ **Controle de status** com fluxo lógico fixo:
  - em análise → análise realizada → análise aprovada → iniciado → planejado → em andamento → encerrado
  - Cancelado pode ser aplicado a qualquer momento
- ✅ Restrições de exclusão conforme status atual do projeto
- ✅ Integração com API mockada para **cadastro e consulta de membros**
- ✅ Regras de alocação de membros:
  - Apenas membros com **atribuição “funcionário”** podem ser alocados
  - Cada projeto aceita de **1 a 10 membros**
  - Um membro pode estar em no máximo **3 projetos ativos**
- ✅ Geração de **relatório gerencial**:
  - Quantidade de projetos por status
  - Total orçado por status
  - Média de duração dos projetos encerrados
  - Total de membros únicos alocados
- ✅ Filtros e paginação na listagem de projetos

---

## 📐 Arquitetura

O projeto segue o padrão **MVC (Model - View - Controller)** com separação clara de responsabilidades:
 ```bash
  com.ezequiel.gerenciadorportfolios
├── config               # Configurações de segurança e CORS
├── controller           # Camada de entrada (API REST)
├── dto                 # Objetos de transferência de dados
├── exception            # Tratamento global de erros
├── model                # Entidades JPA
├── repository           # Interface com o banco de dados
├── service              # Lógica de negócio
├── util                 # Utilitários e helpers

 ```

- Utilização de **DTOs** e mapeamento entre entidades com **ModelMapper**
- Camadas desacopladas e aplicação dos princípios **SOLID** e **Clean Code**

---

## 🔐 Segurança

- Implementação de autenticação **básica** com **Spring Security**
- Usuário/senha configurados em memória:
  - **Usuário:** `admin`
  - **Senha:** `admin123`

---

## 📄 Documentação da API

A documentação interativa está disponível via Swagger:

🔗 `http://localhost:8080/swagger-ui.html`  
🔗 ou via OpenAPI: `http://localhost:8080/v3/api-docs`

---

## 🧪 Testes

- Testes unitários com **JUnit 5** e **Mockito**
- Foco nas regras de negócio com cobertura superior a 70%
- Camada de serviço amplamente testada

---

## 📁 Como Rodar Localmente

### Pré-requisitos

- Java 17+
- Maven
- PostgreSQL

### Passos

1. Clone o repositório:
```bash
git clone https://github.com/EzequielSR/GerenciadorPortfolios.git
cd GerenciadorPortfolios
```

2. Configure o banco de dados: Crie um banco PostgreSQL com as credenciais:
```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/gerenciador_portfolios
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

3. Rode o projeto:
```bash
./mvnw spring-boot:run
```

4. Acesse a API:
* Swagger:  http://localhost:8080/swagger-ui.html
* Endpoints: http://localhost:8080/api/projetos

# Farmácia API

## Sistema de Gestão Backend em Java

## Visão Geral

A **Farmácia API** é uma API REST desenvolvida em **Java 17** com **Spring Boot**,
projetada para gerenciar de forma completa e segura as operações de uma farmácia.
 O sistema contempla cadastro de medicamentos, categorias, clientes, controle de estoque, registro
de vendas e geração de alertas, aplicando regras de negócio reais e boas práticas de engenharia de software.

O projeto foi inicialmente concebido como resposta a um desafio técnico,
porém evoluiu para um backend de padrão corporativo, indo além dos requisitos mínimos esperados e 
refletindo decisões arquiteturais voltadas à manutenção, escalabilidade e clareza do código.

---

## Funcionalidades Principais

* Gestão completa de medicamentos e categorias
* Cadastro e validação de clientes (CPF, idade mínima, e-mail)
* Controle de estoque com histórico de movimentações
* Registro de vendas com validações de negócio
* Alertas automáticos de estoque baixo e validade próxima
* Autenticação e autorização via JWT
* Documentação automática com Swagger/OpenAPI

---

## Arquitetura e Organização do Projeto

A aplicação adota uma **arquitetura orientada a domínio**, com separação clara de responsabilidades e isolamento
entre camadas de negócio, infraestrutura e apresentação. O objetivo é facilitar a leitura, o entendimento do fluxo
da aplicação e a evolução futura do código.

A estrutura reflete fielmente o código existente, sem camadas artificiais ou abstrações desnecessárias.

---

## Estrutura de Diretórios

```text
src/main/java/com/farmacia/api
│
├── auth
│   ├── web        # Endpoints de autenticação
│   ├── service    # Regras de autenticação e geração de JWT
│   └── model      # Modelo de usuário e perfis
│
├── cliente
│   ├── web        # Controllers REST de clientes
│   ├── service    # Regras de negócio do cliente
│   ├── dto        # Contratos de entrada e saída
│   └── repository # Persistência de clientes
│
├── medicamento
│   ├── web        # Endpoints de medicamentos
│   ├── service    # Regras de negócio (status, validade, preço)
│   ├── dto        # Contratos da API
│   └── repository # Persistência de medicamentos
│
├── estoque
│   ├── web        # Entrada e saída de estoque
│   ├── service    # Regras de movimentação
│   └── repository # Persistência de movimentações
│
├── venda
│   ├── web        # Registro e consulta de vendas
│   ├── service    # Regras de venda e cálculo de valores
│   └── repository # Persistência de vendas
│
├── alerta
│   ├── web        # Endpoints de consulta de alertas
│   └── service    # Regras de alerta (estoque e validade)
│
├── mapper         # Conversão entre entidades e DTOs
├── model          # Entidades JPA compartilhadas
│
├── infra
│   ├── security   # Configuração do Spring Security e JWT
│   ├── swagger    # Configuração do Swagger/OpenAPI
│   ├── handler    # Tratamento global de exceções
│   └── bootstrap  # Carga inicial de dados
│
└── exception      # Exceções de negócio da aplicação
```

---

## Diretrizes Arquiteturais

* Organização por domínio funcional
* Controllers responsáveis apenas pela orquestração
* Regras de negócio centralizadas na camada de serviço
* Infraestrutura desacoplada do domínio
* Código orientado à legibilidade e manutenibilidade

---

## Segurança e Autenticação

### Implementação

* Autenticação baseada em JWT
* Spring Security configurado com filtro dedicado
* Perfis de usuário para controle de acesso
* Senhas armazenadas com criptografia BCrypt

### Justificativa

Embora não fosse um requisito explícito do desafio, a implementação de autenticação foi incluída para simular um cenário
real de produção, proteger endpoints sensíveis e demonstrar domínio de segurança em aplicações Spring.

---

## Tratamento de Erros

A aplicação adota um modelo consistente de tratamento de exceções, com foco em previsibilidade e clareza para o 
consumidor da API.

### Exceções de Domínio

* ClienteMenorDeIdadeException
* MedicamentoVencidoException
* MedicamentoInativoException
* EstoqueInsuficienteException

### Padrão Adotado

* Exceção base: `BusinessException`
* Handler global: `GlobalExceptionHandler`
* Resposta de erro padronizada: `ErrorResponse`

Esse modelo evita respostas genéricas, melhora a experiência de integração e facilita o tratamento de erros no frontend.

---

## Regras de Negócio Implementadas

* CPF válido e único por cliente
* Cliente deve ser maior de 18 anos para realizar compras
* Medicamentos não podem estar vencidos ou inativos
* Venda bloqueada em caso de estoque insuficiente
* Estoque nunca assume valores negativos

Essas regras elevam o projeto de um CRUD básico para um sistema com lógica de negócio real.

---

## Persistência de Dados

* Banco de dados: PostgreSQL
* JPA / Hibernate
* Repositórios por agregado
* `ddl-auto=update` configurado para ambiente de desenvolvimento

Preparado para produção com possibilidade de inclusão de:

* Flyway ou Liquibase
* Índices explícitos
* Ajustes de performance (fetch strategies)

---

## Docker e Ambientes

A aplicação é totalmente dockerizada, permitindo execução previsível em qualquer ambiente.

### Benefícios do uso de Docker

* Execução simples para avaliadores e usuários
* Eliminação de dependências locais (Java, Maven, PostgreSQL)
* Consistência entre ambientes

As variáveis sensíveis são gerenciadas via `.env`, com perfis separados para execução local e containerizada.

---

## Documentação da API

A documentação é gerada automaticamente via **Swagger/OpenAPI**.

**Acesso:**

```
http://localhost:8080/swagger-ui.html
```

Inclui:

* Descrição completa dos endpoints
* Contratos de request e response
* Suporte à autenticação JWT

---

## Diagrama C4 — Nível 1 (Contexto)

```text
┌────────────────────┐
│ Usuário / Frontend │
│ (Web ou API Client)│
└─────────┬──────────┘
          │ HTTPS (JSON)
          ▼
┌──────────────────────────────┐
│        Farmácia API          │
│ Backend de regras de negócio │
└─────────┬────────────────────┘
          │ JPA / JDBC
          ▼
┌────────────────────┐
│    PostgreSQL      │
│ Banco de Dados     │
└────────────────────┘
```
## Observações Arquiteturais

* A API é stateless, utilizando JWT para autenticação

* Não há dependência direta entre frontend e banco de dados

* A separação permite troca do frontend ou do banco sem impacto direto no domínio
---

## Execução do Projeto

### Execução Local (sem Docker)

**Pré-requisitos:**

* Java 17 ou superior
* Maven ou Maven Wrapper
* PostgreSQL em execução ( ou qualquer outro DB )

**Passos:**

1. Criar o banco de dados
2. Configurar variáveis de ambiente ou `application-dev.properties`
3. Executar:

```bash
./mvnw clean spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

---

### Execução com Docker

**Pré-requisitos:**

* Docker
* Docker Compose

**Comando:**

```bash
docker-compose up --build
```

Nesta configuração:

* Banco provisionado automaticamente
* Variáveis carregadas via `.env`
* Nenhuma dependência local necessária

---

## Fluxo de Autenticação

**Login:**

```http
POST /auth/login
```

**Request:**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

O token JWT retornado deve ser enviado no header das requisições protegidas:

```http
Authorization: Bearer <TOKEN>
```

**Observação:** o usuário inicial é criado automaticamente via seed na inicialização da aplicação.

---

## Exemplos de Endpoints

### Medicamentos

* `POST /medicamentos`
* `PUT /medicamentos/{id}`
* `PATCH /medicamentos/{id}/status`

### Vendas

* `POST /vendas`
* `GET /vendas/cliente/{clienteId}`

### Alertas

* `GET /alertas/estoque-baixo`
* `GET /alertas/validade-proxima`

---

## Diferenciais Técnicos

* Arquitetura orientada a domínio
* Autenticação JWT com Spring Security
* Regras de negócio realistas
* Tratamento de erros robusto
* Dockerização completa
* Documentação Swagger integrada

---

## Autor

**Renan Justino**

Projeto: Desafio Back-End JAVA
Java | Spring Boot | APIs RESTful

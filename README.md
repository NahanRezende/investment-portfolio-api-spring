# CSI607 - AP03 | API de Gerenciamento de Carteira de Investimentos

Documentação da API desenvolvida para a atividade prática 03 da disciplina **Sistemas WEB II (CSI607)**.

Esta versão descreve somente o que está implementado no código atual da API (sem frontend).

## 1. Descrição da API

API RESTful para gerenciamento de carteira de investimentos. Permite:

- cadastrar ativos
- listar ativos
- filtrar por tipo
- atualizar por ID
- remover por ID
- obter resumo da carteira

Domínio coberto:

- ações
- criptomoedas
- fundos
- renda fixa
- outros

## 2. Tecnologias Utilizadas

- Java 17
- Spring Boot 3.1.5
- Spring Web
- Spring Data JPA
- Spring Validation
- PostgreSQL
- H2 (testes)
- Lombok
- ModelMapper
- SpringDoc OpenAPI / Swagger UI
- Maven
- Docker / Docker Compose

## 3. Como Executar o Projeto

### 3.1 Pré-requisitos

- Java 17+
- PostgreSQL (execução local padrão)
- Maven (ou `mvnw` / `mvnw.cmd`)
- Docker e Docker Compose (opcional)

### 3.2 Rodar com Maven

Configuração padrão em `src/main/resources/application.properties`:

- PostgreSQL em `localhost:5433`
- banco `investment_db`
- usuário `investment_user`
- senha `investment_pass`

Executar:

```powershell
.\mvnw.cmd spring-boot:run
```

API:

- `http://localhost:3000`

Swagger UI:

- `http://localhost:3000/swagger-ui.html`

### 3.3 Rodar com Docker (se aplicável)

Build da imagem da API:

```powershell
docker build -f dockerInvest/Dockerfile -t carteira-investimentos-api .
```

Subir com Docker Compose (API + banco):

```powershell
cd dockerInvest
docker compose up -d postgres app
```

Observação:

- O `docker-compose.yml` também referencia `pgadmin`, que não é obrigatório para a entrega da API.

## 4. Endpoints Disponíveis (6 endpoints reais)

Base URL: `http://localhost:3000`

### 4.1 POST `/investments`

Cadastro de novo ativo na carteira.

Exemplo de request:

```json
{
  "type": "ACAO",
  "symbol": "BBAS3",
  "quantity": 100,
  "purchasePrice": 19.68,
  "purchaseDate": "2025-07-31"
}
```

Exemplo de response (`201 Created`):

```json
{
  "id": 1,
  "type": "ACAO",
  "symbol": "BBAS3",
  "quantity": 100,
  "purchasePrice": 19.68,
  "purchaseDate": "2025-07-31"
}
```

### 4.2 GET `/investments`

Lista todos os ativos cadastrados.

Exemplo de response (`200 OK`):

```json
[
  {
    "id": 1,
    "type": "ACAO",
    "symbol": "BBAS3",
    "quantity": 100,
    "purchasePrice": 19.68,
    "purchaseDate": "2025-07-31"
  }
]
```

### 4.3 GET `/investments/{id}`

Busca um ativo por ID.

Exemplo de response (`200 OK`):

```json
{
  "id": 1,
  "type": "ACAO",
  "symbol": "BBAS3",
  "quantity": 100,
  "purchasePrice": 19.68,
  "purchaseDate": "2025-07-31"
}
```

### 4.4 PUT `/investments/{id}`

Atualiza um ativo existente.

Exemplo de request:

```json
{
  "type": "ACAO",
  "symbol": "BBAS3",
  "quantity": 120,
  "purchasePrice": 20.15,
  "purchaseDate": "2025-07-31"
}
```

Exemplo de response (`200 OK`):

```json
{
  "id": 1,
  "type": "ACAO",
  "symbol": "BBAS3",
  "quantity": 120,
  "purchasePrice": 20.15,
  "purchaseDate": "2025-07-31"
}
```

### 4.5 DELETE `/investments/{id}`

Remove um ativo da carteira.

Response:

- `204 No Content`

### 4.6 GET `/investments/summary`

Retorna resumo da carteira.

Exemplo de response (`200 OK`) exatamente no formato do enunciado:

```json
{
  "totalInvested": 15000.00,
  "totalByType": {
    "ACAO": 8000.00,
    "CRIPTO": 1000.00,
    "FUNDO": 6000.00
  },
  "assetCount": 5
}
```

## 5. Filtros Disponíveis

### Filtro por tipo

Endpoint:

- `GET /investments?type=CRIPTO`

Tipos aceitos (enum `AssetType` atual):

- `ACAO`
- `CRIPTO`
- `FUNDO`
- `RENDA_FIXA`
- `OUTRO`

Valor inválido em `type` retorna `400 Bad Request` em formato `ProblemDetail`.

## 6. Testes

### Como rodar

```powershell
.\mvnw.cmd test
```

### Profile `test` com H2

Os testes usam `@ActiveProfiles("test")` com banco H2 em memória (`src/test/resources/application-test.properties`), compatível com PostgreSQL (`MODE=PostgreSQL`).

## 7. Estrutura do Projeto (visão em camadas)

```text
src/main/java/com/investments/portfolio
|-- config/        Configurações (CORS, Swagger, ModelMapper, DataInitializer)
|-- controller/    Endpoints REST
|-- exception/     Tratamento global de exceções (ProblemDetail)
|-- model/
|   |-- dto/       DTOs de entrada e saída
|   |-- entity/    Entidade JPA
|   `-- enums/     Enum AssetType
|-- repository/    Camada de acesso a dados
|-- service/       Contratos
`-- service/impl/  Implementações
```

## 8. Boas Práticas Aplicadas

- Tratamento global de exceções com `@RestControllerAdvice`
- Uso de `ProblemDetail` para respostas de erro
- Uso de DTOs para separar contrato HTTP da entidade
- Testes de integração da camada web com `MockMvc`

## 9. Autor

- Nome: `Nahan Rezende`
- Disciplina: CSI607 - Sistemas WEB II
- Período: 2025/2

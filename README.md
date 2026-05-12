# Upload API

API REST desenvolvida em **Java com Spring Boot** para autenticação de usuários, gerenciamento de prints e upload de arquivos para a **AWS S3**.
O projeto segue **arquitetura em camadas**, com persistência de dados em **MySQL** e proteção de rotas com **JWT**.

---

## 📌 Visão Geral

A Upload API foi criada para servir como backend de uma aplicação de compartilhamento e gerenciamento de prints de jogos, permitindo:

* cadastro e autenticação de usuários
* upload de imagens para a AWS S3
* persistência dos metadados no banco de dados
* gerenciamento de prints associados a cada usuário
* consumo por frontend externo, como o projeto **Site-Prints-Jogos**

---

## 🧱 Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3.5.6**
* **Spring Web**
* **Spring Data JPA**
* **Spring Security**
* **JWT**
* **MySQL**
* **AWS SDK v2 (S3)**
* **Maven**
* **Lombok**
* **Docker**
* **Postman**

---

## 🏛️ Arquitetura do Projeto

O projeto foi organizado em camadas para facilitar manutenção, evolução e separação de responsabilidades.

### Estrutura principal

* `controller` — endpoints REST da aplicação
* `service` — regras de negócio
* `dto` — objetos de transferência de dados
* `security` — autenticação, autorização e filtro JWT
* `infrastructure/entities` — entidades persistidas no banco
* `infrastructure/repository` — repositórios JPA
* `s3` — configuração do `S3Client`

---

## 🚀 Funcionalidades

* Cadastro de usuários
* Login com autenticação via JWT
* Validação de acesso a rotas protegidas
* Upload de arquivos para bucket AWS S3
* Cadastro e gerenciamento de prints vinculados a usuários
* Busca de prints por ID e por usuário
* Atualização de descrição e dados de prints
* Remoção de prints
* Persistência de usuários e prints com MySQL

---

## 🔐 Autenticação

A API utiliza **JWT (JSON Web Token)** para proteger rotas privadas.

Após realizar o login, envie o token no header das requisições protegidas:

```http
Authorization: Bearer SEU_TOKEN
```

---

## 🔗 Endpoints

### Auth

| Método | Endpoint            | Descrição                           |
| ------ | ------------------- | ----------------------------------- |
| POST   | `/auth/signup`      | Cadastra um novo usuário            |
| POST   | `/auth/login`       | Realiza login e retorna o token JWT |
| GET    | `/auth/teste-token` | Valida o token autenticado          |

### Upload

| Método | Endpoint      | Descrição                                     |
| ------ | ------------- | --------------------------------------------- |
| POST   | `/api/upload` | Realiza upload de um arquivo para o bucket S3 |

#### Parâmetros (`form-data`)

| Nome   | Tipo | Descrição                         |
| ------ | ---- | --------------------------------- |
| file   | file | Arquivo a ser enviado             |
| userId | text | ID do usuário associado ao upload |

#### Resposta

* **Sucesso:** URL do arquivo enviado
* **Erro:** mensagem de falha no upload

> **Observação:** o acesso ao arquivo enviado depende da política de permissões configurada no bucket S3.

### Prints

| Método | Endpoint                   | Descrição                                      |
| ------ | -------------------------- | ---------------------------------------------- |
| POST   | `/prints/upload`           | Faz upload do print para o S3 e salva no banco |
| GET    | `/prints`                  | Lista todos os prints cadastrados              |
| GET    | `/prints/{id}`             | Busca um print pelo ID                         |
| GET    | `/prints/user/{userId}`    | Lista prints de um usuário específico          |
| PUT    | `/prints/{id}`             | Atualiza os dados completos de um print        |
| PATCH  | `/prints/{id}/description` | Atualiza apenas a descrição do print           |
| DELETE | `/prints/{id}`             | Remove um print pelo ID                        |
| DELETE | `/prints/all`              | Remove todos os prints cadastrados             |

#### Parâmetros do endpoint `POST /prints/upload` (`form-data`)

| Nome        | Tipo | Descrição          |
| ----------- | ---- | ------------------ |
| file        | file | Arquivo da imagem  |
| userId      | text | ID do usuário      |
| game        | text | Nome do jogo       |
| description | text | Descrição do print |

### Usuários

| Método | Endpoint     | Descrição                    |
| ------ | ------------ | ---------------------------- |
| POST   | `/user`      | Cria um usuário              |
| GET    | `/user`      | Lista todos os usuários      |
| GET    | `/user/{id}` | Busca usuário por ID         |
| PATCH  | `/user/{id}` | Atualiza dados de um usuário |
| DELETE | `/user/{id}` | Remove um usuário            |

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

* Java 21
* Maven
* MySQL
* Docker *(opcional)*
* Credenciais AWS válidas

---

## ▶️ Executando Sem Docker

### 1. Clone o repositório

```bash
git clone https://github.com/Daniel-Macedo-dev/upload-api.git
```

### 2. Acesse o diretório do projeto

```bash
cd upload-api
```

> Se o projeto estiver dentro de uma subpasta com o mesmo nome, entre nela antes de executar os comandos.

### 3. Execute a aplicação

```bash
./mvnw spring-boot:run
```

### 4. Configure as credenciais AWS e o banco de dados

Você pode configurar via `application.properties` ou variáveis de ambiente.

Exemplo de variáveis AWS:

```bash
AWS_ACCESS_KEY_ID=...
AWS_SECRET_ACCESS_KEY=...
AWS_REGION=sa-east-1
```

Exemplo de configurações esperadas para banco de dados:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/seu_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

---

## 🐳 Executando com Docker

### Build da imagem

> Execute os comandos dentro da pasta onde está o `Dockerfile`.

```bash
docker build -t upload-api .
```

### Rodando o container

```bash
docker run -d -p 8080:8080 --name upload-api \
-e AWS_ACCESS_KEY_ID="SUA_CHAVE" \
-e AWS_SECRET_ACCESS_KEY="SUA_CHAVE_SECRETA" \
-e AWS_REGION="sa-east-1" \
upload-api
```

### Observações

* `-p 8080:8080` mapeia a porta do container para sua máquina
* `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY` definem as credenciais AWS usadas pela aplicação
* `AWS_REGION` define a região utilizada pela AWS

### Parar e remover o container

```bash
docker stop upload-api
docker rm upload-api
```

---

## 🧪 Testando com Postman

### Fluxo recomendado

1. Faça o cadastro em `/auth/signup`
2. Faça login em `/auth/login`
3. Copie o token JWT retornado
4. Nas rotas protegidas, envie o header:

```http
Authorization: Bearer SEU_TOKEN
```

5. Teste os endpoints de upload e gerenciamento de prints

---

## 📂 Exemplo de Fluxo de Uso

### 1. Cadastro de usuário

**POST** `/auth/signup`

```json
{
  "name": "Daniel",
  "email": "daniel@email.com",
  "password": "123456"
}
```

### 2. Login

**POST** `/auth/login`

```json
{
  "email": "daniel@email.com",
  "password": "123456"
}
```

### 3. Upload de print

Use `form-data` no Postman com os campos:

* `file`
* `userId`
* `game`
* `description`

---

## 🎯 Objetivos do Projeto

Este projeto foi desenvolvido com foco em:

* prática de backend com Java e Spring Boot
* integração com serviços da AWS
* autenticação e segurança com JWT
* persistência com JPA e MySQL
* organização em arquitetura em camadas
* suporte a integração com frontend

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License**.

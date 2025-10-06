# API de Upload de Arquivos

Este projeto é uma API RESTful desenvolvida em Java com Spring Boot para gerenciar o upload de arquivos para um bucket AWS S3. A estrutura segue boas práticas de POO, arquitetura em camadas e integração com AWS.

## 🧱 Tecnologias Utilizadas

- Java 21 (LTS)
- Spring Boot 3.5.6
- AWS SDK v2 (S3)
- Maven
- Lombok
- Docker
- Postman (para testes)

## 📁 Estrutura do Projeto

- `service`: regras de negócio para upload e download de arquivos  
- `controller`: endpoints REST  
- `s3`: configuração do `S3Client`  
- `resources/uploads`: armazenamento temporário de arquivos (opcional)  

## 🚀 Funcionalidades

- Upload de arquivos para o bucket S3
- Retorno da URL pública do arquivo
- Validação de tipo e tamanho de arquivo (implementável)
- Possibilidade de listar e excluir arquivos do S3 (opcional)
- Camada de serviço desacoplada com interfaces
- Cada usuário pode utilizar suas próprias credenciais AWS

## 🔗 Endpoints

### POST `/api/upload`

Envia um arquivo para o bucket `prints-jogos`.

**Parâmetros (form-data) no Postman:**

| Nome | Tipo | Descrição |
|------|------|-----------|
| file | file | Arquivo a ser enviado |

**Resposta:**

- Sucesso: URL pública do arquivo no S3  
- Erro: mensagem de falha no upload

## 🐳 Docker

### Build da imagem

```bash
docker build -t upload-api .
```

Rodar o container

Cada usuário deve fornecer suas próprias credenciais AWS:
```bash
docker run -d -p 8080:8080 --name upload-api \
-e AWS_ACCESS_KEY_ID="SUA_CHAVE" \
-e AWS_SECRET_ACCESS_KEY="SUA_CHAVE_SECRETA" \
upload-api
```

-p 8080:8080 mapeia a porta do container para sua máquina

As variáveis AWS_ACCESS_KEY_ID e AWS_SECRET_ACCESS_KEY permitem que cada usuário use suas próprias credenciais

Testando a API via Postman

1. Abra o Postman

2. Crie uma requisição POST para http://localhost:8080/api/upload

3. Em Body, selecione form-data

4. Adicione um campo file e escolha o arquivo a ser enviado

5. Envie a requisição

6. A resposta será a URL pública do arquivo no S3

Parar e remover o container
```bash
docker stop upload-api
docker rm upload-api
```

🔧 Como Executar Sem Docker

1. Clone o projeto:
```bash
git clone https://github.com/Daniel-Macedo-dev/upload-api.git
```
2. Acesse o diretório:
```bash   
cd upload-api
```
3. Compile e execute:
```bash
./mvnw spring-boot:run
```
4. Configure suas credenciais AWS no application.properties ou via variáveis de ambiente:
```bash
AWS_ACCESS_KEY_ID=...
AWS_SECRET_ACCESS_KEY=...
AWS_REGION=sa-east-1
```

📄 Licença

Este projeto está licenciado sob a MIT License.

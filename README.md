# API de Upload de Arquivos

Este projeto é uma API RESTful desenvolvida em Java com Spring Boot para gerenciar o upload de arquivos. A estrutura foi organizada seguindo boas práticas de desenvolvimento orientado a objetos, utilizando arquitetura em camadas e integração com o AWS S3.

## 🧱 Tecnologias Utilizadas

- Java 25 (LTS)
- Spring Boot 3.5.6
- AWS SDK v2 (S3)
- Maven
- Lombok
- Postman (para testes de requisição)

## 📁 Estrutura do Projeto

- `service`: regras de negócio para upload e download de arquivos
- `controller`: endpoints REST  
- `s3`: configuração do S3Client  
- `resources/uploads`: armazenamento temporário de arquivos (opcional)  

## 🚀 Funcionalidades

- Upload de arquivos para o bucket S3
- Retorno da URL pública do arquivo em JSON
- Validação de tipo e tamanho de arquivo
- Listagem de arquivos disponíveis (opcional)
- Exclusão de arquivos do S3 (opcional)
- Camada de serviço desacoplada com interfaces


## 🔧 Como Executar

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

4. Teste os endpoints usando o Postman ou outro cliente HTTP:
```bash
* `POST /api/upload` → enviar arquivo
* `GET /api/files` → listar arquivos (opcional)
* `DELETE /api/files/{filename}` → deletar arquivo (opcional)
```

## 📄 Licença

Este projeto está licenciado sob a MIT License.

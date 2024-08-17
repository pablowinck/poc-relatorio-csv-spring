# POC de Geração de Relatórios em CSV com Spring Boot

Esta POC demonstra a geração de relatórios em CSV utilizando Spring Boot, realizando stream dos dados do banco de dados para um bucket no Amazon S3.

## Requisitos

- Java 17
- Maven
- Node.js
- Variáveis de ambiente da AWS configuradas no sistema operacional

## Estrutura do Projeto

Dentro do diretório `src/main/resources`, há um script chamado `gerador.js`, que gera os arquivos `schema.sql` e `data.sql`. Esses arquivos são automaticamente importados ao iniciar o projeto.

## Como Executar

1. Certifique-se de que as variáveis de ambiente da AWS estejam configuradas corretamente no seu sistema.
2. Clone o repositório e navegue até o diretório do projeto.
3. Execute o comando abaixo para compilar o projeto e gerar os arquivos SQL:

   ```bash
   mvn compile
   ```

Isso irá compilar o código Java e, automaticamente, executar o script `gerador.js`, que gerará os arquivos `schema.sql` e `data.sql`.

4. Para rodar a aplicação, utilize o comando:

   ```bash
   mvn spring-boot:run
   ```

Como o projeto implementa `CommandLineRunner`, o relatório será gerado automaticamente ao iniciar a aplicação.

## Banco de Dados

Este projeto utiliza o banco de dados em memória H2, portanto, não é necessário configurar um banco de dados externo.

## Dependências

- **Java 17**: Linguagem utilizada para desenvolvimento.
- **Maven**: Ferramenta de build e gerenciamento de dependências.
- **Node.js**: Necessário para executar o script `gerador.js`.
- **Spring Boot**: Framework utilizado para simplificar o desenvolvimento em Java.
- **AWS SDK**: Biblioteca para interação com serviços da AWS.
- **H2 Database**: Banco de dados em memória para desenvolvimento e testes.

## Execução do Script de Geração

O script `gerador.js`, localizado na pasta `src/main/resources`, é executado automaticamente durante a fase de compilação (`mvn compile`). Ele gera os arquivos `schema.sql` e `data.sql`, que são utilizados para configurar o esquema e os dados do banco de dados H2 ao iniciar o projeto.

## Contato

E-mail: contato@pablowinter.com.br
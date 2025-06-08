# SafeSpace-API

Diante do desafio de apoiar comunidades vulneráveis em situações de crise, foi desenvolvida uma solução digital focada em gestão de atendimentos sociais e psicológicos prestados por ONGs e voluntários. A plataforma centraliza pedidos de ajuda realizados por usuários, permite o gerenciamento de profissionais voluntários e psicólogos, e facilita o controle dos atendimentos realizados, oferecendo dados em tempo real e histórico detalhado.

## Destaques da solução
- ✅ Centralizar os pedidos de apoio feitos por usuários em situação de vulnerabilidade;

- ✅ Gerenciar os atendimentos realizados por psicólogos e voluntários de forma estruturada;

- ✅ Fornecer relatórios sobre volume de atendimentos por tipo e por região;

- ✅ Permitir que ONGs associem voluntários e acompanhem sua atuação;

## Funcionalidades
Cadastro de usuarios, voluntarios, psicologos, filiados e ongs: Permite o registro no sistema de informações.

Atualização de Informações: Possibilidade de editar ou excluir informações.

Visualização de Informações: Exibe os detalhes dos pedidos e atendimentos desejados, garantindo que suas necessidades sejam atendidas e seus atendimentos psiquiatricos documentados.

API REST em Java

Banco de Dados H2

Autenticação com JWT

Deploy no render

## Requisitos
Java JDK 17+

Maven 3.8+

Git

Docker

insomnia ou postman(para testes manuais)

## Execução do projeto (Visual Studio Code ou IntelliJ)
1. Clone o repositório da api:
   
git clone https://github.com/felipe-2833/mottuGestor.git

2. Entre na pasta do projeto

cd SafeSpace-API

3. Execute a aplicação:

- Caso esteja usando IntelliJ, rode a classe principal (App.java);

- No VS Code, abra a pasta e clique em “Run Java”.

### Não esqueça de conferir os caminhos para rodar os comandos e inicializar a aplicação da forma correta!

## Acesso ao Swagger

Acesse a documentação da API via Swagger:

- Localhost: http://localhost:8080/swagger-ui/index.html

- Deploy Render: https://safespace-api-xa5p.onrender.com/swagger-ui/index.html

### A documentação do Swagger pode apresentar falhas de execução devido à proteção com JWT. Utilize Insominia ou postman para testes

## 1. Autenticar-se (POST /auth/login)
Exemplo de payload:

{
	"email": "admin3@safespace.com",
	"password": "12345"
}

1. Acesse o **endpoint POST /auth/login**.

2. Copie o token JWT retornado.

3. Use esse token nos próximos endpoints (clique em Authorize no Swagger e selecione Bearer Token).

4. Faça login e copie o token.

5. Autorize via Bearer Token em todas as requisições.

## 2. Endpoints de Pedido: POST /pedidos
Exemplo de corpo:

{
  "descricao": "Solicitação de cesta básica urgente",
  "status": "ATIVO",
  "pedidoType": "ALIMENTACAO",
  "user": {
    "id_user": 1
  },
  "dataSolicitacao": "2025-03-04"
}

1. Faça login e copie o token.

2. Autorize via Bearer Token.

3. Vá até o endpoint POST /pedidos.

4. Substitua o corpo pelo exemplo acima.

5. Clique em "send".

## 3. Listar todos os pedidos (GET)
**Endpoint**: GET /pedidos

Parâmetros de filtro de pesquisa:

| Parâmetro     | Tipo       | Descrição                  |
| ------------- | ---------- | -------------------------- |
| `descricao`   | string     | Filtra por descrição       |
| `pedidoType`  | string     | Tipo do pedido (ex: SAUDE) |
| `status`      | string     | Status atual (ATIVO)       |
| `dataInicial` | yyyy-MM-dd | Data inicial do filtro     |
| `dataFinal`   | yyyy-MM-dd | Data final do filtro       |


Se não quiser usar filtros, deixe os campos vazios.

## 4. Buscar pedido por ID (GET)
**Endpoint**: GET /pedidos/{id}

Passos:

1. Clique em GET /pedidos/{id}

2. Preencha com o ID do pedido (ex: 1)

3. Clique em “send”

## 5. Atualizar pedido (PUT)
**Endpoint:** PUT /pedidos/{id}
Exemplo de corpo:

{
  "descricao": "Solicitação de apoio jurídico",
  "status": "ATIVO",
  "pedidoType": "OUTROS",
  "user": {
    "id_user": 1
  },
  "dataSolicitacao": "2025-03-10"
}

1. Clique em PUT /pedidos/{id}

2. Preencha o ID do pedido (ex: 1)

3. Substitua o JSON pelo exemplo acima

4. Clique em “send”

## 6. Apagar pedido (DELETE)
**Endpoint:** DELETE /pedidos/{id}

1. Clique em DELETE /pedidos/{id}

2. Preencha o ID do pedido que deseja excluir

3. Clique em “send”


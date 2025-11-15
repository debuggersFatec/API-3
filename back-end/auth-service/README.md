# Auth Service

Este módulo cuida da autenticação de usuários e reúne todas as informações que o frontend precisa logo após o login. Quando o usuário informa e-mail e senha, o serviço valida os dados, gera um token JWT e monta um payload completo com equipes, projetos, tarefas e notificações que estão salvos no MongoDB.

## Visão Geral da Arquitetura

- **Framework**: Spring Boot 3 com Java 21
- **Banco de dados**: MongoDB (mesma instância usada pelo serviço principal `api-3`)
- **Segurança**: Spring Security com JWT (biblioteca JJWT). O token é assinado com o segredo definido no arquivo `.env`.
- **Agregação de dados**: repositórios para `User`, `Teams`, `Projects`, `Task` e `Notification` seguem o mesmo esquema do serviço principal para devolver o mesmo payload que o frontend já conhece.

## Pré-requisitos

- Java 21 instalado e configurado no `PATH`
- Maven Wrapper incluído no projeto (`mvnw`, `mvnw.cmd`)
- Acesso ao cluster MongoDB (as credenciais ficam nas variáveis de ambiente)

## Variáveis de Ambiente

No diretório `back-end/auth-service` já existe um arquivo `.env`. Se precisar recriá-lo, use este conteúdo:

```dotenv
MONGO_URL="mongodb+srv://debuggersfatec_db_user:debuggersapi@cluster0.r5u6jeh.mongodb.net/api3db?retryWrites=true&w=majority&appName=Cluster0"
JWT_SECRET="85c5341c89a3a22c3fec6a16fe36981aa4938a325f05ded2c60805f69a3a2830"
REDIS_URL=redis://default:Iq4FLYFAzQF5sUATL0zuKEJP7USOEnHa@redis-15330.crce216.sa-east-1-2.ec2.redns.redis-cloud.com:15330
SENHA_APP_EMAIL=alzt mway xstg baiu
```

> Se estiver usando outro cluster/segredo, atualize os valores conforme o ambiente.

### Carregando o `.env` no PowerShell

Sempre que abrir um novo terminal PowerShell para subir o serviço, rode o script abaixo **antes** de executar o Maven:

```powershell
Get-Content ".env" | ForEach-Object {
  if ($_ -match '^\s*#' -or $_ -match '^\s*$') { return }
  if ($_ -match '^\s*([^=]+)\s*=\s*(.*)\s*$') {
   $name  = $matches[1].Trim()
   $value = ($matches[2] -replace '^"|"$','').Trim()
   [Environment]::SetEnvironmentVariable($name,$value,'Process')
  }
}
```

Esse comando lê cada linha do `.env`, ignora comentários e linhas vazias e cria variáveis de ambiente somente para a sessão atual do terminal.

## Como Rodar o Serviço (Passo a Passo)

1. Abra um terminal PowerShell.
2. Vá até a pasta do serviço:
  ```powershell
  cd C:\Users\mates\OneDrive\Desktop\API-3\back-end\auth-service
  ```
3. Carregue as variáveis do `.env` usando o script listado na seção anterior.
4. Inicie a aplicação com o Maven Wrapper:
  ```powershell
  .\mvnw spring-boot:run
  ```
5. Aguarde o log mostrar que a aplicação subiu. O serviço fica disponível em `http://localhost:8081`.
6. **Importante**: mantenha o serviço principal `api-3` rodando (porta 8080). Sem ele, o login não consegue buscar equipes, projetos, tarefas e notificações.

## Endpoints Principais

- `POST /auth/login` – valida o e-mail/senha e devolve um `AuthResponse` completo (token, dados do usuário, rotas, notificações).
- `POST /auth/register` – cria um novo usuário e devolve o token inicial com a estrutura esperada pelo frontend.
- `POST /auth/update-password` – altera a senha de um usuário existente (é preciso enviar um token válido).

## Como Validar o Fluxo de Login

1. Suba os dois serviços: `auth-service` (8081) e `api-3` (8080).
2. No frontend (porta 5173), faça login com um usuário válido.
3. A resposta do login deve conter:
  - Token JWT
  - Dados básicos do usuário (uuid, nome, e-mail, imagem)
  - Equipes (`teams`), projetos e tarefas vinculados
  - Notificações recentes e a contagem de não lidas
4. Use esse token para acessar os endpoints protegidos do `api-3` (enviar no header `Authorization: Bearer <token>`).

## Dúvidas Comuns (Troubleshooting)

- **403 ao acessar equipes/projetos/tarefas**: verifique se o token enviado é o mesmo do login e se o `JWT_SECRET` está idêntico no `auth-service` e no `api-3`.
- **Login sem equipes/projetos/tarefas**: confirme que o serviço `api-3` está ativo e que o usuário realmente possui registros no MongoDB.
- **Variáveis não carregadas**: abra um novo terminal, rode o script do `.env` e só depois execute `spring-boot:run`.
- **Porta 8081 ocupada**: feche instâncias anteriores ou altere a porta em `application.properties` (`server.port`).

Seguindo estes passos, qualquer pessoa consegue subir o `auth-service` localmente e consumir o fluxo de autenticação completo sem precisar ler código.

🩺 Sistema Clínico Principal:
👉 http://localhost:8080/api/ (ou http://localhost:8080/api/index.html)

🌿 Apresentação Comercial & Portfólio:
👉 http://localhost:8080/api/portfolio.html

📖 Documentação Swagger (OpenAPI):
👉 http://localhost:8080/api/swagger-ui.html

# 🚀 Guia de Comandos - ZooMed Fauna & Domésticos

Guia prático de comandos para o **Maven Daemon (`mvnd`)**, **Docker** e **Docker Compose** utilizados no desenvolvimento e execução do sistema.

---

## ⚡ Fluxo Rápido: Alterou o código e quer atualizar no Docker?

Sempre que você alterar o código da aplicação e quiser testar no Docker, siga este fluxo de 2 passos:

```powershell
# Passo 1: Limpa e gera o novo .jar do Spring Boot
mvnd clean package -DskipTests
docker compose build --no-cache app
docker compose up -d
# Passo 2: Reconstrói a imagem e atualiza o container
docker compose up --build -d
```

---

## ☕ 1. Comandos Essenciais do Maven Daemon (`mvnd`)

| Comando | Para que serve | Quando usar |
| :--- | :--- | :--- |
| `mvnd clean package -DskipTests` | **Gera o `.jar` rápido para o Docker** | Sempre antes de rodar `docker compose up --build -d`. |
| `mvnd clean package` | Build completo **com testes automatizados** | Antes de commitar no Git ou enviar para produção. |
| `mvnd spring-boot:run` | Inicia o Spring Boot localmente (sem Docker) | Desenvolvimento rápido mantendo apenas o MySQL no Docker. |
| `mvnd compile` | Valida se as classes Java compilam sem erros | Checar sintaxe e tipos sem gerar o `.jar`. |
| `mvnd test` | Executa a bateria de testes unitários do JUnit | Garantir que alterações não quebraram regras de negócio. |
| `mvnd clean` | Apaga a pasta `target/` e arquivos compilados | Quando quiser fazer uma limpeza completa do build. |

---

## 📦 2. Gerenciamento de Containers Docker

| Comando | Descrição |
| :--- | :--- |
| `docker ps` | Lista os containers em execução no momento. |
| `docker ps -a` | Lista todos os containers (incluindo os que estão desligados). |
| `docker stop <nome_ou_id>` | Para a execução de um container de forma graciosa. |
| `docker start <nome_ou_id>` | Inicia um container parado. |
| `docker restart <nome_ou_id>` | Reinicia um container. |
| `docker rm <nome_ou_id>` | Remove um container parado. |
| `docker rm -f <nome_ou_id>` | Força a remoção de um container (mesmo se estiver rodando). |
| `docker run -d -p 8080:8080 <imagem>` | Cria e inicia um novo container a partir de uma imagem. |

---

## 🖼️ 3. Imagens Docker

| Comando | Descrição |
| :--- | :--- |
| `docker images` | Lista todas as imagens baixadas ou construídas no computador. |
| `docker build -t meu-app .` | Constrói uma nova imagem a partir do `Dockerfile`. |
| `docker pull <imagem>:<tag>` | Baixa uma imagem do Docker Hub (ex: `docker pull mysql:8.0`). |
| `docker rmi <imagem>` | Remove uma imagem do computador. |

---

## 🔍 4. Diagnóstico, Logs e Inspeção

| Comando | Descrição |
| :--- | :--- |
| `docker logs -f <nome>` | Acompanha os logs em tempo real (como o console da IDE). |
| `docker exec -it <nome> sh` *(ou `bash`)* | Abre um terminal interativo dentro do container em execução. |
| `docker stats` | Mostra uso de CPU, memória e rede dos containers em tempo real. |
| `docker inspect <nome>` | Exibe todas as informações técnicas detalhadas do container. |

### Exemplos Práticos no Projeto ZooMed:
```powershell
# Ver logs do Spring Boot em tempo real:
docker logs -f zoomed-app

# Acessar o terminal do banco de dados MySQL:
docker exec -it zoomed-mysql mysql -u root -proot

# Acessar o terminal interno do container da aplicação:
docker exec -it zoomed-app sh
```

---

## 🐙 5. Docker Compose (Orquestração de Múltiplos Serviços)

Comandos para gerenciar o conjunto de serviços definidos no [docker-compose.yml](file:///c:/Users/laura.sousa/Documents/clinica-veterinaria/docker-compose.yml):

| Comando | Descrição |
| :--- | :--- |
| `docker compose up -d` | Sobe todos os serviços em segundo plano (*detached mode*). |
| `docker compose up --build -d` | **Reconstrói as imagens e atualiza os containers** com novo código. |
| `docker compose ps` | Lista o status e a saúde dos containers do projeto. |
| `docker compose logs -f <serviço>` | Acompanha logs de um serviço específico (ex: `docker compose logs -f app`). |
| `docker compose stop` | Pausa os serviços sem removê-los. |
| `docker compose down` | Para e remove os containers e as redes criadas. |
| `docker compose down -v` | Para, remove os containers **e apaga os volumes do banco** (reset total). |
| `docker compose pull` | Baixa as versões mais recentes de todas as imagens externas do arquivo. |

---

## 🧹 6. Limpeza e Manutenção de Espaço em Disco

| Comando | Descrição |
| :--- | :--- |
| `docker system prune` | Remove containers parados, redes não usadas e imagens suspensas (*dangling*). |
| `docker system prune -a` | Limpeza profunda: remove **todas** as imagens não associadas a containers ativos. |
| `docker volume prune` | Remove volumes órfãos que não estão sendo utilizados por nenhum container. |

---

## 🌐 Links Úteis da Aplicação Rodando

* 🩺 **Interface Clínica:** [http://localhost:8080/api/](http://localhost:8080/api/) *(ou `index.html`)*
* 🌿 **Showcase & Portfólio Comercial:** [http://localhost:8080/api/portfolio.html](http://localhost:8080/api/portfolio.html)
* 📖 **Documentação Swagger / OpenAPI:** [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)

passos:

1 -  `mvnd clean package` | Build completo **com testes automatizados** | Antes de commitar no Git ou enviar para produção. |
2 -  `docker compose up --build -d` | **Reconstrói as imagens e atualiza os containers** com novo código. |
3 - testes rapidos: mvnd test, mvnd test -Dtest=AgendamentoServiceTest, 

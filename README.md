# tp-sistema-hospitalar-prog-modular
Trabalho Prático - Programação Modular

# Sistema Hospitalar

Trabalho prático da disciplina **Programação Modular** (POO em Java) — PUC Minas.

Sistema para gerenciar o dia a dia de um hospital: pacientes, profissionais de saúde, consultas, atendimentos de emergência, procedimentos cirúrgicos, internações e quartos.

## Tecnologias

- **Backend:** Java 21, Spring Boot 4.1.2 (Web MVC e Validation), Maven
- **Frontend:** HTML, CSS e JavaScript puro
- **Armazenamento:** em memória (os dados são perdidos ao reiniciar a aplicação)

## Estrutura do projeto

```
├── backend/     # API REST (Spring Boot)
│   └── src/main/java/br/pucminas/sistema_hospitalar/
│       ├── model/        # Entidades e enums
│       ├── repository/   # Repositórios em memória
│       ├── service/      # Regras de negócio
│       ├── controller/   # Endpoints REST
│       └── exception/    # Tratamento global de erros
├── frontend/    # Interface web (páginas, componentes e assets)
└── docs/        # Documentação (ex.: diagrama de classes)
```

## Como executar

### Pré-requisitos

- JDK 21
- Um navegador moderno
- (Opcional) extensão Live Server do VS Code para o frontend

### Backend

```bash
cd backend
./mvnw spring-boot:run        # Linux/macOS
mvnw.cmd spring-boot:run      # Windows
```

A API sobe em `http://localhost:8080`.

> Se aparecer "permission denied" no Linux/macOS, rode `chmod +x mvnw`.

### Frontend

Abra a pasta `frontend` no VS Code e inicie o `index.html` com o **Live Server**
(ou abra o arquivo diretamente no navegador). Mantenha o backend rodando.

### Testes

```bash
cd backend
./mvnw test
```

## Endpoints principais

| Recurso | Rota base |
|---|---|
| Pacientes | `/pacientes` |
| Profissionais | `/profissionais` |
| Consultas | `/consultas` |
| Emergências | `/consultas/emergencias` |
| Internações | `/internacoes` |
| Quartos | `/quartos` |

Os recursos seguem o padrão REST (`GET`, `POST`, `PUT`, `DELETE`), com ações adicionais como
`/consultas/{id}/confirmar`, `/iniciar`, `/concluir`, `/cancelar`, `/reagendar`
e `/internacoes/{id}/alta`.

## Modelo de domínio

- `Atendimento` (abstrata) → `Consulta` (abstrata) → `ConsultaRotina` / `ConsultaEspecializada`
- `Atendimento` → `AtendimentoEmergencia`
- `Atendimento` → `ProcedimentoCirurgico`
- `ProfissionalSaude` (abstrata) → `MedicoClinicoGeral`, `MedicoEspecialista`, `Cirurgiao`
- `Paciente`, `Quarto` e `Internacao`

## Equipe

- Luís Otávio
- Caio Giovanni
- Pedro Bastos
- Arthur Gabriel 

## Repositório

https://github.com/LuisOtavioLima/tp-sistema-hospitalar-prog-modular
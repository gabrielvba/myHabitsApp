# Habitos — Documento de Arquitetura

> Versão 1.0 | 2026-05-15
> Engenharia mobile com Kotlin Multiplatform (KMP)

---

## 1. Objetivo do Sistema

App de rastreamento de hábitos com foco em **consistência e streaks**. O usuário cria hábitos recorrentes, registra execuções diárias e acompanha sequências contínuas. O valor central é a visualização da constância ao longo do tempo — "você não quebra a corrente".

---

## 2. Escopo por Fase

### Fase 1 — MVP de Aprendizado (KMP + Local)

**Objetivo:** aprender Kotlin, KMP e mobile funcionando em Android e iOS.

**Inclui:**
- Criar, editar e arquivar hábitos
- Frequência: diário ou N vezes por semana
- Registrar execução do dia (check)
- Visualizar streak atual e histórico simples
- Persistência local (SQLDelight)
- Sem login, sem sync, sem conta

**Não inclui na Fase 1:**
- Notificações
- Temas / dark mode
- Sincronização
- Estatísticas avançadas
- Múltiplos usuários

### Fase 2 — Produto Publicável

**Inclui tudo da Fase 1, mais:**
- Notificações push locais por hábito
- Estatísticas: taxa de conclusão semanal/mensal, melhor streak
- Heatmap de consistência (estilo GitHub)
- Temas (light/dark)
- Backup/exportação local (JSON/CSV)
- Sincronização com backend (Supabase ou Firebase)
- Login com conta (Google / Apple)
- Widget iOS e Android
- Publicação na App Store e Google Play

---

## 3. Requisitos Funcionais

### RF-01 Gerenciar Hábitos
- RF-01.1 Criar hábito com nome, ícone/emoji, cor e frequência
- RF-01.2 Editar hábito existente
- RF-01.3 Arquivar hábito (não apaga, para não perder histórico)
- RF-01.4 Listar hábitos ativos

### RF-02 Frequência
- RF-02.1 Frequência diária: hábito deve ser feito todos os dias
- RF-02.2 Frequência semanal: hábito deve ser feito N vezes por semana (1-7), em qualquer dia

### RF-03 Registrar Execução
- RF-03.1 Marcar hábito como completo no dia atual
- RF-03.2 Desmarcar execução do dia atual (com confirmação)
- RF-03.3 Não permitir marcar mais de uma vez no mesmo dia

### RF-04 Streak
- RF-04.1 Calcular streak atual (dias/semanas consecutivos sem falhar)
- RF-04.2 Exibir melhor streak histórico
- RF-04.3 Streak quebra automaticamente se frequência não for cumprida

### RF-05 Histórico
- RF-05.1 Visualizar calendário de execuções por hábito
- RF-05.2 Indicar dias cumpridos, dias perdidos e dias futuros

---

## 4. Requisitos Não Funcionais

| Código | Requisito | Detalhe |
|--------|-----------|---------|
| RNF-01 | Offline-first | 100% funcional sem internet na Fase 1 |
| RNF-02 | Persistência local | SQLite via SQLDelight, dados no dispositivo |
| RNF-03 | Multiplataforma | Android (API 26+) e iOS (16+) com shared logic |
| RNF-04 | Performance | Tela principal carrega em < 300ms |
| RNF-05 | Consistência de dados | Nenhuma execução duplicada por hábito/dia |
| RNF-06 | Preparado para sync | Modelo com `id` UUID e `updatedAt` desde o início |

---

## 5. Modelo de Domínio

```
┌─────────────────────────────────────┐
│              Habit                  │
│─────────────────────────────────────│
│ id: UUID                            │
│ name: String                        │
│ emoji: String?                      │
│ color: String (hex)                 │
│ frequency: FrequencyType            │
│ timesPerWeek: Int? (se semanal)     │
│ createdAt: LocalDate                │
│ archivedAt: LocalDate?              │
│ updatedAt: Instant                  │
└────────────────┬────────────────────┘
                 │ 1
                 │
                 │ N
┌────────────────▼────────────────────┐
│          HabitCompletion            │
│─────────────────────────────────────│
│ id: UUID                            │
│ habitId: UUID (FK)                  │
│ date: LocalDate                     │
│ completedAt: Instant                │
└─────────────────────────────────────┘

FrequencyType (enum/sealed class):
  - Daily
  - Weekly(timesPerWeek: Int)
```

**Conceitos calculados (não persistidos):**
- `streak`: calculado em tempo real a partir de `HabitCompletion`
- `isCompletedToday`: derivado das completions do dia atual
- `isActive`: `archivedAt == null`

---

## 6. Regras de Negócio

| Código | Regra |
|--------|-------|
| RN-01 | Um hábito não pode ter mais de uma completion para o mesmo `date` |
| RN-02 | Desmarcar uma execução exige confirmação explícita do usuário |
| RN-03 | Para frequência `Daily`: o streak quebra se houver algum dia sem completion desde a criação |
| RN-04 | Para frequência `Weekly(N)`: o streak (em semanas) quebra se em alguma semana o número de completions for menor que N |
| RN-05 | Somente é possível marcar o dia atual (sem retroativo na Fase 1) |
| RN-06 | Arquivar um hábito preserva todo o histórico de completions |
| RN-07 | Hábitos arquivados não aparecem na tela principal nem contam para streaks ativos |
| RN-08 | Streak de um hábito semanal conta por semana ISO (segunda a domingo) |

---

## 7. Arquitetura KMP

```
habitos/
├── shared/                          ← Kotlin Multiplatform
│   ├── commonMain/
│   │   └── kotlin/com/habitos/
│   │       ├── domain/
│   │       │   ├── model/           ← Habit, HabitCompletion, FrequencyType
│   │       │   ├── repository/      ← interfaces (HabitRepository, CompletionRepository)
│   │       │   └── usecase/         ← toda a lógica de negócio
│   │       ├── data/
│   │       │   ├── db/              ← SQLDelight .sq files
│   │       │   └── repository/      ← implementações das interfaces
│   │       └── util/
│   │           └── DateUtils.kt     ← helpers de data (kotlinx-datetime)
│   ├── androidMain/
│   │   └── kotlin/                  ← DatabaseDriverFactory Android
│   └── iosMain/
│       └── kotlin/                  ← DatabaseDriverFactory iOS
│
├── androidApp/                      ← Android (Jetpack Compose)
│   └── src/main/kotlin/
│       ├── ui/
│       │   ├── habits/              ← tela principal
│       │   ├── detail/              ← detalhe + histórico
│       │   └── create/              ← criação/edição
│       └── MainActivity.kt
│
└── iosApp/                          ← iOS (SwiftUI)
    └── iosApp/
        ├── Views/
        │   ├── HabitListView.swift
        │   ├── HabitDetailView.swift
        │   └── CreateHabitView.swift
        └── ContentView.swift
```

**Fluxo de dados:**
```
UI → ViewModel/StateHolder → UseCase → Repository (interface) → RepositoryImpl → SQLDelight
```

A UI nunca acessa o banco diretamente. Toda lógica de negócio fica no `shared`.

---

## 8. Casos de Uso (shared/domain/usecase)

| Caso de Uso | Entrada | Saída | Regras aplicadas |
|-------------|---------|-------|-----------------|
| `CreateHabitUseCase` | nome, emoji, cor, frequência | `Habit` | valida nome não vazio |
| `ArchiveHabitUseCase` | `habitId` | Unit | seta `archivedAt` |
| `CompleteHabitUseCase` | `habitId`, `date` | `HabitCompletion` | RN-01, RN-05 |
| `UncompleteHabitUseCase` | `habitId`, `date` | Unit | RN-02 (confirmação na UI) |
| `GetTodayHabitsUseCase` | — | `List<HabitWithStatus>` | filtra arquivados, injeta `isCompletedToday` |
| `GetHabitStreakUseCase` | `habitId` | `StreakInfo` | RN-03, RN-04, RN-08 |
| `GetHabitHistoryUseCase` | `habitId`, mês/ano | `List<LocalDate>` (completions) | — |

**`HabitWithStatus`** (dado composto, não persistido):
```kotlin
data class HabitWithStatus(
    val habit: Habit,
    val isCompletedToday: Boolean,
    val currentStreak: Int
)
```

---

## 9. Estratégia de Persistência

**Biblioteca:** [SQLDelight](https://cashapp.github.io/sqldelight/) — gera código Kotlin typesafe a partir de arquivos `.sq`.

### Schema (`.sq` files em `shared/commonMain`)

```sql
-- Habit.sq
CREATE TABLE Habit (
    id          TEXT    NOT NULL PRIMARY KEY,   -- UUID string
    name        TEXT    NOT NULL,
    emoji       TEXT,
    color       TEXT    NOT NULL DEFAULT '#4CAF50',
    frequency   TEXT    NOT NULL,               -- "DAILY" | "WEEKLY"
    times_per_week INTEGER,                     -- NULL se DAILY
    created_at  TEXT    NOT NULL,               -- ISO-8601 LocalDate
    archived_at TEXT,                           -- NULL se ativo
    updated_at  TEXT    NOT NULL                -- ISO-8601 Instant
);

CREATE INDEX idx_habit_archived ON Habit(archived_at);

-- HabitCompletion.sq
CREATE TABLE HabitCompletion (
    id           TEXT NOT NULL PRIMARY KEY,     -- UUID string
    habit_id     TEXT NOT NULL REFERENCES Habit(id),
    date         TEXT NOT NULL,                 -- ISO-8601 LocalDate (YYYY-MM-DD)
    completed_at TEXT NOT NULL,                 -- ISO-8601 Instant

    UNIQUE(habit_id, date)                      -- RN-01: sem duplicatas
);

CREATE INDEX idx_completion_habit    ON HabitCompletion(habit_id);
CREATE INDEX idx_completion_date     ON HabitCompletion(date);
CREATE INDEX idx_completion_habit_date ON HabitCompletion(habit_id, date);
```

### Migrations

Cada alteração de schema gera um arquivo `migrations/X.sqm`. SQLDelight aplica automaticamente na inicialização. Nunca alterar schema sem criar migration.

### Driver por plataforma

```kotlin
// androidMain
actual fun createDriver(): SqlDriver =
    AndroidSqliteDriver(HabitosDatabase.Schema, context, "habitos.db")

// iosMain
actual fun createDriver(): SqlDriver =
    NativeSqliteDriver(HabitosDatabase.Schema, "habitos.db")
```

---

## 10. Estratégia Futura de Sincronização (Fase 2)

O modelo já está preparado com `id` (UUID) e `updatedAt` (Instant).

**Abordagem planejada para Fase 2:**
- Backend: Supabase (PostgreSQL + Auth + Realtime)
- Estratégia: **last-write-wins** por `updatedAt` — suficiente para dados pessoais de um único usuário
- Fila offline: operações pendentes salvas localmente e enviadas quando online
- Sem CRDTs na Fase 2 — complexidade desnecessária para o escopo

**O que NÃO mudar no schema ao sincronizar:**
- `id` continua UUID gerado no cliente
- `date` continua `LocalDate` (sem timezone — intencional, hábitos são locais ao dia do usuário)

---

## 11. Plano Incremental de Implementação

### Etapa 1 — Setup do Projeto (2-3h)
- [ ] Criar projeto KMP com Android Studio (template KMP)
- [ ] Configurar `build.gradle.kts` com SQLDelight e kotlinx-datetime
- [ ] Confirmar build Android e iOS funcionando (app em branco)

### Etapa 2 — Banco de Dados (2-3h)
- [ ] Criar arquivos `.sq` com schema de `Habit` e `HabitCompletion`
- [ ] Configurar `DatabaseDriverFactory` para Android e iOS
- [ ] Gerar código SQLDelight e confirmar compilação

### Etapa 3 — Domínio e Repositórios (3-4h)
- [ ] Criar data classes: `Habit`, `HabitCompletion`, `FrequencyType`
- [ ] Criar interfaces: `HabitRepository`, `CompletionRepository`
- [ ] Implementar repositórios com SQLDelight
- [ ] Escrever testes unitários dos repositórios

### Etapa 4 — Casos de Uso (3-4h)
- [ ] Implementar todos os use cases listados na seção 8
- [ ] Implementar lógica de streak (a mais complexa — testar bem)
- [ ] Escrever testes unitários dos use cases

### Etapa 5 — UI Android (4-6h)
- [ ] Tela principal: lista de hábitos do dia com check
- [ ] Tela de criação/edição de hábito
- [ ] Tela de detalhe: histórico em calendário + streak
- [ ] ViewModel conectando use cases à UI

### Etapa 6 — Build e Teste Android (1-2h)
- [ ] Testar fluxo completo no emulador e dispositivo físico
- [ ] Validar persistência (fechar e reabrir o app)

### Etapa 7 — UI iOS (4-6h)
- [ ] Replicar as 3 telas em SwiftUI consumindo o shared
- [ ] Testar no simulador iOS

### Etapa 8 — Build iOS (1-2h)
- [ ] Testar no simulador e dispositivo físico (se disponível)
- [ ] Validar que shared logic funciona igual em ambas plataformas

---

## 12. Evoluções Futuras Previstas (Fase 2+)

| Funcionalidade | Complexidade | Impacto |
|---------------|-------------|---------|
| Notificações locais por hábito | Média | Alta |
| Heatmap de consistência anual | Baixa | Alta |
| Taxa de conclusão semanal/mensal | Baixa | Média |
| Backup JSON/CSV local | Baixa | Média |
| Widget Android/iOS | Alta | Alta |
| Login + sync Supabase | Alta | Alta |
| Hábitos por horário do dia | Média | Média |
| Compartilhar streak (social) | Média | Baixa |

---

## Decisões Técnicas Registradas

| Decisão | Alternativa Considerada | Motivo da Escolha |
|---------|------------------------|-------------------|
| SQLDelight | Room (Android-only) | Funciona no shared, gera código typesafe |
| kotlinx-datetime | java.time | Suporte multiplataforma (iOS também) |
| UUID como String | Int autoincrement | Preparado para sync sem conflito de IDs |
| `date` como `LocalDate` sem timezone | `Instant` | Hábito pertence ao dia local do usuário |
| Streak calculado em runtime | Campo persistido | Evita inconsistência — fonte de verdade é a completion |

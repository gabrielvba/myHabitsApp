# Habitos — Histórias Técnicas (Fase 1)

> Sequência incremental de implementação com foco em testabilidade e aprendizado progressivo de KMP.
> Cada história é independente e entregável. UI começa apenas após toda a lógica estar testada.

---

## Grafo de Dependências

```
HT-00 (setup)
  └─► HT-01 (modelo de domínio)
        └─► HT-02 (interfaces repositórios)
              └─► HT-03 (fakes para testes)     ← testes unitários começam aqui
                    ├─► HT-04 (use cases CRUD)
                    ├─► HT-05 (registrar execução)
                    ├─► HT-06 (cálculo de streak)
                    └─► HT-07 (hoje + histórico)
                          └─► HT-08 (schema SQLDelight)
                                └─► HT-09 (DatabaseDriverFactory)
                                      └─► HT-10 (repositórios reais)   ← testes de integração
                                            ├─► HT-11 (UI Android: lista)
                                            ├─► HT-12 (UI Android: criação/detalhe)
                                            └─► HT-13 (UI iOS: SwiftUI)
```

**Princípio central:** a UI é apenas uma representação do domínio. Lógica de negócio não depende de nenhuma plataforma.

---

## Épico 0 — Fundação

### HT-00 · Setup do Projeto KMP

**O que entrega:**
- Projeto KMP criado via template oficial do Android Studio ("Kotlin Multiplatform App")
- `build.gradle.kts` configurado com:
  - SQLDelight 2.x (plugin + runtime)
  - kotlinx-datetime
  - kotlin-test para commonTest
- Build Android verde (emulador abre tela em branco)
- Build iOS verde (simulador abre tela em branco)
- Estrutura de módulos: `shared/`, `androidApp/`, `iosApp/`

**Critérios de aceite:**
- [ ] `./gradlew :androidApp:assembleDebug` passa sem erros
- [ ] Xcode consegue buildar o `iosApp` apontando para o framework gerado pelo shared
- [ ] Plugin SQLDelight aparece no classpath e gera código ao buildar

**Desafios e armadilhas:**
- O template do Android Studio muda entre versões — usar **Android Studio Koala ou superior** (suporte KMP estável)
- Version catalog (`libs.versions.toml`) é a forma moderna de declarar dependências em Gradle — muito conteúdo antigo usa `buildSrc` ou strings diretas, o que gera conflito
- SQLDelight 2.x quebrou compatibilidade com 1.x — ignorar tutoriais que usam `SqlDriver` de forma diferente da doc oficial atual
- iOS requer Xcode instalado; o KMP framework é gerado como `.framework` via task Gradle `embedAndSignAppleFrameworkForXcode`

**Recursos para estudar:**
- [Kotlin Multiplatform — Get Started (kotlinlang.org)](https://kotlinlang.org/docs/multiplatform-get-started.html)
- [SQLDelight 2.x — Getting Started](https://cashapp.github.io/sqldelight/2.x/multiplatform_sqlite/)
- [Version Catalogs no Gradle (docs.gradle.org)](https://docs.gradle.org/current/userguide/version_catalogs.html)
- Tópicos para pesquisar: `KMP template Android Studio`, `expect actual Kotlin`, `gradle kts vs groovy`, `libs.versions.toml`

---

## Épico 1 — Domínio Puro

> Código Kotlin puro em `shared/commonMain`. Zero dependências de plataforma, banco ou UI.

### HT-01 · Modelo de Domínio

**O que entrega:**
- `FrequencyType` — sealed class com dois casos: `Daily` e `Weekly(timesPerWeek: Int)`
- `Habit` — data class com todos os campos definidos no ARCHITECTURE.md
- `HabitCompletion` — data class com id, habitId, date, completedAt
- `HabitWithStatus` — data class composta (não persistida): habit + isCompletedToday + currentStreak
- `StreakInfo` — data class: current + best

**Critérios de aceite:**
- [ ] Todos os tipos compilam em `commonMain` sem imports de plataforma
- [ ] `FrequencyType.Weekly(timesPerWeek = 3)` é instanciável e seus dados são acessíveis
- [ ] `Habit` com `archivedAt = null` representa hábito ativo
- [ ] `copy()` do data class funciona para criar variações nos testes

**Desafios e armadilhas:**
- `LocalDate` e `Instant` devem vir de `kotlinx.datetime`, não de `java.time` — imports errados são o erro mais comum
- `sealed class` com dados (como `Weekly(n)`) é diferente de `enum` — não tem `.values()`, mas tem `when` exhaustivo
- Cuidado com campos opcionais nullable (`String?`) vs com default — `archivedAt: LocalDate? = null` é correto

**Recursos para estudar:**
- [Kotlin sealed classes (kotlinlang.org)](https://kotlinlang.org/docs/sealed-classes.html)
- [Kotlin data classes (kotlinlang.org)](https://kotlinlang.org/docs/data-classes.html)
- [kotlinx-datetime — uso básico (GitHub)](https://github.com/Kotlin/kotlinx-datetime)
- Tópicos para pesquisar: `Kotlin sealed class vs enum`, `kotlinx-datetime LocalDate`, `Kotlin data class copy`, `Kotlin nullable types`

---

### HT-02 · Interfaces dos Repositórios

**O que entrega:**
- `HabitRepository` — interface com operações: `save`, `findById`, `findAllActive`, `archive`
- `CompletionRepository` — interface com operações: `save`, `delete`, `findByHabitId`, `findByHabitIdAndDate`, `findByHabitIdBetween`
- Arquivo: `shared/commonMain/kotlin/com/habitos/domain/repository/`

**Critérios de aceite:**
- [ ] Nenhum arquivo neste pacote importa SQLDelight, Android SDK ou qualquer coisa de plataforma
- [ ] `CompletionRepository.findByHabitId(habitId)` retorna `List<HabitCompletion>`
- [ ] `HabitRepository.findAllActive()` retorna `List<Habit>` (sem arquivados)

**Desafios e armadilhas:**
- A interface deve usar tipos de domínio puro — nunca `Long` de banco, nunca `Cursor` de Android
- Funções suspensas (`suspend fun`) facilitam async mas não são obrigatórias na Fase 1 — para simplicidade, começar com funções síncronas e migrar depois se necessário
- Separação clara: `domain/repository` = contratos; `data/repository` = implementações

**Recursos para estudar:**
- [Clean Architecture em Android (medium/proandroiddev)](https://proandroiddev.com/clean-architecture-data-flow-dependency-rule-615c5e1d0341)
- [Kotlin interfaces (kotlinlang.org)](https://kotlinlang.org/docs/interfaces.html)
- Tópicos para pesquisar: `Clean Architecture repository pattern Kotlin`, `ports and adapters mobile`, `Kotlin suspend fun coroutines basics`

---

### HT-03 · Fakes dos Repositórios (base dos testes)

**O que entrega:**
- `FakeHabitRepository` em `shared/commonTest` — implementa `HabitRepository` com `MutableMap<String, Habit>`
- `FakeCompletionRepository` em `shared/commonTest` — implementa `CompletionRepository` com `MutableList<HabitCompletion>`
- Ambos com método `clear()` para resetar estado entre testes
- Primeiro teste de exemplo: `HabitRepositoryFakeTest` validando save + findById

**Critérios de aceite:**
- [ ] `FakeHabitRepository.save(habit)` persiste em memória e `findById` retorna o mesmo objeto
- [ ] `FakeHabitRepository.findAllActive()` exclui registros com `archivedAt != null`
- [ ] `FakeCompletionRepository.findByHabitIdAndDate()` retorna null se não existir
- [ ] Testes rodam com `./gradlew :shared:allTests` sem emulador

**Desafios e armadilhas:**
- `commonTest` roda na JVM (para Android) e via Kotlin/Native (para iOS) — não usar `System.currentTimeMillis()` ou qualquer API Java pura
- O padrão Fake é preferível a Mock em KMP porque frameworks como Mockito são JVM-only
- Não usar `@Before`/`@After` do JUnit — em KMP commonTest usa `kotlin.test` com `@BeforeTest`/`@AfterTest`

**Recursos para estudar:**
- [kotlin-test (kotlinlang.org)](https://kotlinlang.org/api/latest/kotlin.test/)
- [Test doubles: Fake vs Mock vs Stub (martinfowler.com)](https://martinfowler.com/articles/mocksArentStubs.html)
- Tópicos para pesquisar: `kotlin multiplatform commonTest`, `kotlin.test BeforeTest`, `fake repository pattern`, `KMP testing without mockito`

---

## Épico 2 — Lógica de Negócio

> Use cases testados com fakes. Nenhum banco, nenhuma UI.

### HT-04 · Use Cases: Gerenciar Hábitos (CRUD)

**O que entrega:**
- `CreateHabitUseCase` — valida e persiste novo hábito com UUID gerado
- `ArchiveHabitUseCase` — seta `archivedAt` no hábito
- `GetActiveHabitsUseCase` — retorna lista de hábitos não arquivados
- `expect fun generateUuid(): String` + `actual` por plataforma (Android: `UUID.randomUUID()`, iOS: `NSUUID`)

**Critérios de aceite (testes em commonTest):**
- [ ] `CreateHabitUseCase("")` lança `IllegalArgumentException` com mensagem descritiva
- [ ] `CreateHabitUseCase("Meditar")` retorna `Habit` com `id` não vazio e `archivedAt == null`
- [ ] `ArchiveHabitUseCase(id)` faz `findById(id).archivedAt != null`
- [ ] `GetActiveHabitsUseCase` não retorna hábitos já arquivados

**Desafios e armadilhas:**
- `java.util.UUID` não existe em `commonMain` — precisa de `expect/actual` ou biblioteca como `uuid` da Benassi
- Use cases devem receber repositórios via construtor (injeção de dependência manual, sem framework na Fase 1)
- Evitar lógica no construtor — validação vai dentro do método `execute()` ou `invoke()`

**Recursos para estudar:**
- [expect/actual em KMP (kotlinlang.org)](https://kotlinlang.org/docs/multiplatform-expect-actual.html)
- [Biblioteca uuid para KMP (github.com/benasher44/uuid)](https://github.com/benasher44/uuid)
- Tópicos para pesquisar: `KMP UUID generation`, `use case pattern clean architecture Kotlin`, `Kotlin operator fun invoke`

---

### HT-05 · Use Cases: Registrar Execução

**O que entrega:**
- `CompleteHabitUseCase(habitId, date)` — registra completion; aplica RN-01 e RN-05
- `UncompleteHabitUseCase(habitId, date)` — remove completion do dia
- `Clock` injetado nos use cases para controle do "hoje" nos testes

**Critérios de aceite (testes em commonTest):**
- [ ] Completar o mesmo hábito duas vezes no mesmo dia lança `HabitAlreadyCompletedException` (RN-01)
- [ ] Tentar completar em data diferente de hoje lança `CannotCompleteInPastException` (RN-05)
- [ ] `UncompleteHabitUseCase` em hábito sem completion lança `CompletionNotFoundException`
- [ ] Completar com clock fixado em 2026-01-15 e verificar que `completion.date == 2026-01-15`

**Desafios e armadilhas:**
- `LocalDate.now()` hardcoded torna o teste impossível — clock sempre deve ser injetado
- Criar exceções de domínio customizadas (ex: `HabitAlreadyCompletedException : Exception()`) é mais legível que strings de erro
- Em Kotlin, `throw` é uma expressão — pode ser usado no lado direito de `?: throw ...`

**Recursos para estudar:**
- [Padrão Clock para testes (fowler.com)](https://martinfowler.com/bliki/ClockWrapper.html)
- [Kotlin exceptions (kotlinlang.org)](https://kotlinlang.org/docs/exceptions.html)
- Tópicos para pesquisar: `Kotlin custom exceptions`, `clock injection testing`, `kotlinx-datetime Clock.System`, `Kotlin elvis operator throw`

---

### HT-06 · Use Case: Cálculo de Streak *(história mais complexa)*

**O que entrega:**
- `GetHabitStreakUseCase(habitId)` → `StreakInfo(current: Int, best: Int)`
- Lógica separada por frequência: `DailyStreakCalculator` e `WeeklyStreakCalculator`
- Cálculo de semana ISO (segunda a domingo) para frequência semanal

**Critérios de aceite (testes em commonTest — testar cada cenário em método separado):**
- [ ] Hábito diário sem completions → `current = 0, best = 0`
- [ ] Hábito diário com 5 dias consecutivos → `current = 5`
- [ ] Hábito diário com gap ontem → `current = 0` (quebrou hoje)
- [ ] Hábito diário: sequência de 7, gap, sequência de 3 → `current = 3, best = 7`
- [ ] Hábito semanal 3x: semana com 3 completions = cumprida; `current = 1`
- [ ] Hábito semanal 3x: semana com 2 completions = quebrada; `current = 0`
- [ ] Streak semanal: 3 semanas cumpridas → `current = 3`
- [ ] Semana ISO começa na segunda: completion na segunda pertence à semana correta

**Desafios e armadilhas:**
- Esta é a lógica mais propensa a bugs sutis — escrever os testes ANTES do código (TDD aqui é altamente recomendado)
- Semana ISO: `LocalDate.dayOfWeek` em kotlinx-datetime retorna `DayOfWeek` com `MONDAY = 1, SUNDAY = 7` — use `date.minus(date.dayOfWeek.value - 1, DateTimeUnit.DAY)` para obter o início da semana
- O streak atual deve checar se a semana/dia corrente já foi cumprido para não punir o usuário no meio do dia
- Separar `DailyStreakCalculator` e `WeeklyStreakCalculator` como classes internas ou funções puras facilita testes unitários isolados

**Recursos para estudar:**
- [ISO 8601 week (wikipedia)](https://en.wikipedia.org/wiki/ISO_week_date)
- [kotlinx-datetime API reference](https://kotlinlang.org/api/kotlinx-datetime/)
- Tópicos para pesquisar: `ISO week start Monday Kotlin`, `kotlinx-datetime DateTimeUnit`, `TDD Kotlin kotlin-test`, `streak algorithm consecutive dates`

---

### HT-07 · Use Cases: Status do Dia e Histórico

**O que entrega:**
- `GetTodayHabitsUseCase` → `List<HabitWithStatus>` (apenas ativos, com `isCompletedToday` e `currentStreak`)
- `GetHabitHistoryUseCase(habitId, year, month)` → `List<LocalDate>` das completions naquele mês

**Critérios de aceite (testes em commonTest):**
- [ ] `GetTodayHabits` não inclui hábitos arquivados
- [ ] `isCompletedToday = true` somente se há completion com `date == today`
- [ ] `currentStreak` no `HabitWithStatus` bate com `GetHabitStreakUseCase` para o mesmo habitId
- [ ] `GetHabitHistory(2026, 1)` retorna apenas datas de janeiro de 2026 com completion
- [ ] `GetHabitHistory` para mês sem completions retorna lista vazia (não null)

**Desafios e armadilhas:**
- `GetTodayHabitsUseCase` compõe `GetHabitStreakUseCase` internamente — injetar o segundo no primeiro via construtor
- Evitar retornar `null` — prefira lista vazia ou `Result<T>`
- Clock injetado aqui também — "hoje" deve ser parametrizável nos testes

**Recursos para estudar:**
- [Kotlin collections: filter, map (kotlinlang.org)](https://kotlinlang.org/docs/collection-filtering.html)
- Tópicos para pesquisar: `Kotlin Result type`, `composing use cases clean architecture`, `Kotlin list operations filter map`

---

## Épico 3 — Persistência Real

> Substitui os fakes por implementações reais com SQLDelight. Os testes de use case continuam verdes.

### HT-08 · Schema SQLDelight e Geração de Código

**O que entrega:**
- `Habit.sq` com DDL completo, índices e queries nomeadas: `insertHabit`, `selectAllActive`, `selectById`, `updateArchived`
- `HabitCompletion.sq` com DDL, constraint `UNIQUE(habit_id, date)`, índices e queries: `insertCompletion`, `deleteCompletion`, `selectByHabitId`, `selectByHabitIdAndDate`, `selectByHabitIdBetween`
- Código Kotlin gerado pelo plugin SQLDelight (verificar em `build/generated/`)
- `HabitosDatabase` interface gerada com acesso às queries

**Critérios de aceite:**
- [ ] `./gradlew :shared:generateCommonMainHabitosDatabaseInterface` executa sem erro
- [ ] Código gerado contém `HabitQueries` e `HabitCompletionQueries`
- [ ] Constraint `UNIQUE(habit_id, date)` existe no schema (valida RN-01 em nível de banco)
- [ ] Índices declarados corretamente para as queries de histórico por período

**Desafios e armadilhas:**
- Nomes de queries em `.sq` devem ser `camelCase` — o plugin gera funções Kotlin com o mesmo nome
- SQLDelight infere o tipo de retorno automaticamente: `SELECT *` retorna a data class gerada; `SELECT COUNT(*)` retorna `Long`
- `REFERENCES Habit(id)` ativa FK — SQLDelight precisa de `PRAGMA foreign_keys = ON` explicitamente no driver (não é padrão SQLite)
- Nunca editar código gerado — sempre editar os `.sq` files

**Recursos para estudar:**
- [SQLDelight 2.x — Multiplatform SQLite (cashapp.github.io)](https://cashapp.github.io/sqldelight/2.x/multiplatform_sqlite/)
- [SQLDelight — Queries (cashapp.github.io)](https://cashapp.github.io/sqldelight/2.x/common/sql_statements/)
- Tópicos para pesquisar: `SQLDelight 2 named queries`, `SQLDelight custom types adapters`, `SQLite PRAGMA foreign_keys`, `SQLDelight generated code structure`

---

### HT-09 · DatabaseDriverFactory (expect/actual por plataforma)

**O que entrega:**
- `expect class DatabaseDriverFactory` em `shared/commonMain`
- `actual class DatabaseDriverFactory(context: Context)` em `shared/androidMain` usando `AndroidSqliteDriver`
- `actual class DatabaseDriverFactory()` em `shared/iosMain` usando `NativeSqliteDriver`
- `createInMemoryDriver()` para testes (JVM) — retorna `JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)`

**Critérios de aceite:**
- [ ] Projeto compila em Android e iOS sem erro
- [ ] Driver in-memory criado em `commonTest` permite executar queries SQLDelight
- [ ] `./gradlew :shared:allTests` passa com driver in-memory

**Desafios e armadilhas:**
- `expect class` é diferente de `expect fun` — a classe pode ter construtor diferente por plataforma
- `JdbcSqliteDriver` (para testes JVM) é uma dependência separada: `testImplementation("app.cash.sqldelight:sqlite-driver:2.x")`
- iOS não tem contexto Android — `NativeSqliteDriver` usa o path nativo automaticamente
- O schema precisa ser criado explicitamente com `HabitosDatabase.Schema.create(driver)` no teste

**Recursos para estudar:**
- [SQLDelight — Testing (cashapp.github.io)](https://cashapp.github.io/sqldelight/2.x/common/testing/)
- [KMP expect/actual classes (kotlinlang.org)](https://kotlinlang.org/docs/multiplatform-expect-actual.html#expected-and-actual-classes)
- Tópicos para pesquisar: `SQLDelight JdbcSqliteDriver testing`, `KMP expect class constructor`, `AndroidSqliteDriver NativeSqliteDriver`

---

### HT-10 · Implementações Reais dos Repositórios

**O que entrega:**
- `HabitRepositoryImpl(db: HabitosDatabase)` implementando `HabitRepository` com SQLDelight
- `CompletionRepositoryImpl(db: HabitosDatabase)` implementando `CompletionRepository`
- Adaptadores de tipo: `LocalDate ↔ String ISO`, `Instant ↔ String ISO`, `FrequencyType ↔ String`
- Testes de integração em `commonTest` usando driver in-memory — mesmos cenários dos fakes

**Critérios de aceite:**
- [ ] `HabitRepositoryImpl.save(habit)` persiste e `findById` retorna o mesmo objeto reconstituído
- [ ] `CompletionRepositoryImpl.save()` com `habit_id, date` duplicado lança `SQLiteException` (UNIQUE constraint)
- [ ] Testes de integração passam com `./gradlew :shared:allTests` (sem emulador)
- [ ] Fechar e reabrir o driver (simular reinício) mantém os dados — banco não é in-memory neste cenário

**Desafios e armadilhas:**
- Adaptadores de tipo em SQLDelight 2.x usam `columnAdapter` — é diferente da sintaxe 1.x
- `LocalDate.toString()` em kotlinx-datetime já produz ISO `YYYY-MM-DD` — usar `LocalDate.parse()` para reconstruir
- A `SQLiteException` de violação de constraint tem código `-1` em Android e mensagem diferente em iOS — capturar pela mensagem ou criar wrapper no repositório
- Testes de integração devem chamar `HabitosDatabase.Schema.create(driver)` no setup

**Recursos para estudar:**
- [SQLDelight — Column Adapters (cashapp.github.io)](https://cashapp.github.io/sqldelight/2.x/common/adapters/)
- [kotlinx-datetime parse/format](https://github.com/Kotlin/kotlinx-datetime#formatting-and-parsing-local-date-and-time-values)
- Tópicos para pesquisar: `SQLDelight 2 column adapter`, `SQLDelight integration test in-memory`, `Kotlin SQLite unique constraint handling`

---

## Épico 4 — Interface do Usuário

> Lógica 100% pronta e testada. A UI apenas consome use cases via ViewModel/StateHolder.

### HT-11 · UI Android — Tela Principal (Jetpack Compose)

**O que entrega:**
- `HabitListViewModel` — consome `GetTodayHabitsUseCase` e `CompleteHabitUseCase`; expõe `StateFlow<HabitListState>`
- `HabitListScreen` em Compose — `LazyColumn` com cada hábito, checkbox e streak
- Navegação básica: botão para tela de criação
- `MainActivity` inicializando o grafo de dependências manualmente (sem DI framework)

**Critérios de aceite:**
- [ ] Marcar hábito como completo persiste após fechar e reabrir o app
- [ ] Streak exibido na lista atualiza após marcar
- [ ] Hábitos arquivados não aparecem na lista
- [ ] Tela carrega em menos de 300ms em emulador médio

**Desafios e armadilhas:**
- `ViewModel` de `androidx.lifecycle` não existe no `shared` — criar `HabitListViewModel` no módulo `androidApp` usando `androidx.lifecycle.ViewModel`
- `StateFlow` substituiu `LiveData` — `collectAsStateWithLifecycle()` é a forma moderna em Compose
- Ciclo de vida em Compose é diferente de Activity — `LaunchedEffect` para side effects, não `onCreate`
- Inicialização manual do banco (`DatabaseDriverFactory(context)`) deve acontecer no `Application` ou na `Activity`, não no `ViewModel`

**Recursos para estudar:**
- [Jetpack Compose — Getting Started (developer.android.com)](https://developer.android.com/jetpack/compose/tutorial)
- [StateFlow em Compose (developer.android.com)](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- [ViewModel com Compose (developer.android.com)](https://developer.android.com/jetpack/compose/libraries#viewmodel)
- Tópicos para pesquisar: `Jetpack Compose LazyColumn`, `StateFlow collectAsStateWithLifecycle`, `Compose ViewModel manual DI`, `Android Application class initialization`

---

### HT-12 · UI Android — Criação e Detalhe

**O que entrega:**
- `CreateHabitScreen` — formulário com nome, emoji, cor e frequência; chama `CreateHabitUseCase`
- `HabitDetailScreen` — exibe streak atual, melhor streak e calendário do mês com dias marcados
- `NavHost` com rotas: `"list"`, `"create"`, `"detail/{habitId}"`
- `HabitDetailViewModel` consumindo `GetHabitStreakUseCase` e `GetHabitHistoryUseCase`

**Critérios de aceite:**
- [ ] Criar hábito com nome vazio exibe mensagem de erro na tela (não crasha)
- [ ] Hábito criado aparece na lista imediatamente após voltar
- [ ] Calendário do mês exibe corretamente dias marcados (verde) e não marcados
- [ ] Navegar detalhe → lista → detalhe mantém o estado correto

**Desafios e armadilhas:**
- Navigation Compose usa `NavController` — passar dados simples (habitId) via rota string; dados complexos via `ViewModel` compartilhado ou repositório
- Calendário customizado: usar `LazyVerticalGrid(columns = Fixed(7))` com lógica para calcular o offset do primeiro dia do mês
- `BackHandler` para capturar botão voltar no Android se necessário
- Formulário de frequência: alternar entre `Daily` e `Weekly` mudando a UI dinamicamente (mostrar/esconder campo N vezes)

**Recursos para estudar:**
- [Navigation Compose (developer.android.com)](https://developer.android.com/jetpack/compose/navigation)
- [LazyVerticalGrid (developer.android.com)](https://developer.android.com/jetpack/compose/lists#lazy-grids)
- Tópicos para pesquisar: `Compose NavController arguments`, `Compose LazyVerticalGrid calendar`, `Compose form validation`, `Compose BackHandler`

---

### HT-13 · UI iOS — SwiftUI consumindo shared

**O que entrega:**
- `HabitListView` em SwiftUI com lista de hábitos e toggle de completion
- `CreateHabitView` com formulário equivalente ao Android
- `HabitDetailView` com streak e calendário
- `HabitStore` (ObservableObject) em Swift — wrapper dos use cases Kotlin para o SwiftUI

**Critérios de aceite:**
- [ ] Build no simulador iOS passa sem erros
- [ ] Marcar hábito persiste (mesma base SQLite, lógica compartilhada)
- [ ] Streak exibido é idêntico ao Android para os mesmos dados
- [ ] Zero lógica de negócio duplicada em Swift — tudo vem do shared

**Desafios e armadilhas:**
- `suspend fun` Kotlin vira função com callback em Swift — KMP exporta como `(result, error) -> Unit`; usar wrappers ou `AsyncFunction` helper
- `Flow<T>` Kotlin não tem equivalente direto em Swift — usar `StateFlow` e polling/wrapper ou biblioteca `KMPNativeCoroutines`
- O framework Kotlin gerado para iOS usa nomenclatura em camelCase mas pode ter conflitos com palavras reservadas Swift
- `@HiddenFromObjC` em Kotlin esconde funções do framework iOS — funções com tipos genéricos complexos podem precisar de wrappers simples

**Recursos para estudar:**
- [KMP — iOS Integration (kotlinlang.org)](https://kotlinlang.org/docs/multiplatform-ios-integration-overview.html)
- [SwiftUI — Getting Started (developer.apple.com)](https://developer.apple.com/tutorials/swiftui)
- [KMP-NativeCoroutines (github.com/rickclephas)](https://github.com/rickclephas/KMP-NativeCoroutines)
- Tópicos para pesquisar: `KMP Swift interop`, `Kotlin suspend function Swift callback`, `SwiftUI ObservableObject`, `KMP-NativeCoroutines StateFlow`

---

## Resumo de Esforço Estimado

| História | Complexidade | Estimativa |
|----------|-------------|-----------|
| HT-00 Setup | Média (tooling) | 2–4h |
| HT-01 Modelo | Baixa | 1h |
| HT-02 Interfaces | Baixa | 1h |
| HT-03 Fakes | Baixa | 1–2h |
| HT-04 CRUD use cases | Baixa | 2–3h |
| HT-05 Execução | Média | 2–3h |
| HT-06 Streak | **Alta** | 4–6h |
| HT-07 Histórico | Baixa | 1–2h |
| HT-08 Schema SQL | Baixa | 1–2h |
| HT-09 Driver Factory | Média | 2–3h |
| HT-10 Repos reais | Média | 3–4h |
| HT-11 UI Android lista | Média | 3–5h |
| HT-12 UI Android forms | Média | 4–6h |
| HT-13 UI iOS | **Alta** | 5–8h |
| **Total estimado** | | **32–50h** |

> Estimativas para alguém aprendendo Kotlin/KMP simultaneamente. Incluem tempo de leitura de docs e debugging.

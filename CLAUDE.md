# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build Android app
./gradlew :androidApp:assembleDebug

# Run all shared (commonTest) unit tests — no emulator needed
./gradlew :shared:allTests

# Run a single test class
./gradlew :shared:allTests --tests "com.habitos.domain.usecase.StreakCalculatorsTest"

# Generate SQLDelight code (run after editing .sq files)
./gradlew :shared:generateCommonMainHabitosDatabaseInterface

# iOS: open in Xcode after building the shared framework
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
open iosApp/iosApp.xcodeproj
```

## Architecture

Kotlin Multiplatform project. All business logic lives in `shared/` and is shared across Android and iOS. Neither platform module contains any domain logic.

```
shared/commonMain   ← domain model, use cases, repository interfaces, SQLDelight queries
shared/androidMain  ← DatabaseDriverFactory (Android), UUID generation (actual)
shared/iosMain      ← DatabaseDriverFactory (iOS)
shared/commonTest   ← unit tests (fakes) + integration tests (in-memory SQLite)
androidApp/         ← Jetpack Compose UI + ViewModels (consume shared use cases)
iosApp/             ← SwiftUI views (consume shared use cases via Kotlin framework)
```

**Data flow:** `UI → ViewModel → UseCase → Repository interface → RepositoryImpl → SQLDelight`

The UI never touches the database. ViewModels never contain business logic.

## Dependency Injection

Manual DI — no framework. `MainActivity.kt` wires the entire graph: driver → database → repositories → use cases → ViewModels. When adding a new use case, wire it in `MainActivity` and pass it through `AppNavigation`.

## Domain Layer (`shared/commonMain`)

- **Models:** `Habit`, `HabitCompletion`, `FrequencyType` (sealed class: `Daily` | `Weekly(timesPerWeek)`)
- **Computed (never persisted):** `HabitWithStatus`, `StreakInfo`
- **Custom exceptions:** defined in `domain/model/Exceptions.kt` — use these instead of generic exceptions
- **Clock injection:** all use cases that depend on "today" accept `clock: Clock = Clock.System` — always pass a fixed clock in tests

## Testing

Unit tests use `FakeHabitRepository` / `FakeCompletionRepository` (in `commonTest`) — no mocking framework (Mockito is JVM-only; KMP tests run on both JVM and Kotlin/Native).

Integration tests (`RealRepositoriesIntegrationTest`) use `JdbcSqliteDriver(IN_MEMORY)` via `TestDatabaseDriverFactory` — analogous to H2 in Spring. Call `HabitosDatabase.Schema.create(driver)` in test setup.

The streak logic has the most edge cases — see `StreakCalculatorsTest` for the full scenario matrix before modifying `GetHabitStreakUseCase`.

## SQLDelight

`.sq` files are in `shared/src/commonMain/sqldelight/com/habitos/data/db/`. Edit these files (never the generated code in `build/`). Run `generateCommonMainHabitosDatabaseInterface` after any schema change.

Type adapters for `LocalDate`, `Instant`, and `FrequencyType` are in `data/repository/Adapters.kt`. Add new adapters there when adding new domain types to the schema.

## Key Constraints

- `commonMain` must never import `java.*` — use `kotlinx.datetime` for all date/time types
- UUID generation uses `expect/actual`: `commonMain` declares `expect fun generateUuid()`, platform-specific `actual` implementations are in `androidMain` and `iosMain`
- Streak is always calculated at runtime from completion records — never persist or cache it
- `UNIQUE(habit_id, date)` constraint in `HabitCompletion` enforces RN-01 at the DB level

## Documentation

`docs/ARCHITECTURE.md` — domain model, business rules (RN-01 to RN-08), DB schema decisions  
`docs/STORIES.md` — 13 technical stories (HT-00 to HT-13) with acceptance criteria, ordered by dependency  
`docs/summaries/` — per-story implementation notes written after each story was completed

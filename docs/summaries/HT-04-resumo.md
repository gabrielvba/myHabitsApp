# Resumo - HT-04 · Use Cases: Gerenciar Hábitos (CRUD)

## Decisões Tomadas
- O gerador de UUIDs foi implementado seguindo o mecanismo de `expect`/`actual` de forma agnóstica entre plataformas: `java.util.UUID` para Android (`androidMain`) e `platform.Foundation.NSUUID` para iOS (`iosMain`).
- Foram implementados os UseCases básicos para lidar com hábitos: `CreateHabitUseCase`, `ArchiveHabitUseCase` e `GetActiveHabitsUseCase`.
- Foram utilizadas exceções simples customizadas (`IllegalArgumentException`) ao lidar com regras de domínio violadas (nome em branco, id inexistente), e `Clock` para evitar dependência de `LocalDate.now()` no tempo.
- Não houveram mocks externos; foi mantida a estrutura Fake com `FakeHabitRepository`.

## Testes Realizados
- Os comportamentos listados foram testados usando as suítes em `ManageHabitsUseCasesTest`, sendo validados campos como não-aceitação de nomes em branco, inserção correta de dados preenchidos, transição de estados null para não-null de exclusão (arquivamento), além de testar os IDs auto-gerados pela plataforma nativa que roda o teste.

## Próximos Passos
- Criação dos UseCases para registro e exclusão de marcação das execuções e cálculo das frequências (HT-05 e HT-06).

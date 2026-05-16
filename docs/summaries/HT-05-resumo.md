# Resumo - HT-05 · Use Cases: Registrar Execução

## Decisões Tomadas
- Adicionadas as exceções customizadas no pacote `domain/model` (`HabitAlreadyCompletedException`, `CannotCompleteInPastException` e `CompletionNotFoundException`).
- Implementado `CompleteHabitUseCase` responsável por aplicar RN-01 (unicidade de data/habito) e RN-05 (registro apenas no "hoje"). O conceito de hoje é abstraído via injenção de dependência de tempo com `kotlinx.datetime.Clock`.
- Implementado `UncompleteHabitUseCase`, onde exigimos apenas saber se o hábito já tinha sido completado antes para permitir o delete via repositório.

## Testes Realizados
- Foi feito um wrap local de tempo com a classe `FixedClock` para garantir a injeção determinística do "Hoje" em `2026-01-15`.
- Todos os casos e regras de exceções, falhas e de sucessos foram contemplados nos testes na suíte `CompletionUseCasesTest`. Todos executados e passando sem falhas no ambiente JVM do commonTest.

## Próximos Passos
- Criação dos UseCases de Streak (cálculos analíticos do histórico contínuo da corrente do hábito) - essa história será a mais complexa da fase de negócios (HT-06).

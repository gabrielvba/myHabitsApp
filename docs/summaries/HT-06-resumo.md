# Resumo - HT-06 · Use Case: Cálculo de Streak

## Decisões Tomadas
- O cálculo de sequências contínuas ("streaks") foi implementado de forma pura em Kotlin `commonMain` sob os objetos `DailyStreakCalculator` e `WeeklyStreakCalculator`.
- Como estabelecido pelas regras do negócio, as semanas foram isoladas no formato ISO (onde Segunda é o primeiro dia e Domingo o último), via a extension customizada `startOfWeekIso()`.
- Lógicas complexas de streak foram mitigadas por TDD, onde o `today` tem tolerância para falha no "current" sem cortar o "best". Apenas quando uma janela real de falha (ontem para diário, semana passada para semanal) ocorre o loop identifica a corrente quebrada.

## Testes Realizados
- Diversos cenários unitários definidos na documentação do projeto foram criados em `StreakCalculatorsTest`, incluindo: zero execuções, cumprimento no dia exato, falha total na véspera, e cumprimento na primeira/última data semanal.
- O build completo de testes (`:shared:test`) confirmou o comportamento de cada cenário corretamente, tanto para as frequências diárias quanto semanais (ex.: calcular offset reverso, validar 3 completitudes na semana do `Weekly(3)`).

## Próximos Passos
- Criação dos UseCases responsáveis por mapear o status do dia atual e recuperar histórico com composições visando o modelo final da tela (HT-07).

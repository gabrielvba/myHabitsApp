# Resumo - HT-07 · Use Cases: Status do Dia e Histórico

## Decisões Tomadas
- Adicionados `GetTodayHabitsUseCase` e `GetHabitHistoryUseCase` para lidar com as representações complexas de interface combinando os models e informações analíticas (Streak, "Completed Today", calendário).
- O `GetTodayHabitsUseCase` compõe dentro dele o `GetHabitStreakUseCase` (passado via injeção) e filtra os hábitos que já estão arquivados, facilitando a montagem do "HabitWithStatus" que dita o formato da UI.
- `GetHabitHistoryUseCase` utiliza matemática simples de datas no `LocalDate` (adicionando e removendo dias desde as bordas do mês) para delegar a busca dentro de um "between" de datas.

## Testes Realizados
- Foi adicionada a suíte `TodayAndHistoryUseCasesTest` englobando as checagens com Mock de Repositórios e injeção do FixedClock. Testado com sucesso que tarefas arquivadas de fato não entram no array de hoje, que "completed today" avalia corretamente como true/false, e que o calendário trará precisamente o que está dentro do mês solicitado.

## Próximos Passos
- Como a fundação de domínio e lógica está solidificada em 100% de uso de Fakes em Common, a próxima tarefa é implementar toda a infraestrutura baseada no banco real (SQLDelight) com geração de código, conforme delineado no Épico 3 (HT-08).

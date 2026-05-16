# Resumo - HT-12 · UI Android — Criação e Detalhe

## Decisões Tomadas
- O Compose Navigation (`navigation-compose`) foi adicionado como dependência para gerenciar o roteamento entre as telas `HabitListScreen`, `CreateHabitScreen` e `HabitDetailScreen`.
- No componente Root (Main Activity), todos os use cases foram instanciados utilizando injeção de dependência manual inicializando a partir da instância do `DatabaseDriverFactory(this)` (context).
- A tela `CreateHabitScreen` gerencia seu formulário usando MutableStateFlow encapsulado pelo `CreateHabitViewModel`. Ao salvar com sucesso, avisa a UI para invocar `navController.popBackStack()`.
- O Detail constrói o pequeno mockup do calendário chamando `GetHabitHistoryUseCase` e `GetHabitStreakUseCase` filtrados pelo Habit ID enviado como String Type na rota.

## Testes Realizados
- Validamos a conexão entre views, imports, argumentos das views e ViewModels rodando a target `:androidApp:assembleDebug` do Gradle, que confirmou a injeção funcional e estrutura compatível com os UseCases Kotlin de Domínio puros.

## Próximos Passos
- Criação das Views SwiftUI no projeto iOSApp delegando as funções e ViewModels baseados diretamente na arquitetura KMP. (HT-13).

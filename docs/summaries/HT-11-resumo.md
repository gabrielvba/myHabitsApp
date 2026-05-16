# Resumo - HT-11 · UI Android — Tela Principal

## Decisões Tomadas
- Adicionada a dependência do `kotlinx-datetime` ao build.gradle do AndroidApp para poder manipular os tipos do Clock no ViewModel.
- Criada a `HabitListViewModel` herdando de `androidx.lifecycle.ViewModel` e usando o `viewModelScope.launch` para lidar com as chamadas de banco, que são tratadas via stateflows e transformações.
- Criadas as telas com `Jetpack Compose` (`HabitListScreen`), manipulando a state machine do ViewModel usando `collectAsState`, e permitindo callback de eventos para navegar (`onHabitClick`, `onCreateClick`) e alternar completions (`onToggle`).

## Testes Realizados
- Foi testado o build de `assembleDebug` do aplicativo. Todos os erros de dependência foram resolvidos via gradle (incluindo datetime na layer do app). O módulo Compose buildou a sintaxe e UI com sucesso.

## Próximos Passos
- Expandir as demais rotas em Compose e fechar o escopo da navegação inicial criando o Grafo (HT-12), com inicialização manual do Dependency Injection conforme explicitado no design.

# Resumo - HT-02 · Interfaces dos Repositórios

## Decisões Tomadas
- Adicionadas as interfaces `HabitRepository` e `CompletionRepository` na pasta de domínio, separando completamente as operações do banco de dados (Clean Architecture).
- Usamos os tipos de dados `LocalDate` do `kotlinx-datetime` puros em `commonMain` nas buscas, garantindo total isolamento da implementação nativa de bancos de dados.
- Funções como síncronas foram mantidas nesta fase 1 inicial para simplificação, conforme orientações dadas no arquivo `STORIES.md`.

## Testes Realizados
- Compilação das abstrações usando a Task `:shared:compileDebugKotlinAndroid`. Sem falhas.

## Próximos Passos
- Criação dos mocks em memória (Fakes) usando as interfaces implementadas, para assim conseguir fazer testes dos casos de uso futuramente sem envolver o database SQLite (HT-03).

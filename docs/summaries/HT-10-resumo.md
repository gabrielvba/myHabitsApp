# Resumo - HT-10 · Implementações Reais dos Repositórios

## Decisões Tomadas
- O objeto Kotlin `ColumnAdapter` customizado foi criado de acordo com a documentação do SQLDelight 2.x para parear `LocalDate` e `Instant` do Multiplatform Datetime com strings ISO (encode e decode limpos).
- `HabitRepositoryImpl` e `CompletionRepositoryImpl` agora envelopam as Queries de forma transparente com mapToHabit/mapToCompletion, lidando com `FrequencyType` em serialização de strings.
- O método de arquivo na interface do repositório levanta exceção para delegar a responsabilidade do tempo (`updatedAt`) sempre para o Usecase que compõe com `save`.
- Caso uma mesma data seja salva em `CompletionRepositoryImpl`, repassamos o exception de constraint do banco permitindo que a regra `RN-01` tenha suporte transacional.

## Testes Realizados
- Foi feito o `RealRepositoriesIntegrationTest` instanciando um banco em memória pelo JDBC para validar transações reais e mapping em CommonTest.
- As consultas via mock foram mapeadas e o teste passou `testReleaseUnitTest` / `testDebugUnitTest` sem vazamento de erros de constraint de tipo (String para enum, ou String para Data).
- O teste de duplicatas (mesmo date num habitId) resultou num assertFails assertivo do `SQLiteException`.

## Próximos Passos
- Criação das telas no ecossistema final Android, começando pela `HabitListScreen` (HT-11) para listar hábitos baseados nos dados testados.

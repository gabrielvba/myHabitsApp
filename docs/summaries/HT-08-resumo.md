# Resumo - HT-08 · Schema SQLDelight e Geração de Código

## Decisões Tomadas
- O schema SQL (Data Definition Language) foi inserido em arquivos com extensão `.sq` no caminho source `shared/src/commonMain/sqldelight/...`.
- Foi estabelecida a UNIQUE constraint no `HabitCompletion.sq` de forma a garantir a integridade da RN-01 em nível de banco de dados, protegendo contra inserções duplicadas por concorrência para a mesma data num mesmo hábito.
- Foi mantido o "FOREIGN KEY" constraint do SQLite via uso de `REFERENCES Habit(id)`. Como citado na documentação de setup, o driver precisará explicitar no momento de criar/conectar de que as chaves estrangeiras devem ser checadas via PRAGMA, embora o SQLDelight crie a constraint normalmente na tabela.

## Testes Realizados
- Foi executada a target de plugin `generateCommonMainHabitosDatabaseInterface`. A pasta de build agora contém os arquivos e queries como objetos gerados pelo framework baseados puramente no `.sq`. Sem erros sintáticos.

## Próximos Passos
- Criação dos Adapters em Kotlin e as fábricas de conexões (`DatabaseDriverFactory`) implementando o driver in-memory para rodar de fato os testes baseados na geração SQLDelight (HT-09).

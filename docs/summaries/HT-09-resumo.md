# Resumo - HT-09 · DatabaseDriverFactory

## Decisões Tomadas
- O SQLDelight requer instanciações diferentes do SQLite Driver pra cada plataforma (Android usa `AndroidSqliteDriver`, iOS usa `NativeSqliteDriver` por debaixo dos panos co.touchlab.sqliter). Para suportar, foi criada uma classe usando `expect`/`actual`.
- Em Android, repassamos o contexto e ativamos checagem de foreign keys pelo Callback.
- Em iOS, as `DatabaseConfiguration.Extended` foram atualizadas para checar constraints relativas às chaves estrangeiras.
- Em Kotlin/Native e in-memory (para os testes da JVM `TestDatabaseDriverFactory`), foi incluído o pragma de checagem.

## Testes Realizados
- Foi compilado com sucesso os binários com KotlinAndroid `:shared:compileDebugKotlinAndroid`.
- Não houve testes locais por estarmos apenas abrindo as chaves com framework JDBC puro, que precisará ser consumido no Repositório para teste de transação.

## Próximos Passos
- Implementar as classes de Repository concretas que consumirão essas transações baseadas nos Drivers provisionados na injeção ou testes in-memory. (HT-10).

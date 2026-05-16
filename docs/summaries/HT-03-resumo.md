# Resumo - HT-03 · Fakes dos Repositórios (base dos testes)

## Decisões Tomadas
- O padrão `Fake` foi preferido ao invés do uso de mocks dinâmicos (como Mockito ou MockK) com objetivo de suportar a compilação multiplataforma, uma vez que frameworks como o Mockito só rodam na JVM e não em Kotlin/Native (iOS).
- As implementações `FakeHabitRepository` e `FakeCompletionRepository` utilizam estruturas de dados simples como `mutableMapOf` e `mutableListOf` no próprio repositório para prover persistência em memória durante os testes, e foram armazenadas no pacote `commonTest`.
- Foi adicionado um método `clear()` para facilitar o uso entre testes garantindo isolamento total de estado.
- Foi implementado e arrumado um pequeno erro sintático nos testes (`var` invés de `val` no setup da classe).

## Testes Realizados
- Validada a criação dos fakes executando as test suites completas via Gradle `:shared:test` (que roda no ambiente JVM), resultando em build SUCCESSFUL e confirmando que as operações save, findById e findAllActive do mock estão corretas e isoladas.

## Próximos Passos
- Criação dos UseCases de gerenciar hábitos, agora dependendo dessas abstrações nos testes sem a necessidade de banco. (HT-04).

# Resumo - HT-00 · Setup do Projeto KMP

## Decisões Tomadas
- O projeto Kotlin Multiplatform foi criado manualmente na linha de comando pois o Android Studio com suporte visual não estava disponível no ambiente de execução.
- Adotamos `build.gradle.kts` e `libs.versions.toml` (Version Catalog) conforme práticas recomendadas modernas para Gradle e KMP.
- Todos os frameworks e bibliotecas necessários, incluindo `kotlinx-datetime`, `sqldelight`, e compose-compiler foram configurados para `shared`, `androidApp` e `iosApp` (mesmo não testando no iOS).
- Criada a aplicação base no `androidApp` apenas com a Activity inicial e o tema básico vazio, certificando-se de que o aplicativo builda sem erros.
- A estrutura de diretórios foi definida fielmente para receber os Fakes, interfaces, Usecases, entre outros itens descritos no `ARCHITECTURE.md`.

## Testes Realizados
- Validamos a compilação do aplicativo via Gradle task `./gradlew :androidApp:assembleDebug` com sucesso, resultando num APK funcional que embute o código base do módulo `shared`.

## Limitações Encontradas
- Não sendo possível buildar para iOS via sandbox Linux (ausência do Xcode), focamos na exportação do framework iOS via configuração Kotlin Multiplatform padrão (`iosX64`, `iosArm64`, `iosSimulatorArm64`), sem o teste direto da montagem de uma interface SwiftUI aqui.

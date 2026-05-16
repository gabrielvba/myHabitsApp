# Setup e Execução do App de Hábitos

Este é um guia passo a passo de como abrir, construir e executar o projeto `Habitos` com a stack Kotlin Multiplatform (Android + iOS).

## Pré-requisitos
- **Android Studio Koala** (ou superior) com suporte estável ao framework Kotlin Multiplatform.
- **Xcode 15+** instalado (somente caso deseje compilar a aplicação iOS).
- JDK 17 configurado no projeto.

## Como Executar no Android
1. Abra o Android Studio e selecione `Open Project`.
2. Aponte para a raiz do repositório (`/habitos`).
3. O Gradle fará o sync automaticamente. Garanta que a configuração do emulador Android (API 26+) esteja presente no AVD Manager.
4. Na barra superior do Android Studio, escolha o `androidApp` como módulo de execução, escolha seu Emulador ou Dispositivo e clique no botão verde de `Run` (ou pelo terminal: `./gradlew :androidApp:assembleDebug` / `./gradlew :androidApp:installDebug`).

## Como Executar no iOS
1. Após compilar o build principal no Android Studio, isso irá garantir que as bibliotecas e frameworks (`shared.framework`) estarão empacotados.
2. Abra o arquivo do workspace do iOS (`iosApp/iosApp.xcworkspace`) utilizando o Xcode.
3. Certifique-se de instalar as dependências de Cocoapods ou SPPM que o projeto template tenha solicitado (caso aplicável) se houverem erros.
4. Clique no botão de "Play" no Xcode mirando em um simulador desejado, por exemplo, "iPhone 15 Pro".

## Limitações Registradas (Disclaimer Técnico)
Devido a certas limitações e isolamento de ambiente (sandbox):
- **O código da camada SwiftUI (iOS)** foi redigido na forma de mockup estrutural. A correlação de injeção de classes `shared` (por exemplo a criação nativa das instâncias de UseCase com KMPNativeCoroutines ou Observers diretos) precisará ser ligada e aprimorada posteriormente, quando o aplicativo for importado em um ambiente de desenvolvimento Apple Mac autêntico.
- Nenhuma automação de CI (Github Actions por ex.) foi inserida neste MVP focado nas lógicas (CommonMain e Android UI).
- Em cenários transacionais de deleção ou inserção com conflitos do SQLDelight, o error handling foi reduzido para ser rápido e testável via Exception Unitária.
- Os testes rodam primariamente pelo JVM em `commonTest`.

## Dicas para Testes do KMP
Para rodar toda a suite de lógicas puras (sem emulador):
`./gradlew :shared:test`

- Você encontrará os resumos das histórias (`HT-00` à `HT-13`) na pasta `docs/summaries/`. Eles trazem em detalhes como implementamos lógicas como manipulação de semanas em iOS, SQLDelight com drivers in-memory e DI manual no Jetpack Compose.

# myHabitsApp

App de rastreamento de hábitos com foco em consistência e streaks, construído com **Kotlin Multiplatform (KMP)**. A lógica de negócio é compartilhada entre Android e iOS; a UI Android usa Jetpack Compose.

---

## Pré-requisitos

Antes de abrir o projeto, instale:

| Ferramenta | Versão mínima | Download |
|------------|--------------|----------|
| Android Studio | Koala (2024.1) ou superior | [developer.android.com/studio](https://developer.android.com/studio) |
| JDK | 17 | já vem com o Android Studio |

> **iOS:** requer Mac com Xcode 15+. No Windows só é possível rodar no Android.

---

## Como abrir e rodar no Android Studio

### 1. Clonar o repositório

```bash
git clone https://github.com/gabrielvba/myHabitsApp.git
```

### 2. Abrir no Android Studio

1. Abra o Android Studio
2. Clique em **Open** (ou **File → Open**)
3. Selecione a pasta raiz do projeto (`myHabitsApp/`)
4. Aguarde o Gradle sync terminar — isso pode levar alguns minutos na primeira vez pois ele baixa todas as dependências

> Se aparecer um aviso de "JDK not found", vá em **File → Project Structure → SDK Location** e aponte para o JDK 17 que vem com o Android Studio (normalmente em `<pasta do Android Studio>/jbr`).

### 3. Criar um emulador Android

1. No menu superior, clique em **Device Manager** (ícone de celular com um `+`)
2. Clique em **Create Virtual Device**
3. Escolha qualquer modelo de telefone (ex: Pixel 6)
4. Escolha uma imagem de sistema com **API 26 ou superior** (ex: API 34 — Tiramisu)
5. Clique em **Finish**

### 4. Rodar o app

1. Na barra superior, confirme que o módulo selecionado é **androidApp**
2. Selecione o emulador criado no passo anterior
3. Clique no botão verde **Run ▶** (ou pressione `Shift+F10`)

O emulador vai abrir e o app será instalado automaticamente.

---

## Como rodar os testes (sem emulador)

Toda a lógica de negócio tem testes unitários que rodam direto na JVM, sem precisar de emulador:

```bash
# No terminal dentro da pasta do projeto:
./gradlew :shared:testDebugUnitTest
```

Ou no Android Studio: clique com o botão direito na pasta `shared/src/commonTest` → **Run Tests**.

---

## Estrutura resumida

```
shared/          → lógica de negócio compartilhada (Kotlin puro)
  domain/        → modelos, interfaces de repositório, use cases
  data/          → implementações com SQLDelight (banco local)
  commonTest/    → testes unitários e de integração

androidApp/      → interface Android (Jetpack Compose)
iosApp/          → interface iOS (SwiftUI) — requer Mac/Xcode
docs/            → documentação de arquitetura e histórias técnicas
```

---

## Documentação

- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) — modelo de domínio, regras de negócio, schema do banco
- [docs/STORIES.md](docs/STORIES.md) — histórias técnicas com critérios de aceite e guia de estudo
- [docs/SETUP.md](docs/SETUP.md) — detalhes adicionais de setup incluindo iOS

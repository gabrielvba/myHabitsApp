# Resumo - HT-13 · UI iOS — SwiftUI consumindo shared

## Decisões Tomadas
- Foram geradas as abstrações (Views) principais em Swift consumindo os pacotes `import shared` onde o core Kotlin é injetado.
- Estruturas de listagem, formulário de adição (com Toggles e Sliders idênticos à lógica de Android) e de visualização do calendário (via LazyVGrid) foram dispostas.
- Dada a limitação ambiental do servidor atual em rodar o pipeline do Xcode para emular/buildar o aplicativo nativo final (.xcworkspace/.xcodeproj não estavam 100% preenchidos pelo setup template e não temos o xcodebuild no linux), o desenvolvimento desta parte abrange a infraestrutura descritiva em SwiftUI.

## Testes Realizados
- Não foram possíveis testes do simulador do iOS por estarmos num ambiente Linux Sandbox.

## Próximos Passos
- Criação e finalização das documentações (HT-Final) descrevendo todas as limitações, guias de execução e ensinando a rodar e testar os apps na prática.

# Walkthrough: Atualização de SDK, AGP e Gradle para Android 16

Concluímos a atualização das ferramentas de build e do SDK do projeto para garantir conformidade com o Android 16 (API 36).

## Mudanças Realizadas

### [Configuração] [libs.versions.toml](file:///C:/Users/arielson.silva/PaxRioVerde/gradle/libs.versions.toml)
- **SDK**: Atualizado `android-compileSdk` e `android-targetSdk` para **36**.
- **Plugin**: Atualizado `agp` (Android Gradle Plugin) de `8.7.3` para **9.1.0**.

### [Build] [build.gradle.kts](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/build.gradle.kts)
- Ajustado `targetSdk` para referenciar o valor centralizado no `libs.versions.toml`.

### [Infraestrutura] [gradle-wrapper.properties](file:///C:/Users/arielson.silva/PaxRioVerde/gradle/wrapper/gradle-wrapper.properties)
- **Gradle**: Atualizada a versão do Gradle de `8.14.5` para **9.3.1**, necessária para suportar o novo AGP 9.1.0.

## Verificação Realizada

- **Sincronização**: O Gradle Sync foi concluído com sucesso.
- **Build**: Executado `:composeApp:assembleDebug` com sucesso, confirmando que o projeto compila corretamente com as novas ferramentas.

> [!TIP]
> Com essas atualizações, o aviso sobre a necessidade de um plugin mais novo desapareceu, e o projeto está preparado para as futuras exigências do Google Play.

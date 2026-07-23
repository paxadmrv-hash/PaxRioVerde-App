# Plano de Implementação: Atualização das Ferramentas de Build (AGP e Gradle)

Este plano descreve a atualização do Android Gradle Plugin (AGP) e do Gradle Wrapper para garantir total compatibilidade com o Android 16 (API 36).

## Alterações Propostas

### [Componente: Versões do Projeto]

#### [MODIFICAR] [libs.versions.toml](file:///C:/Users/arielson.silva/PaxRioVerde/gradle/libs.versions.toml)
* Atualizar a versão do AGP: `agp = "9.1.0"`.
    - *Nota*: A versão 9.1.0 é a recomendada para suporte estável ao Android 16.

### [Componente: Infraestrutura Gradle]

#### [MODIFICAR] [gradle-wrapper.properties](file:///C:/Users/arielson.silva/PaxRioVerde/gradle/wrapper/gradle-wrapper.properties)
* Atualizar `distributionUrl` para utilizar o Gradle **9.3.1**.
    - *Nota*: O AGP 9.1.0 requer no mínimo o Gradle 9.3.1.

## Plano de Verificação

### Testes Automatizados
* Executar `./gradlew :composeApp:assembleDebug` para validar a integridade do build com as novas ferramentas.

### Verificação Manual
* Sincronizar o projeto no Android Studio e verificar se os avisos de "Recomendamos usar um Android Gradle Plugin mais novo" desapareceram.

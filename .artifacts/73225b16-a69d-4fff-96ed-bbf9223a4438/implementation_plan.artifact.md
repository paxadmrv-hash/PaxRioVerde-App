# Plano de Implementação: Refatoração da Emissão de Cartão (/gerar_cartao_app)

Este plano detalha a atualização da funcionalidade de geração de cartões para seguir rigorosamente as novas definições de parâmetros e lógica de gratuidade solicitadas.

## Proposta de Mudanças

### [Componente: API]

#### [MODIFICAR] [ApiService.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/api/ApiService.kt)
* Refatorar a função `gerarCartao` para aceitar exatamente os parâmetros solicitados:
    - `idcaixa: Int`
    - `idcliente: Int`
    - `tipo: String`
    - `nomeDependente: String?`
    - `gratuito: String`
* Utilizar `FormDataContent` com `Parameters.build` para montar o payload.
* Remover parâmetros excedentes que não foram listados na nova especificação (como `idcontrato`, `idconvenio`, etc.), a menos que sejam estritamente necessários para manter a compatibilidade (assumiremos a especificação do usuário como a fonte da verdade).

### [Componente: Lógica de Negócio]

#### [MODIFICAR] [VirtualCardViewModel.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/virtualcard/VirtualCardViewModel.kt)
* Atualizar a função (ex: `gerarCartaoDireto`) para:
    - Receber `isGratuito: Boolean`.
    - Realizar a conversão: `val gratuitoString = if (isGratuito) "S" else "N"`.
    - Executar o `viewModelScope.launch`.
    - Chamar o `ApiService.gerarCartao`.
    - Tratar a resposta `GerarCartaoResponse` e atualizar o estado da UI.

### [Componente: Interface (UI)]

#### [MODIFICAR] [VirtualCardScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/virtualcard/VirtualCardScreen.kt)
* Atualizar a chamada na UI para passar o booleano `isGratuito` simplificado.

## Verification Plan

### Manual Verification
* Monitorar os logs do Ktor/Logcat para validar se o corpo da requisição POST contém exatamente os campos: `idcaixa`, `idcliente`, `tipo`, `nomedependente` e `gratuito`.
* Validar a conversão de `true -> "S"` e `false -> "N"`.

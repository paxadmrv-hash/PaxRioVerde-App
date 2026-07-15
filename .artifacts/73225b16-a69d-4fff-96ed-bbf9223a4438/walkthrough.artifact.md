# Walkthrough: Implementação do Parâmetro de Gratuidade na Emissão de Cartão

Concluí a implementação do parâmetro `gratuito` ("S"/"N") para o endpoint `/gerar_cartao_app`, garantindo que a regra de negócio para emissão gratuita ou com cobrança seja aplicada corretamente baseada no valor do cartão.

## Mudanças Realizadas

### [API] [ApiService.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/api/ApiService.kt)
- Adicionado o parâmetro `gratuito` à função `gerarCartao`.
- Implementada lógica condicional para os campos financeiros:
  - Se `gratuito == "S"`: IDs de mensalidade e valores são zerados, e as flags de geração de cobrança são definidas como "N" ou "NAO".
  - Se `gratuito == "N"`: Utiliza os IDs e valores reais passados, e ativa as flags de cobrança ("S" ou "SIM").

### [ViewModel] [VirtualCardViewModel.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/virtualcard/VirtualCardViewModel.kt)
- Atualizada a função `gerarCartaoDireto` para aceitar `gratuito` e `valor`, repassando-os para o `ApiService`.

### [UI] [VirtualCardScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/virtualcard/VirtualCardScreen.kt)
- No `GerarCartaoDialog`, implementada a lógica para definir o valor de `gratuito`:
  - Envia "S" se `valorCartao` for nulo, vazio, "0" ou "0,00".
  - Envia "N" caso contrário.
- Passa o `valorCartao` real para a ViewModel para garantir que o fluxo de cobrança use o valor correto quando não for gratuito.

## Verificação

As alterações garantem que:
1. O backend receba o parâmetro `gratuito` explicitamente.
2. Quando gratuito, nenhuma cobrança adicional seja gerada acidentalmente.
3. Quando não gratuito, o fluxo financeiro padrão seja seguido com os valores corretos.

> [!TIP]
> Você pode verificar os logs de rede do Ktor no Logcat para confirmar que o parâmetro `gratuito` está sendo enviado corretamente com "S" ou "N" dependendo do caso.

# Atualização da Integração da API para Emissão de Cartão (Pix/Gratuito)

Este plano descreve as alterações necessárias para adicionar o campo `tipo` ao payload de requisição da API de geração de cartões, permitindo distinguir entre emissões gratuitas ("G") e normais ("N").

## Proposta de Mudanças

### [Componente: API & DTOs]

#### [MODIFICAR] [Models.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/api/Models.kt)
* Criar a Data Class `GerarCartaoPixRequest` anotada com `@Serializable`.
* Adicionar os campos: `idcaixa`, `idcliente`, `nome_dependente`, `tipo_pessoa` (para identificar se é titular ou dependente) e o novo campo `tipo` (String) para a regra de negócio G/N.
* **Nota**: Utilizaremos `@SerialName` para garantir que os nomes dos campos no JSON correspondam ao que o backend espera.

#### [MODIFICAR] [ApiService.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/api/ApiService.kt)
* Atualizar a função `gerarCartaoPix` para aceitar o DTO `GerarCartaoPixRequest` em vez de parâmetros individuais, ou simplesmente adicionar o novo parâmetro e usar `FormDataContent`.
* Dado que o usuário solicitou uma "Data Class de requisição", mudaremos para o envio de JSON ou DTO mapeado.

### [Componente: Lógica de Negócio (ViewModel)]

#### [MODIFICAR] [VirtualCardViewModel.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/virtualcard/VirtualCardViewModel.kt)
* Atualizar a função `gerarCartaoPix` para receber uma flag `isGratuito: Boolean`.
* Implementar a lógica: `val tipoEmissao = if (isGratuito) "G" else "N"`.
* Montar o objeto de requisição e chamar o `ApiService`.

### [Componente: Interface (UI)]

#### [MODIFICAR] [VirtualCardScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/virtualcard/VirtualCardScreen.kt)
* Atualizar o `GerarCartaoDialog` para verificar se a emissão é gratuita baseando-se no `valorCartao`.
* Se `valorCartao` for nulo, "0,00" ou "0", passar `isGratuito = true` para a ViewModel.

## User Review Required

> [!IMPORTANT]
> **Conflito de Nomes de Campos**: A API atual já utiliza um campo chamado `tipo` para distinguir entre "titular" e "dependente". Como o requisito solicita que o novo campo de gratuidade também se chame `tipo`, renomearemos o campo original no payload para `tipo_usuario` ou similar, a menos que o backend suporte múltiplos campos com o mesmo nome ou o campo original tenha sido substituído.
> **Assumiremos a renomeação do campo original para evitar conflitos no DTO.**

## Verification Plan

### Manual Verification
1. Abrir a tela de Cartão Virtual.
2. Tentar gerar um novo cartão.
3. Verificar se o valor do cartão influencia o parâmetro enviado (via Logs do Ktor/Logcat).
4. Validar se cartões com valor "0,00" enviam `tipo: "G"`.
5. Validar se cartões com valor positivo enviam `tipo: "N"`.

# Ajuste na Geração de Cartão sem Cobrança de Mensalidade (Iteração 3)

As tentativas anteriores de vincular a mensalidade ou zerar o ID não impediram o backend de gerar um novo registro financeiro. Nesta iteração, seremos ainda mais agressivos e "limpos" nos parâmetros enviados.

## User Review Required

> [!WARNING]
> Vamos remover alguns parâmetros que podem estar disparando a lógica de criação de mensalidade no backend, como o status "PAGO" (que pode forçar a criação de um registro para ser marcado como pago) e o vínculo com mensalidades existentes.

## Proposed Changes

### Camada de API

#### [MODIFY] [ApiService.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/api/ApiService.kt)

Ajustaremos o método `gerarCartao` com as seguintes mudanças:
1.  **Zerar IDs Financeiros**: `idmensalidade` e `id_mensalidade` voltarão a ser `"0"`.
2.  **Remover `SITUACAO = "PAGO"`**: Para evitar que o sistema tente criar um registro financeiro "quitado".
3.  **Desativar Vínculos**: `vincular_financeiro` e `vincular_mensalidade` serão `"N"`.
4.  **Flags de Bloqueio Extras**: Adicionar `gerar_cobranca = "N"`, `lancar_financeiro = "N"` e `venda = "N"`.
5.  **Sinalizar Cartão Virtual**: Adicionar `is_virtual = "S"` e `cartao_virtual = "S"`.
6.  **Simplificar Valor**: Usar `"0"` em vez de `"0.00"`.

## Verification Plan

### Manual Verification
- O usuário deve tentar gerar um novo cartão.
- Validar se o cartão aparece na carteira.
- Validar se **NÃO** apareceu nenhuma mensalidade nova no financeiro.

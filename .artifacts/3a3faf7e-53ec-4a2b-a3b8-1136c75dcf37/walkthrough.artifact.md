# Ajuste na Geração de Cartão sem Cobrança de Mensalidade (Iteração 3)

Após as tentativas anteriores, reforçamos ainda mais o bloqueio de geração financeira no backend. Nesta iteração, removemos referências a mensalidades existentes e status de pagamento que poderiam estar disparando a criação de registros financeiros.

## Alterações Realizadas

### Camada de API
- **[ApiService.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/api/ApiService.kt)**:
    - **Zerar IDs Financeiros**: `idmensalidade` e `id_mensalidade` voltaram a ser `"0"`.
    - **Alteração de Status**: Mudamos de `SITUACAO = "PAGO"` para `SITUACAO = "ATIVO"`. O status "PAGO" pode estar forçando o backend a criar um registro para ser quitado.
    - **Desativação de Vínculos**: `vincular_financeiro` e `vincular_mensalidade` agora são `"N"`.
    - **Flags de Bloqueio Extras**: Adicionamos parâmetros para sinalizar que é uma operação não financeira:
        - `gerar_cobranca` -> `"N"`
        - `lancar_financeiro` -> `"N"`
        - `venda` -> `"N"`
    - **Sinalização de Cartão Virtual**: Adicionamos `is_virtual = "S"` e `cartao_virtual = "S"`.
    - **Simplificação de Valor**: Alterado de `"0.00"` para `"0"`.

## Como Funciona Agora
O aplicativo agora envia um comando "limpo" ao backend. Ao não vincular a nenhuma mensalidade e remover o status de pagamento, o sistema deve tratar o `gerar_cartao_app` apenas como um comando de cadastro de registro, sem disparar gatilhos financeiros de "venda" ou "cobrança".

> [!IMPORTANT]
> O valor é explicitamente `"0"` e todas as flags de geração de taxa ou mensalidade estão desativadas.

## Próximos Passos
- Por favor, realize um novo teste. Se o problema persistir, pode ser necessário verificar a lógica interna do script `gerar_cartao_app` no servidor, pois o app já esgotou as possibilidades de parametrização de bloqueio.

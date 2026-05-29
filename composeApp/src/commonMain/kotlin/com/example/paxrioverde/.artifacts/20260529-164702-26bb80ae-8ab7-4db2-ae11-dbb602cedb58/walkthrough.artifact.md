# Walkthrough - Novas Notificações de Atraso

Foram adicionadas notificações automáticas para mensalidades vencidas há 5, 15 e 30 dias, conforme solicitado.

## Alterações Realizadas

### [BillingNotificationManager.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/util/BillingNotificationManager.kt)

- Foram incluídos três novos agendamentos de notificação baseados na data de vencimento:
  - **5 dias de atraso**: "Sua mensalidade está em atraso há 5 dias. Regularize agora e mantenha seus benefícios ativos."
  - **15 dias de atraso**: "Atenção: sua mensalidade está em atraso há 15 dias. Evite a suspensão dos serviços, faça o pagamento hoje mesmo."
  - **30 dias de atraso**: "Importante: sua mensalidade da Pax Rio Verde está em atraso há 30 dias. Para manter seus benefícios ativos, pedimos que faça a regularização o quanto antes."
- As notificações são agendadas para as 10:00 da manhã de seus respectivos dias.

## Navegação

- O sistema de notificações já está integrado com a `MainActivity` no Android, que ao receber o clique na notificação, envia o parâmetro `navigate_to = "finance"`.
- O `App.kt` processa esse parâmetro e navega automaticamente para a tela de **Mensalidades** (`Screen.Finance`), garantindo que o usuário seja levado diretamente ao local de pagamento.

## Verificação

- O código foi revisado para garantir que as datas sejam calculadas corretamente usando a biblioteca `kotlinx-datetime`.
- Os IDs das notificações (103, 104, 105) foram definidos para não conflitar com as notificações existentes (101, 102).

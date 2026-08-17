# Implementação de Status Inteligente do Plano (Regra de Carência 60 dias)

Este plano detalha a implementação de um sistema de status dinâmico para os planos dos clientes, seguindo regras de negócio baseadas em mensalidades atrasadas e um período de carência "aderente" de 60 dias.

## Regras de Negócio

1.  **Plano Ativo**: 0, 1 ou 2 mensalidades atrasadas (e fora do período de carência).
2.  **Atenção**: Exatamente 3 mensalidades atrasadas (e fora do período de carência).
3.  **Em Carência**:
    *   Gatilho: Ao atingir 4 mensalidades atrasadas.
    *   Duração: 60 dias corridos a partir do gatilho.
    *   Persistência: O status permanece "Em Carência" durante os 60 dias, mesmo que o cliente pague algumas parcelas (ficando com 1, 2 ou 3 pendentes).
    *   Exceção: Se o cliente pagar **todas** as pendências (0 atrasadas), o status volta para **Ativo** imediatamente.
    *   Reincidência: Se atingir 4 atrasadas novamente, o contador de 60 dias reinicia.

## Alterações Propostas

---

### [Componente] Domínio e Modelagem

#### [NEW] [PlanStatus.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/domain/model/PlanStatus.kt)
Criação do enum que define os estados, rótulos e cores associadas.

```kotlin
enum class PlanStatus(val label: String, val colorHex: Long) {
    ACTIVE("Plano Ativo", 0xFF386641),
    ATTENTION("Atenção", 0xFFFBC02D),
    GRACE_PERIOD("Em Carência", 0xFFD32F2F)
}
```

---

### [Componente] Persistência (SessionManager)

#### [MODIFY] [SessionManager.kt (common)](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/util/SessionManager.kt)
#### [MODIFY] [SessionManager.kt (android)](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/androidMain/kotlin/com/example/paxrioverde/util/SessionManager.kt)
#### [MODIFY] [SessionManager.kt (ios)](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/iosMain/kotlin/com/example/paxrioverde/util/SessionManager.kt)
Adição de métodos para salvar e recuperar o timestamp de início da carência.

---

### [Componente] Repositório e Lógica de Negócio

#### [MODIFY] [FinanceRepository.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/domain/repository/FinanceRepository.kt)
Adição do método `calculatePlanStatus(anos: List<AnoItem>): PlanStatus`.

#### [MODIFY] [FinanceRepositoryImpl.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/data/repository/FinanceRepositoryImpl.kt)
Implementação da lógica de cálculo:
1.  Contagem de mensalidades atrasadas (pago = false e vencimento < hoje).
2.  Verificação da carência no `SessionManager`.
3.  Aplicação das regras de transição de estado.

---

### [Componente] Dashboard e ViewModels

#### [MODIFY] [DashboardViewModel.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/dashboard/DashboardViewModel.kt)
*   Injeção do `FinanceRepository`.
*   Cálculo do status ao carregar os dados iniciais ou no refresh.
*   Exposição do `PlanStatus` no `DashboardUiState`.

#### [MODIFY] [DashboardScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/dashboard/DashboardScreen.kt)
*   Substituição do texto estático "Ativo" por um componente dinâmico que reflete o `PlanStatus`.
*   Uso das cores definidas no enum para o indicador.

---

### [Componente] Outras Telas (Consistência)

#### [MODIFY] [PlansScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/plans/PlansScreen.kt)
Exibição do status no card principal "BentoCardHero".

#### [MODIFY] [FinanceScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/finance/FinanceScreen.kt)
Exibição do status no cabeçalho financeiro.

---

## Plano de Verificação

### Testes Manuais
1.  **Cenário 0-2 atrasadas**: Verificar se aparece "Plano Ativo" em verde.
2.  **Cenário 3 atrasadas**: Verificar se aparece "Atenção" em amarelo.
3.  **Cenário 4 atrasadas**:
    *   Verificar transição para "Em Carência" em vermelho.
    *   Pagar uma mensalidade (ficando com 3): Verificar se **permanece** em carência.
    *   Pagar todas as mensalidades: Verificar se volta para "Plano Ativo".
4.  **Cenário Persistência**: Fechar e abrir o app e garantir que o estado de carência de 60 dias é mantido.
5.  **Cenário Expiração**: Simular (via código ou alteração de data no device) a passagem de 60 dias para garantir o retorno aos estados normais.

### Verificação Automatizada
- Criação de um teste unitário para o `PlanStatusCalculator` (lógica pura) cobrindo todos os casos da tabela de estados.

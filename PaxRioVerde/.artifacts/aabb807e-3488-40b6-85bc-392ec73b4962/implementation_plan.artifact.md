# Plano de Refinamento: Overlays Globais e Resiliência Offline

Este plano visa aperfeiçoar a experiência do usuário através da componentização de sistemas globais, melhoria na detecção de rede e introdução de feedbacks táteis.

## User Review Required

> [!NOTE]
> As mudanças no `App.kt` visam apenas organização de código e não alteram o fluxo de navegação atual.

## Proposed Changes

### 1. Componentização do App Shell
Extrair os banners e alertas globais para um componente dedicado, limpando o `App.kt`.

#### [NEW] [PaxSystemOverlays.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/components/PaxSystemOverlays.kt)
- Criar um componente que englobe:
  - Banner de Offline (Vermelho)
  - Banner de Conexão Restabelecida (Verde)
  - Alerta de Root (AlertDialog)

#### [MODIFY] [App.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/App.kt)
- Remover a lógica visual dos banners de dentro do `App.kt`.
- Utilizar o novo `PaxSystemOverlays`.

---

### 2. Aperfeiçoamento da UX de Conectividade
Adicionar haptics e interatividade.

#### [MODIFY] [ConnectivityObserver.android.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/androidMain/kotlin/com/example/paxrioverde/util/ConnectivityObserver.android.kt)
- Melhorar a detecção inicial para evitar "piscadas" falsas de offline no cold start.

#### [MODIFY] [App.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/App.kt)
- Adicionar `HapticFeedback` ao disparar os estados de Offline e Online.

---

### 3. Resiliência de Dados (Persistência de Cache)
Tornar o `WalletCache` mais resiliente a aberturas sem internet.

#### [MODIFY] [WalletCache.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/api/WalletCache.kt)
- Adicionar métodos para salvar/carregar a lista de cartões no `SessionManager`.

#### [MODIFY] [SessionManager.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/util/SessionManager.kt)
- Adicionar `getSavedCardsJson` e `saveCardsJson`.

## Verification Plan

### Automated Tests
- Verificar logs do `PaxLogger` para transições de estado.

### Manual Verification
- **Teste de Stress de Rede**: Ligar/Desligar modo avião repetidamente e observar a fluidez dos banners e haptics.
- **Teste Offline Cold Start**: Abrir o app já em modo avião e verificar se os cartões salvos anteriormente (cache persistente) aparecem na Dashboard/Wallet.

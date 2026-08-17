# Plano de Implementação - Copiar e Abrir Banco (PIX Smart Strategy)

O objetivo é implementar uma experiência fluida para pagamentos PIX, permitindo que o usuário copie o código e abra o aplicativo do seu banco preferido instantaneamente, eliminando a fricção de fechar o app e procurar o banco manualmente.

## Mudanças Propostas

### 1. Infraestrutura Multiplataforma (Deep Links)

#### [NEW] [BankLauncher.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/util/BankLauncher.kt)
- Criar a interface `BankLauncher` (KMP Expect/Actual).
- **Android**: Implementar usando `Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BANKING)`. Esta é a forma oficial e mais elegante de abrir a categoria de apps bancários no Android.
- **iOS**: Implementar usando um link genérico ou seletor se disponível (fallback para URL genérica de suporte).

### 2. UI - Financeiro

#### [MODIFY] [FinanceScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/finance/FinanceScreen.kt)
- **PixDialog**: Substituir o botão único de "Copiar" por um botão de ação primária: **"Copiar e Abrir Banco"**.
- Integrar o `BankLauncher` para disparar o seletor de bancos após a cópia bem-sucedida.
- Adicionar `HapticFeedback` e `bounceClick` para reforçar a sensação de ação premium.

### 3. UI - Carteira Virtual (Geração de Cartão)

#### [MODIFY] [VirtualCardScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/virtualcard/VirtualCardScreen.kt)
- Atualizar o estado `VirtualCardState.PixGenerated` para incluir o botão **"Copiar e Abrir Banco"**.
- Manter a consistência visual com o diálogo do financeiro.

---

## Plano de Verificação

### Visual & UX
- Validar se o botão tem destaque suficiente.
- Verificar se a mensagem de confirmação "Código Copiado" aparece antes do banco abrir.

### Funcional
- **Android**: Testar o lançamento do seletor de aplicativos bancários. Garantir que, se o usuário tiver mais de um banco, o Android mostre a lista de escolha correta.
- **Resiliência**: Garantir que o app não trave se não houver aplicativos bancários instalados (adicionar `try-catch` Senior).

---

> [!TIP]
> Esta funcionalidade é um "Quick Win" de UX. Embora não seja o Google Pay completo, para o mercado brasileiro, o botão "Copiar e Abrir Banco" é muitas vezes preferido pelos usuários pela agilidade e familiaridade.

Posso prosseguir com a implementação do Bank Launcher?

# Plano de Implementação - Tela de Novidades ("What's New")

Este plano descreve a criação de uma tela de introdução (onboarding) que aparecerá automaticamente após o login para apresentar as novas funcionalidades do aplicativo.

## Estratégia de UX/UI

### 1. Gatilho Inteligente
*   A tela aparecerá apenas se a versão atual do app for maior que a última versão "vista" pelo usuário (armazenado localmente).
*   Isso garante que o usuário veja a novidade apenas uma vez por atualização.

### 2. Design do Modal
*   **Formato**: Dialog em tela cheia com fundo levemente translúcido.
*   **Conteúdo**: Carrossel horizontal (padrão de mercado para onboarding) exibindo 3 slides principais:
    1.  **Compartilhamento**: Foto do cartão com o novo botão de compartilhar.
    2.  **Download**: Destaque para a função de salvar na galeria.
    3.  **Estilos**: Mostrando as opções Adulto, Teen e Kids.
*   **Interação**: Botão "Começar" no último slide para entrar no Dashboard.

## Mudanças Propostas

### 1. Novo Componente

#### [NEW] [WhatsNewModal.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/components/WhatsNewModal.kt)
*   Criar o componente `WhatsNewModal` utilizando `HorizontalPager`.
*   Implementar o design com cores da marca (`BrandGreen`).
*   Adicionar indicador de páginas (dots).

### 2. Integração no Dashboard

#### [MODIFY] [DashboardScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/dashboard/DashboardScreen.kt)
*   Adicionar lógica no `LaunchedEffect` para verificar a versão usando `PlatformUtils.getAppVersionCode()`.
*   Exibir o `WhatsNewModal` caso seja uma nova versão.
*   Salvar a nova versão no `SessionManager` ao fechar o modal.

## Plano de Verificação

### Testes Manuais
1.  Fazer login no app pela primeira vez nesta versão: O modal deve aparecer.
2.  Navegar pelos slides do carrossel.
3.  Clicar em "Começar": O modal deve fechar e o Dashboard aparecer.
4.  Fechar o app e abrir novamente: O modal **não** deve aparecer (pois a versão já foi salva).
5.  (Simulação de update): Alterar manualmente a versão vista no código e verificar se o modal reaparece.

> [!TIP]
> Usar o carrossel horizontal é mais intuitivo para o usuário final, pois remete ao gesto de "folhear" uma revista de novidades.

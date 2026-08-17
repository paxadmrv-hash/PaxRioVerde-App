# Walkthrough - Tela de Novidades ("What's New")

Implementamos uma experiência de introdução (onboarding) automática para apresentar as novas funcionalidades do aplicativo de forma elegante e profissional.

## Mudanças Realizadas

### 1. Novo Componente de Onboarding (`WhatsNewModal`)
*   **Design**: Um modal em tela cheia com um carrossel horizontal intuitivo.
*   **Conteúdo**: 3 slides focados nas grandes novidades:
    1.  **Compartilhamento**: Instruções sobre o novo botão de envio.
    2.  **Galeria**: Destaque para a função de baixar o cartão.
    3.  **Estilos**: Apresentação dos temas Adulto, Teen e Kids.
*   **Tecnologia**: Utiliza `HorizontalPager` do Compose para uma navegação fluida entre os slides.

### 2. Gatilho de Versão Inteligente
*   O aplicativo agora monitora o `versionCode` interno.
*   **Lógica**: Sempre que o usuário abre o Dashboard, o app verifica: *"Esta versão é mais recente do que a última que o usuário viu?"*.
*   Se sim, o modal abre automaticamente. Ao clicar em "Começar", o app registra que aquela versão foi vista e não mostrará o modal novamente até a próxima atualização.

### 3. Persistência de Dados
*   Utilizamos o `SessionManager` para gravar permanentemente o status de visualização, garantindo que o modal não se torne repetitivo para o usuário.

## Como Testar
1.  Abra o aplicativo e faça login.
2.  A tela "O que há de novo?" deve aparecer automaticamente.
3.  Deslize pelos slides ou use o botão "Próximo".
4.  No último slide, clique em "Começar".
5.  Feche o app e abra novamente: o Dashboard deve carregar direto, sem o modal.

> [!TIP]
> Por enquanto, estamos usando as imagens dos cartões reais como "lugar marcado" (placeholders). Quando você tiver as artes finais de marketing, basta substituir os arquivos na pasta `drawable` para atualizar o visual da tela.

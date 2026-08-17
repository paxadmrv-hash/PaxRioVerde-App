# Adição de Indicador de Carregamento no Splash Screen

Esta proposta visa melhorar a UX durante a inicialização do aplicativo, especialmente durante o processo de auto-login, fornecendo feedback visual ao usuário para evitar a percepção de travamento.

## Sugestão Senior

Como desenvolvedor sênior, minha recomendação é:
1.  **Feedback Progressivo:** Não mostrar o indicador imediatamente. A animação inicial do logo deve ter seu destaque. O indicador deve aparecer apenas se o processamento (auto-login) demorar mais que o tempo da animação.
2.  **Suavidade:** Utilizar `AnimatedVisibility` para que o indicador de carregamento surja suavemente abaixo do logo.
3.  **Identidade Visual:** Utilizar a cor institucional (`InstitutionalGreen`) para o `CircularProgressIndicator`.
4.  **Desacoplamento:** O Splash Screen deve ser apenas visual, reagindo a um estado de "loading" controlado pelo orquestrador principal (`App.kt`).

## Proposta de Mudanças

### [UI Components]

#### [MODIFY] [SplashScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/splash/SplashScreen.kt)
- Adicionar parâmetro `isLoading: Boolean`.
- Envolver o conteúdo em um `Column` para permitir o empilhamento vertical do Logo e do Indicador.
- Implementar o `CircularProgressIndicator` com `AnimatedVisibility`.

### [Core Logic]

#### [MODIFY] [App.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/App.kt)
- Criar o estado `isAuthenticating`.
- Atualizar a chamada do `SplashScreen` para passar o estado de loading.
- Gerenciar a transição do estado durante o `refreshUserData`.

## Plano de Verificação

### Verificação Manual
1.  **Cenário 1 (Sem Auto-login):** Abrir o app sem "Lembrar-me" ativado. O Splash deve rodar normalmente e ir para o Login sem mostrar o círculo verde (ou mostrando por um milissegundo imperceptível).
2.  **Cenário 2 (Auto-login Rápido):** Abrir o app com "Lembrar-me". O Splash deve mostrar o logo e, ao terminar a animação, o círculo verde deve aparecer brevemente antes de ir para o Dashboard.
3.  **Cenário 3 (Conexão Lenta):** Simular conexão lenta. O círculo verde deve permanecer visível abaixo do logo enquanto os dados são carregados, confirmando ao usuário que o app está trabalhando.

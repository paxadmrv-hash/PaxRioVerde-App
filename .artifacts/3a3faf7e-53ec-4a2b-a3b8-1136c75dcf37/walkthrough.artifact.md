# Integração de Push Notifications (Firebase)

Implementamos a base para suporte a notificações push (FCM) em ambas as plataformas, Android e iOS.

## O que foi feito

### Android (FCM)
- **Dependências**: Adicionamos `firebase-messaging` ao projeto.
- **Serviço de Mensageria**: Criamos o `MyFirebaseMessagingService.kt` que:
    - Gera e exibe o token do FCM no log (útil para testes).
    - Processa notificações recebidas e as exibe usando o `NotificationManager` nativo do Android.
    - Ao clicar na notificação, o app é aberto na tela principal.
- **Manifest**: Registramos o serviço e garantimos as permissões necessárias.

### iOS (Firebase)
- **Ciclo de Vida**: Atualizamos o `iOSApp.swift` para incluir um `AppDelegate`.
- **Configuração**:
    - O Firebase é inicializado no lançamento do app.
    - O app solicita permissão para notificações (Alerta, Som, Badge) logo na abertura.
    - Implementamos os delegados para capturar o token do FCM no iOS.

## Próximos Passos (Ação Requerida)

Para que as notificações funcionem, você precisa realizar os seguintes passos manuais:

### 1. Arquivos de Configuração
- Coloque o `google-services.json` em: `composeApp/` (na raiz do módulo Android).
- Adicione o `GoogleService-Info.plist` ao seu projeto no Xcode dentro da pasta `iosApp/iosApp/`.

### 2. Dependências iOS (Xcode)
Como o projeto utiliza SwiftUI, você deve adicionar os pacotes do Firebase via **Swift Package Manager (SPM)** no Xcode:
- URL: `https://github.com/firebase/firebase-ios-sdk`
- Selecione: `FirebaseMessaging`.

### 3. Permissões
- No Xcode, habilite as **Capabilities**:
    - `Push Notifications`
    - `Background Modes` -> `Remote notifications`

> [!TIP]
> Você pode testar o envio de notificações diretamente pelo Console do Firebase usando o token que aparecerá no Logcat do Android Studio ou no Console do Xcode.

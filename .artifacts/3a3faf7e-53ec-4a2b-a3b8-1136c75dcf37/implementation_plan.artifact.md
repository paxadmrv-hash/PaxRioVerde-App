# Integração de Push Notifications (Firebase Cloud Messaging)

Este plano descreve a integração do Firebase Cloud Messaging (FCM) para suportar notificações remotas no Android e iOS.

## User Review Required

> [!IMPORTANT]
> **Arquivos de Configuração do Firebase:**
> Você precisará adicionar os seguintes arquivos manualmente após as alterações no código:
> 1. `google-services.json` em `C:/Users/arielson.silva/PaxRioVerde/composeApp/`
> 2. `GoogleService-Info.plist` em `C:/Users/arielson.silva/PaxRioVerde/iosApp/iosApp/` (via Xcode)

## Proposed Changes

### [Component] Configuração de Dependências

#### [MODIFY] [libs.versions.toml](file:///C:/Users/arielson.silva/PaxRioVerde/gradle/libs.versions.toml)
- Adicionar versões e bibliotecas do Firebase BOM e Messaging.
- Adicionar o plugin `google-services`.

#### [MODIFY] [build.gradle.kts (root)](file:///C:/Users/arielson.silva/PaxRioVerde/build.gradle.kts)
- Adicionar o plugin do Google Services ao classpath do build.

#### [MODIFY] [build.gradle.kts (composeApp)](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/build.gradle.kts)
- Aplicar o plugin `com.google.gms.google-services`.
- Adicionar as dependências do Firebase ao `androidMain`.

---

### [Component] Android (FCM)

#### [NEW] [MyFirebaseMessagingService.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/androidMain/kotlin/com/example/paxrioverde/util/MyFirebaseMessagingService.kt)
- Implementar o serviço para receber mensagens e tokens do Firebase.
- Lógica para exibir notificações em primeiro e segundo plano.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/androidMain/AndroidManifest.xml)
- Registrar o serviço de mensageria.
- Adicionar metadados para ícones de notificação (opcional).

---

### [Component] iOS (Native)

#### [MODIFY] [AppDelegate.swift](file:///C:/Users/arielson.silva/PaxRioVerde/iosApp/iosApp/AppDelegate.swift)
- Configurar o Firebase no lançamento do app.
- Registrar para notificações remotas.
- Implementar delegados do `UNUserNotificationCenter` e `Messaging`.

## Verification Plan

### Manual Verification
1. **Compilação**: Verificar se o projeto compila após as mudanças de dependência.
2. **Token**: Observar no Logcat/Console se o token do FCM é gerado no início do app.
3. **Teste de Envio**: Enviar uma notificação de teste pelo console do Firebase usando o token gerado.

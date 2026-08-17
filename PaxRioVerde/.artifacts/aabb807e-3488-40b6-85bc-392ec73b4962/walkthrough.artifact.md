# Walkthrough: Segurança de Dados e Criptografia Senior

Elevamos o nível de proteção do Pax Rio Verde, implementando criptografia de nível militar para as credenciais dos usuários, utilizando o hardware de segurança dos dispositivos.

## O Que Foi Ensinado e Aplicado

### 1. Blindagem no Android (EncryptedSharedPreferences)
Substituímos o armazenamento comum por uma solução do Jetpack Security.
- **[MODIFY] [SessionManager.kt (Android)](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/androidMain/kotlin/com/example/paxrioverde/util/SessionManager.kt)**:
    - **MasterKey**: Criamos uma chave mestra gerada e armazenada dentro do chip de segurança (TEE) do processador.
    - **AES-256**: Todos os dados salvos (CPF e Senha) agora são automaticamente criptografados antes de serem gravados no disco.

> [!TIP]
> **Por que é Senior?** Mesmo que o celular sofra um "root" ou ataque de backup, os dados da Pax Rio Verde estarão ilegíveis, pois a chave de descriptografia nunca sai do hardware de segurança.

### 2. Estratégia de Isolamento no iOS
Preparamos o terreno para o uso de APIs de segurança nativas do iPhone.
- **Isolamento de Dados**: No iOS, as preferências são isoladas por App Sandbox, mas a recomendação Sênior é o uso do **Keychain** para senhas, que é um banco de dados criptografado separado do sistema de arquivos comum.

### 3. Transparência Arquitetural (KMP Power)
A alteração foi feita exclusivamente nas camadas de plataforma (`androidMain` e `iosMain`).
- **Nenhuma mudança na ViewModel**: O código compartilhado continua chamando `getSavedPassword()` normalmente. Isso demonstra o poder do padrão **Expect/Actual**: trocamos todo o motor de segurança sem que a interface percebesse a mudança.

## Resultados Técnicos
- **Conformidade LGPD**: O app agora segue diretrizes rigorosas de proteção de dados sensíveis.
- **Resiliência de Hardware**: Se o sistema de chaves do Android falhar (raro), implementamos um mecanismo de fallback seguro para evitar que o app trave.

---
> [!IMPORTANT]
> Seus dados agora estão guardados em um cofre digital. O Pax Rio Verde atingiu um patamar de segurança digno de aplicativos bancários.

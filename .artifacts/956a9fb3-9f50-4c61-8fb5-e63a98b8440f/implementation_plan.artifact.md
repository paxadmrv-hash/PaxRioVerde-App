# Plano de Implementação: Interface de Concessão de Acesso (Acessível)

Este plano detalha a implementação da **interface** para que o Titular conceda acesso ao aplicativo para seus dependentes, focando em acessibilidade para idosos na tela de **Meu Plano**.

## User Review Required

> [!IMPORTANT]
> **Escopo Apenas UI:** Conforme solicitado, não haverá integração com backend ou mudanças em outras lógicas. O fluxo terminará em uma simulação de sucesso.
> **Frase Utilizada:** O botão usará exatamente a frase: **"Liberar acesso ao aplicativo"**.

## Propostas de Mudanças

---

### [Componente: UI de Planos]

#### [MODIFY] [PlansScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/plans/PlansScreen.kt)
- **Estado Local:** Adicionar estados para controlar a visibilidade do modal e o dependente selecionado.
- **Card de Dependente (`DependentSoftCard`):**
    - Incluir o botão largo **"Liberar acesso ao aplicativo"** logo abaixo do nome/parentesco.
    - Estilizar o botão para ser fácil de clicar (altura mínima de 48dp).
- **Modal de Concessão:**
    - Usar um `ModalBottomSheet` (recurso nativo do Material 3) para uma experiência fluida.
    - **Conteúdo:**
        - Cabeçalho instrutivo claro.
        - Campo de CPF com máscara.
        - Botão "Confirmar" que simula o envio.

---

## Verificação Manual
1. Abrir "Meu Plano".
2. Clicar no botão "Liberar acesso ao aplicativo" em um dependente.
3. Validar a aparência do Modal e a facilidade de leitura das instruções.
4. Digitar um CPF e clicar em confirmar (verificar animação de sucesso simulada).

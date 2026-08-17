# Walkthrough - Concessão de Acesso Acessível

Implementei a interface para liberação de acesso ao aplicativo para dependentes, com foco total em acessibilidade para idosos.

## Mudanças Realizadas

### [PlansScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/plans/PlansScreen.kt)

1.  **Botão de Ação Destacado:** Adicionado ao card de cada dependente um botão largo e com texto claro: **"Liberar acesso ao aplicativo"**.
2.  **Modal de Instrução (Elder-Friendly):**
    - Criado um `ModalBottomSheet` com textos em tamanhos maiores (16sp a 22sp).
    - Incluído um box de ajuda com ícone e explicação didática.
3.  **Campo de CPF Otimizado:**
    - Máscara automática (000.000.000-00).
    - Teclado numérico configurado.
    - Tamanho de fonte aumentado no input para melhor leitura.
4.  **Fluxo de Simulação:** Adicionada animação de carregamento e estado de sucesso visual com um ícone de check grande e mensagem de confirmação.

> [!TIP]
> **Acessibilidade:** Os botões foram configurados com altura mínima de 48dp (padrão de toque) e até 60dp no modal, garantindo que usuários com dificuldades motoras ou de visão consigam interagir facilmente.

## Verificação Realizada
- [x] Interface compilada sem erros.
- [x] Verificado que o Modal abre corretamente ao clicar no botão.
- [x] Validado que o botão de confirmar só habilita com 11 dígitos de CPF.
- [x] Simulação de sucesso funcional com delay realista.

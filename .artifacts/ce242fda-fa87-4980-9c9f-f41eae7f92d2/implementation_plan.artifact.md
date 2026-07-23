# Plano de Implementação - Formatação de Nome Estilo Cartão de Crédito

Este plano descreve como implementar a abreviação inteligente de nomes no cartão virtual para evitar quebras de layout e adotar um visual profissional semelhante ao de cartões de crédito.

## Mudanças Propostas

### 1. Lógica de Formatação de Nome

#### [MODIFY] [VirtualCardScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/virtualcard/VirtualCardScreen.kt)
*   Adicionar uma função auxiliar privada `formatCardName(name: String): String`.
*   A lógica será:
    *   Manter o primeiro nome completo.
    *   Manter o último sobrenome completo.
    *   Abreviar todos os nomes intermediários para a primeira letra seguida de um ponto (Ex: "JOÃO DA SILVA SANTOS" -> "JOÃO D. S. SANTOS").

### 2. Aplicação na Interface

#### [MODIFY] [VirtualCardScreen.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/virtualcard/VirtualCardScreen.kt)
*   Atualizar o componente `CardContent` para usar `formatCardName` ao exibir o nome do titular ou dependente dentro do design do cartão.
*   **Nota**: No diálogo de expansão, o nome exibido *abaixo* do cartão continuará sendo o nome completo para garantir a legibilidade total, enquanto o nome *dentro* da imagem do cartão será o formatado.

## Plano de Verificação

### Testes Manuais
1.  Verificar cartões com nomes curtos (2 nomes): Devem permanecer inalterados.
2.  Verificar cartões com nomes médios (3 nomes): O nome do meio deve ser abreviado.
3.  Verificar cartões com nomes longos (4+ nomes): Todos os nomes intermediários devem ser abreviados.
4.  Confirmar que a palavra não "estoura" a largura do cartão, mesmo com nomes originalmente muito extensos.

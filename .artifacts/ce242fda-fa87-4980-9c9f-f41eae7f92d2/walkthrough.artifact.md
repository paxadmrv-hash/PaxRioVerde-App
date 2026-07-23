# Walkthrough - Abreviação de Nomes no Cartão Virtual

Implementamos uma formatação inteligente para os nomes exibidos nos cartões virtuais, seguindo o padrão profissional de cartões de crédito. Isso evita que nomes longos quebrem o layout ou fiquem ilegíveis.

## Mudanças Realizadas

### 1. Lógica de Abreviação de Nomes
*   Adicionamos a função `formatCardName` em `VirtualCardScreen.kt`.
*   **Como funciona**:
    *   O primeiro nome e o último sobrenome são mantidos completos.
    *   Nomes intermediários são abreviados para a primeira letra (ex: "Roberto" -> "R.").
    *   Preposições comuns como "da", "de", "do", "dos" são mantidas para preservar a naturalidade do nome.
    *   **Exemplo**: "MARIA DA SILVA SANTOS" vira "MARIA DA S. SANTOS".

### 2. Aplicação Visual
*   A abreviação é aplicada apenas no texto que aparece **dentro do cartão**.
*   O nome completo continua sendo exibido nos detalhes (abaixo do cartão) para que o usuário possa conferir a grafia exata se necessário.

## Resultado
Os cartões agora possuem um visual muito mais limpo e profissional, garantindo que o nome sempre caiba perfeitamente no espaço designado, independentemente de quão longo ele seja.

> [!TIP]
> Esta técnica é amplamente utilizada por bancos e administradoras de cartões para garantir a estética e a padronização visual dos seus produtos.

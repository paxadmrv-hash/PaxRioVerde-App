# Diretrizes de Excelência Kotlin Multiplatform (Senior)

Este documento serve como o "Contrato de Mentoria" para a evolução técnica do projeto Pax Rio Verde.

## 1. Princípios de Arquitetura

### Repository Pattern (Obrigatório)
Toda comunicação com APIs, Bancos de Dados ou Cache Local deve passar por um Repositório.
- **Por que?** Permite trocar a fonte de dados (ex: mudar de Ktor para SQLDelight) sem tocar na UI. Facilita o uso de "Dados Falsos" (Mocks) em testes.

### Inversão de Dependência
Sempre injete **Interfaces**, não classes concretas.
- **Por que?** Segue o 'D' do SOLID. Torna o sistema desacoplado e flexível.

### Gestão de Estado com StateFlow
Utilize `StateFlow` nas ViewModels e `collectAsStateWithLifecycle` (ou similar) na UI.
- **Por que?** `StateFlow` é reativo, thread-safe e funciona em qualquer plataforma Kotlin (iOS, Desktop, Web), ao contrário do `mutableStateOf` que é focado apenas em Compose.

## 2. Padrões de Código (Clean Code)

- **Single Responsibility (SRP)**: Uma ViewModel deve apenas gerenciar o estado da tela, não salvar arquivos ou processar JSONs complexos.
- **Explicação Constante**: Todo novo padrão aplicado deve vir acompanhado de uma explicação didática do "Porquê".

## 3. Workflow de Mentoria

1.  **Identificar**: Encontrar códigos que podem ser melhorados.
2.  **Explicar**: Mostrar o conceito teórico (ex: Clean Architecture).
3.  **Aplicar**: Refatorar o código seguindo o padrão.
4.  **Validar**: Garantir que nada quebrou.

---
> [!TIP]
> Ser um desenvolvedor Sênior não é apenas saber "como" fazer, mas saber "por que" aquela é a melhor forma entre todas as alternativas.

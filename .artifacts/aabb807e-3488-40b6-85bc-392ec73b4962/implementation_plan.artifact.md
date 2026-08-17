# Plano de Implementação: Fallback Local para Gestão de Pets

Este plano visa resolver o erro 404 ao salvar Pets, implementando uma persistência local resiliente no `PetRepository`. Caso o servidor não responda ou retorne erro (como o 404 atual), o aplicativo salvará o pet localmente para garantir a continuidade do uso.

## Problema Identificado
O endpoint `https://rest.paxrioverdeapi.uk/inserir_pet_app` está retornando **404 Not Found**. Como o Ktor tenta converter a resposta (que provavelmente é uma página HTML de erro) para `PetActionResponse`, ocorre uma exceção de transformação que trava o fluxo do usuário.

## Mudanças Propostas

### [Data & Domain Layers]

#### [MODIFY] [PetRepository.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/domain/repository/PetRepository.kt)
- Adicionar função `saveSinglePetLocally(pet: PetItem)` à interface para permitir salvar ou atualizar um pet individual no armazenamento local.

#### [MODIFY] [PetRepositoryImpl.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/data/repository/PetRepositoryImpl.kt)
- Implementar `saveSinglePetLocally`:
    - Recupera a lista atual via `getLocalPets()`.
    - Se o pet já existir (pelo ID), atualiza-o.
    - Se for novo, gera um ID temporário (ex: timestamp negativo) e adiciona à lista.
    - Salva a lista atualizada via `sessionManager`.
- Refatorar `savePet`:
    - Envolver a chamada da API e, em caso de `NetworkResult.Error`, invocar o `saveSinglePetLocally`.
    - Retornar um `NetworkResult.Success` com uma mensagem indicando o salvamento local, para que a UI prossiga normalmente.

### [UI Layer]

#### [MODIFY] [PetViewModel.kt](file:///C:/Users/arielson.silva/PaxRioVerde/composeApp/src/commonMain/kotlin/com/example/paxrioverde/ui/pet/PetViewModel.kt)
- Ajustar o tratamento de sucesso no `savePet` para garantir que, após o salvamento (mesmo que local), a lista seja recarregada do repositório para refletir a mudança na tela.

---

## Plano de Verificação

### Testes Manuais
1.  **Cenário de Erro (Atual):** Tentar cadastrar um pet. O app deve mostrar "Salvo localmente" (ou sucesso) em vez do erro 404, e o pet deve aparecer na lista.
2.  **Persistência:** Fechar e abrir o app. O pet salvo localmente deve persistir na lista (já implementado via `SessionManager` no `loadPets`).
3.  **Edição:** Editar um pet recém-criado localmente. As alterações devem ser mantidas.

> [!IMPORTANT]
> Esta solução é isolada e não altera a interface visual, apenas a lógica de persistência para contornar a indisponibilidade do endpoint no backend.

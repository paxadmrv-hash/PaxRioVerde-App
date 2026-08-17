package com.example.paxrioverde.ui.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paxrioverde.api.PetItem
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.repository.PetRepository
import com.example.paxrioverde.util.PaxLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PetUiState(
    val petsList: List<PetItem> = emptyList(),
    val selectedPetIndex: Int = 0,
    val isLoading: Boolean = true,
    val showPetDialog: Boolean = false,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val editName: String = "",
    val editBreed: String = "",
    val editBirthDate: String = "",
    val editPhotoBase64: String = ""
)

class PetViewModel(
    private val repository: PetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    fun loadPets(idcliente: Int) {
        viewModelScope.launch {
            // Primeiro carrega o que tem local para UI imediata
            val localPets = repository.getLocalPets()
            _uiState.update { it.copy(petsList = localPets, isLoading = localPets.isEmpty()) }
            
            // Depois busca da API para sincronizar
            val result = repository.getPets(idcliente)
            
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(petsList = result.data, isLoading = false) }
                }
                is NetworkResult.Error -> {
                    // Senior Note: Só incomodamos o usuário com banners de erro 
                    // se não tivermos nada para mostrar na tela.
                    _uiState.update { it.copy(isLoading = false) }
                    if (_uiState.value.petsList.isEmpty()) {
                        _events.emit("Modo offline: ${result.message}")
                    }
                }
                else -> {}
            }
        }
    }

    fun onSelectPet(index: Int) {
        _uiState.update { it.copy(selectedPetIndex = index) }
    }

    fun onShowDialog(isEditing: Boolean, pet: PetItem? = null) {
        _uiState.update {
            if (isEditing && pet != null) {
                it.copy(
                    showPetDialog = true,
                    isEditing = true,
                    editName = pet.nome ?: "",
                    editBreed = pet.raca ?: "",
                    editBirthDate = pet.dtnascimento?.replace("/", "") ?: "",
                    editPhotoBase64 = pet.foto ?: ""
                )
            } else {
                it.copy(
                    showPetDialog = true,
                    isEditing = false,
                    editName = "",
                    editBreed = "",
                    editBirthDate = "",
                    editPhotoBase64 = ""
                )
            }
        }
    }

    fun onDismissDialog() {
        if (!_uiState.value.isSaving) {
            _uiState.update { it.copy(showPetDialog = false) }
        }
    }

    fun onEditNameChange(value: String) {
        _uiState.update { it.copy(editName = value) }
    }

    fun onEditBreedChange(value: String) {
        _uiState.update { it.copy(editBreed = value) }
    }

    fun onEditBirthDateChange(value: String) {
        _uiState.update { it.copy(editBirthDate = value.filter { char -> char.isDigit() }.take(8)) }
    }

    fun onEditPhotoChange(base64: String) {
        _uiState.update { it.copy(editPhotoBase64 = base64) }
    }

    fun savePet(idcliente: Int, idcontrato: Int, idconvenio: Int) {
        val state = _uiState.value
        
        // Senior Note: Validação rigorosa para evitar erro 403 do backend
        if (state.editName.isBlank()) {
            viewModelScope.launch { _events.emit("Informe o nome do pet") }
            return
        }
        if (state.editBreed.isBlank()) {
            viewModelScope.launch { _events.emit("Informe a raça do pet") }
            return
        }
        if (state.editBirthDate.length < 8) {
            viewModelScope.launch { _events.emit("Informe a data de nascimento completa") }
            return
        }
        if (state.editPhotoBase64.isBlank()) {
            viewModelScope.launch { _events.emit("Selecione uma foto para o pet") }
            return
        }
        if (idcontrato == 0 || idconvenio == 0) {
            viewModelScope.launch { _events.emit("Erro: Dados do contrato não localizados") }
            return
        }

        PaxLogger.d("Iniciando salvamento: idcontrato=$idcontrato, idconvenio=$idconvenio", "PET_DEBUG")
        PaxLogger.d("Dados editados: nome=${state.editName}, raca=${state.editBreed}, nascimento=${state.editBirthDate}, fotoLength=${state.editPhotoBase64.length}", "PET_DEBUG")

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            try {
                val formattedDate = "${state.editBirthDate.substring(0, 2)}/${state.editBirthDate.substring(2, 4)}/${state.editBirthDate.substring(4, 8)}"
                PaxLogger.d("Data formatada para envio: $formattedDate", "PET_DEBUG")
                
                val idPetStr = if (state.isEditing) state.petsList[state.selectedPetIndex].idpet else "0"
                val idPetInt = idPetStr?.toIntOrNull() ?: 0
                val isNewPet = idPetInt <= 0
                
                val situacao = if (isNewPet) "I" else "A"
                val idToSend = if (isNewPet) 0 else idPetInt

                PaxLogger.d("Parâmetros para repositório: idpet=$idToSend, situacao=$situacao", "PET_DEBUG")

                val result = repository.savePet(
                    idcliente = idcliente,
                    idcontrato = idcontrato,
                    idconvenio = idconvenio,
                    idpet = idToSend,
                    nome = state.editName.trim(),
                    raca = state.editBreed.trim(),
                    dtnascimento = formattedDate,
                    foto = state.editPhotoBase64,
                    situacao = situacao
                )
                
                when (result) {
                    is NetworkResult.Success -> {
                        val response = result.data
                        if (response.success) {
                            _events.emit("Pet salvo com sucesso!")
                            _uiState.update { it.copy(showPetDialog = false, isSaving = false) }
                            // Recarrega a lista para sincronizar
                            loadPets(idcliente)
                        } else {
                            _events.emit(response.message ?: "Erro ao salvar pet")
                            _uiState.update { it.copy(isSaving = false) }
                        }
                    }
                    is NetworkResult.Error -> {
                        _events.emit(result.message)
                        _uiState.update { it.copy(isSaving = false) }
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _events.emit("Erro inesperado: ${e.message}")
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}

package com.example.paxrioverde.ui.benefits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.milliseconds

data class BenefitsUiState(
    val selectedCategory: String = "Todos",
    val selectedCity: String = "Todas",
    val searchQuery: String = "",
    val filteredPartners: List<Partner> = realPartners.sortedBy { it.name },
    val isReady: Boolean = false
)

class BenefitsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BenefitsUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    
    init {
        setupSearchDebounce()
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        _searchQuery
            .debounce(300.milliseconds) 
            .distinctUntilChanged()
            .onEach { 
                filterPartners()
            }
            .launchIn(viewModelScope)
    }

    fun setReady() {
        _uiState.update { it.copy(isReady = true) }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        filterPartners()
    }

    fun onCitySelected(city: String) {
        _uiState.update { it.copy(selectedCity = city) }
        filterPartners()
    }

    fun onSearchQueryChange(query: String) {
        // Senior Fix: Atualiza a UI imediatamente para digitação fluida
        _uiState.update { it.copy(searchQuery = query) }
        // Dispara o fluxo de debounce apenas para a filtragem pesada
        _searchQuery.value = query
    }

    private fun filterPartners() {
        val currentState = _uiState.value
        val query = currentState.searchQuery.trim()
        
        val filtered = realPartners.filter { p ->
            val catOk  = currentState.selectedCategory == "Todos" || p.category == currentState.selectedCategory
            val cityOk = currentState.selectedCity == "Todas"    || p.city == currentState.selectedCity
            val searchOk = query.isEmpty() ||
                    p.name.contains(query, ignoreCase = true) ||
                    p.category.contains(query, ignoreCase = true) ||
                    p.discount.contains(query, ignoreCase = true)
            catOk && cityOk && searchOk
        }.sortedBy { it.name }
        _uiState.update { it.copy(filteredPartners = filtered) }
    }
}

package com.example.paxrioverde.api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.example.paxrioverde.util.PaxLogger
import com.example.paxrioverde.util.SessionManager
import kotlinx.coroutines.delay
import kotlinx.datetime.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WalletCache(
    private val api: ApiService,
    private val sessionManager: SessionManager
) {
    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    val cartoesList = mutableStateListOf<CartaoItem>()
    val dependentesList = mutableStateListOf<DependenteItem>()
    val mensalidadesList = mutableStateListOf<MensalidadeItem>()
    val loadedBitmaps = mutableStateMapOf<Int, ImageBitmap>()
    // Senior Note: Controle de expiração FIFO para evitar leak de memória
    private val bitmapOrder = mutableListOf<Int>()
    private val MAX_BITMAPS = 10
    private var lastIdCliente: Int = 0

    fun putBitmap(id: Int, bitmap: ImageBitmap) {
        if (loadedBitmaps.containsKey(id)) {
            loadedBitmaps[id] = bitmap
            return
        }

        if (bitmapOrder.size >= MAX_BITMAPS) {
            val oldestId = bitmapOrder.removeAt(0)
            loadedBitmaps.remove(oldestId)
        }

        loadedBitmaps[id] = bitmap
        bitmapOrder.add(id)
    }
    var isPreloading by mutableStateOf(false)
    

    val totalValorCartoes: Double
        get() = cartoesList.sumOf { 
            it.valor?.replace(",", ".")?.toDoubleOrNull() ?: 0.0 
        }

    suspend fun preLoad(idcliente: Int, forceRefresh: Boolean = false) {
        if (idcliente == 0) return

        // Troca de usuário detectada: Limpa cache em memória e reinicia lastIdCliente
        if (lastIdCliente != 0 && lastIdCliente != idcliente) {
            PaxLogger.d("Troca de usuário detectada ($lastIdCliente -> $idcliente). Limpando cache.", "WalletCache")
            clear()
        }
        lastIdCliente = idcliente
        
        // Carrega do cache persistente se as listas estiverem vazias (Cold Start)
        if (cartoesList.isEmpty()) {
            loadFromPersistentCache()
        }
        if (dependentesList.isEmpty()) {
            loadDependentsFromPersistentCache()
        }

        if (isPreloading && !forceRefresh) return
        
        if (forceRefresh) {
            // Em vez de limpar tudo e causar flicker, limpamos apenas os cartões que são persistidos
            // Os dependentes serão atualizados via merge inteligente
            cartoesList.clear()
            mensalidadesList.clear()
            loadedBitmaps.clear()
        }

        isPreloading = true
        try {
            // 1. Busca a lista de dependentes
            try {
                val resDep = api.getDependentes(idcliente)
                if (resDep.success) {
                    val novosDependentes = resDep.dependentes.orEmpty()
                        .distinctBy { it.nomeDependente + it.parentesco }
                    
                    if (dependentesList.isEmpty()) {
                        dependentesList.addAll(novosDependentes)
                        saveDependentsToPersistentCache()
                    } else {
                        // MERGE INTELIGENTE: Atualiza sem causar flicker e preserva dados novos
                        var changed = false
                        novosDependentes.forEach { novo ->
                            val index = dependentesList.indexOfFirst { 
                                it.nomeDependente == novo.nomeDependente && it.parentesco == novo.parentesco 
                            }
                            if (index != -1) {
                                val atual = dependentesList[index]
                                
                                val novoCpfValido = !novo.cpf.isNullOrBlank() && 
                                                    novo.cpf.filter { it.isDigit() }.length >= 11
                                val atualCpfValido = !atual.cpf.isNullOrBlank() && 
                                                     atual.cpf.filter { it.isDigit() }.length >= 11

                                // LÓGICA DE MERGE ROBUSTA (Senior):
                                // Comparamos os objetos ignorando a instância.
                                if (novo != atual) {
                                    if (!novoCpfValido && atualCpfValido) {
                                        // Lag do servidor detectado: Preservamos o CPF local mas aceitamos outras mudanças se houver
                                        val merged = novo.copy(cpf = atual.cpf)
                                        if (merged != atual) {
                                            dependentesList[index] = merged
                                            changed = true
                                            PaxLogger.d("Sincronização: Mesclando dados do servidor e preservando CPF local para ${novo.nomeDependente}", "WalletCache")
                                        }
                                    } else {
                                        // Caso normal: Servidor tem dados novos ou confirma a atualização
                                        dependentesList[index] = novo
                                        changed = true
                                    }
                                }
                            } else {
                                dependentesList.add(novo)
                                changed = true
                            }
                        }
                        if (changed) {
                            saveDependentsToPersistentCache()
                        }
                    }
                }
            } catch (e: Exception) {
                PaxLogger.e("Erro ao carregar dependentes", e, "WalletCache")
            }

            // 2. Busca a lista de mensalidades para validade dinâmica
            try {
                val resMens = api.getMensalidades(idcliente)
                if (resMens.success) {
                    mensalidadesList.clear()
                    val todasMensalidades = resMens.anos?.flatMap { it.mensalidades } ?: emptyList()
                    mensalidadesList.addAll(todasMensalidades)
                }
            } catch (e: Exception) {
                PaxLogger.e("Erro ao carregar mensalidades", e, "WalletCache")
            }

            // 3. Busca a lista de cartões
            val response = api.getCartoes(idcliente)
            if (response.success && response.cartoes != null) {
                cartoesList.clear()
                cartoesList.addAll(response.cartoes.orEmpty())
                saveToPersistentCache()
            }
        } catch (e: Exception) {
            PaxLogger.e("Erro no processo de pre-load", e, "WalletCache")
        } finally {
            isPreloading = false
        }
    }

    /**
     * Calcula a validade do cartão baseada no histórico de fidelidade.
     * < 12 mensalidades pagas = +6 meses de validade.
     * >= 12 mensalidades pagas = +1 ano de validade.
     */
    fun getCalculatedValidity(originalValidity: String): String {
        val totalPaid = mensalidadesList.count { it.pago }
        
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        val monthsToAdd = if (totalPaid >= 12) 12 else 6
        
        val validityDate = today.plus(monthsToAdd, DateTimeUnit.MONTH)
        
        return formatDate(validityDate)
    }

    private fun parseDate(dateStr: String): LocalDate? {
        return try {
            val parts = dateStr.split("/")
            if (parts.size == 3) {
                LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun formatDate(date: LocalDate): String {
        val day = date.dayOfMonth.toString().padStart(2, '0')
        val month = date.monthNumber.toString().padStart(2, '0')
        val year = date.year
        return "$day/$month/$year"
    }

    /**
     * Persistência de Cache Offline
     */
    private fun saveToPersistentCache() {
        try {
            val cardsJson = json.encodeToString(cartoesList.toList())
            sessionManager.saveCardsJson(cardsJson)
            PaxLogger.d("Cache de cartões salvo (${cartoesList.size} itens)", "WalletCache")
        } catch (e: Exception) {
            PaxLogger.e("Erro ao salvar cache de cartões", e, "WalletCache")
        }
    }

    private fun loadFromPersistentCache() {
        try {
            val cardsJson = sessionManager.getSavedCardsJson()
            if (cardsJson.isNotEmpty()) {
                val cachedCards = json.decodeFromString<List<CartaoItem>>(cardsJson)
                cartoesList.clear()
                cartoesList.addAll(cachedCards)
                PaxLogger.d("Cache de cartões recuperado (${cachedCards.size} itens)", "WalletCache")
            }
        } catch (e: Exception) {
            PaxLogger.e("Erro ao carregar cache de cartões", e, "WalletCache")
        }
    }

    /**
     * Persistência de Dependentes
     */
    fun saveDependentsToPersistentCache() {
        try {
            val depsJson = json.encodeToString(dependentesList.toList())
            sessionManager.saveDependentsJson(depsJson)
            PaxLogger.d("Cache de dependentes salvo (${dependentesList.size} itens)", "WalletCache")
        } catch (e: Exception) {
            PaxLogger.e("Erro ao salvar cache de dependentes", e, "WalletCache")
        }
    }

    private fun loadDependentsFromPersistentCache() {
        try {
            val depsJson = sessionManager.getSavedDependentsJson()
            if (depsJson.isNotEmpty()) {
                val cachedDeps = json.decodeFromString<List<DependenteItem>>(depsJson)
                dependentesList.clear()
                dependentesList.addAll(cachedDeps)
                PaxLogger.d("Cache de dependentes recuperado (${cachedDeps.size} itens)", "WalletCache")
            }
        } catch (e: Exception) {
            PaxLogger.e("Erro ao carregar cache de dependentes", e, "WalletCache")
        }
    }

    fun clearBitmaps() {
        loadedBitmaps.clear()
        bitmapOrder.clear()
    }

    fun clear() {
        cartoesList.clear()
        dependentesList.clear()
        mensalidadesList.clear()
        loadedBitmaps.clear()
        bitmapOrder.clear()
        lastIdCliente = 0
    }
}

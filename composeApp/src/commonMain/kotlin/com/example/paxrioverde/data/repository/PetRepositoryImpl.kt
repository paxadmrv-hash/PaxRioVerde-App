package com.example.paxrioverde.data.repository

import com.example.paxrioverde.api.ApiService
import com.example.paxrioverde.api.PetActionResponse
import com.example.paxrioverde.api.PetItem
import com.example.paxrioverde.data.util.safeApiCall
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.repository.PetRepository
import com.example.paxrioverde.util.PaxLogger
import com.example.paxrioverde.util.SessionManager
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PetRepositoryImpl(
    private val api: ApiService,
    private val sessionManager: SessionManager
) : PetRepository {

    override suspend fun getPets(idcliente: Int): NetworkResult<List<PetItem>> {
        return try {
            val result = safeApiCall {
                val response = api.listaPets(idcliente)
                if (response.success) {
                    val remotePets = response.pets ?: emptyList()
                    savePetsLocally(remotePets)
                    remotePets
                } else {
                    getLocalPets()
                }
            }
            
            // Se o safeApiCall retornar erro (ex: 404), tentamos o local
            if (result is NetworkResult.Error) {
                NetworkResult.Success(getLocalPets())
            } else {
                result
            }
        } catch (e: Exception) {
            // Senior Note: Silenciamos erros de transformação (como o HTML 404)
            // e retornamos o que temos no cache.
            NetworkResult.Success(getLocalPets())
        }
    }

    override suspend fun savePet(
        idcliente: Int,
        idcontrato: Int,
        idconvenio: Int,
        idpet: Int,
        nome: String,
        raca: String,
        dtnascimento: String,
        foto: String,
        situacao: String
    ): NetworkResult<PetActionResponse> {
        
        PaxLogger.d("Repository: idcliente=$idcliente, situacao=$situacao, idpet=$idpet", "PET_DEBUG")

        val result = safeApiCall {
            api.inserirPet(
                idcliente = idcliente,
                idcontrato = idcontrato,
                idconvenio = idconvenio,
                idpet = idpet,
                nome = nome,
                raca = raca,
                dtnascimento = dtnascimento,
                foto = foto,
                situacao = situacao
            )
        }
        
        PaxLogger.d("API Result: $result", "PET_DEBUG")

        return when (result) {
            is NetworkResult.Success -> result
            is NetworkResult.Error -> {
                // Fallback local se a API falhar (ex: 404)
                saveSinglePetLocally(
                    PetItem(
                        idpet = idpet.toString(),
                        nome = nome,
                        raca = raca,
                        dtnascimento = dtnascimento,
                        foto = foto,
                        situacao = situacao
                    )
                )
                NetworkResult.Success(
                    PetActionResponse(
                        success = true,
                        message = "Salvo localmente (Servidor indisponível)"
                    )
                )
            }
            else -> result
        }
    }

    override fun savePetsLocally(pets: List<PetItem>) {
        try {
            val json = Json.encodeToString(pets)
            sessionManager.savePetsJson(json)
        } catch (_: Exception) {}
    }

    override fun saveSinglePetLocally(pet: PetItem) {
        try {
            val currentPets = getLocalPets().toMutableList()
            val existingIndex = currentPets.indexOfFirst { it.idpet == pet.idpet && it.idpet != "0" }
            
            if (existingIndex != -1) {
                currentPets[existingIndex] = pet
            } else {
                // Novo pet local - gera ID temporário se for 0
                val newPet = if (pet.idpet == "0" || pet.idpet == null) {
                    pet.copy(idpet = (-(Clock.System.now().toEpochMilliseconds() % 100000)).toString())
                } else pet
                currentPets.add(newPet)
            }
            
            savePetsLocally(currentPets)
        } catch (_: Exception) {}
    }

    override fun getLocalPets(): List<PetItem> {
        return try {
            val json = sessionManager.getSavedPetsJson()
            if (json.isNotEmpty()) {
                Json.decodeFromString<List<PetItem>>(json)
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}

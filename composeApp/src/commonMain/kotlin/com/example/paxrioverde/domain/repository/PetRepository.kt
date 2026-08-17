package com.example.paxrioverde.domain.repository

import com.example.paxrioverde.api.PetItem
import com.example.paxrioverde.api.PetActionResponse
import com.example.paxrioverde.domain.model.NetworkResult

/**
 * Interface para gestão dos pets do cliente com resiliência.
 */
interface PetRepository {
    /**
     * Busca a lista de pets.
     */
    suspend fun getPets(idcliente: Int): NetworkResult<List<PetItem>>

    /**
     * Insere ou atualiza um pet no backend.
     */
    suspend fun savePet(
        idcliente: Int,
        idcontrato: Int,
        idconvenio: Int,
        idpet: Int,
        nome: String,
        raca: String,
        dtnascimento: String,
        foto: String,
        situacao: String
    ): NetworkResult<PetActionResponse>
    
    fun savePetsLocally(pets: List<PetItem>)
    fun saveSinglePetLocally(pet: PetItem)
    fun getLocalPets(): List<PetItem>
}

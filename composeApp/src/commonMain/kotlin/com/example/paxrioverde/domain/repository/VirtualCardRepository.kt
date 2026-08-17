package com.example.paxrioverde.domain.repository

import com.example.paxrioverde.api.*
import com.example.paxrioverde.domain.model.NetworkResult

/**
 * Interface para gestão de cartões virtuais e dependentes com resiliência.
 */
interface VirtualCardRepository {
    fun getCartoes(): List<CartaoItem>
    suspend fun refreshData(idcliente: Int): NetworkResult<Unit>

    suspend fun gerarCartaoPix(
        idcaixa: Int,
        idcliente: Int,
        tipo: String,
        nomeDependente: String
    ): NetworkResult<GerarCartaoPixResponse>

    suspend fun gerarCartaoDireto(
        idcaixa: Int,
        idcliente: Int,
        tipo: String,
        nomeDependente: String?,
        isGratuito: Boolean,
        idcontrato: Int,
        idconvenio: Int,
        cpfDependente: String?,
        dtvencimento: String?,
        parentesco: String?,
        idfilial: Int
    ): NetworkResult<GerarCartaoResponse>

    suspend fun verificarPixPago(identificadorPix: String): NetworkResult<Boolean>
}

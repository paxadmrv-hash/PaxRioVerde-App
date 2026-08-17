package com.example.paxrioverde.data.repository

import com.example.paxrioverde.api.*
import com.example.paxrioverde.data.util.safeApiCall
import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.domain.repository.VirtualCardRepository

class VirtualCardRepositoryImpl(
    private val api: ApiService,
    private val walletCache: WalletCache
) : VirtualCardRepository {

    override fun getCartoes(): List<CartaoItem> = walletCache.cartoesList.toList()

    override suspend fun refreshData(idcliente: Int): NetworkResult<Unit> {
        return safeApiCall {
            walletCache.preLoad(idcliente, forceRefresh = true)
        }
    }

    override suspend fun gerarCartaoPix(
        idcaixa: Int,
        idcliente: Int,
        tipo: String,
        nomeDependente: String
    ): NetworkResult<GerarCartaoPixResponse> {
        return safeApiCall {
            api.gerarCartaoPix(idcaixa, idcliente, tipo, nomeDependente)
        }
    }

    override suspend fun gerarCartaoDireto(
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
    ): NetworkResult<GerarCartaoResponse> {
        return safeApiCall {
            api.gerarCartao(
                idcaixa = idcaixa,
                idcliente = idcliente,
                tipo = tipo,
                nomeDependente = nomeDependente,
                gratuito = if (isGratuito) "S" else "N",
                idcontrato = idcontrato,
                idconvenio = idconvenio,
                cpfDependente = cpfDependente,
                dtvencimento = dtvencimento,
                parentesco = parentesco,
                idfilial = idfilial
            )
        }
    }

    override suspend fun verificarPixPago(identificadorPix: String): NetworkResult<Boolean> {
        return safeApiCall {
            val response = api.verificarPixPago(identificadorPix)
            response.pago
        }
    }
}

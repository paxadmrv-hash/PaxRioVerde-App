package com.example.paxrioverde.data.util

import com.example.paxrioverde.domain.model.NetworkResult
import com.example.paxrioverde.util.PaxLogger
import io.ktor.client.plugins.*

/**
 * Função utilitária Sênior para encapsular chamadas de API com tratamento de erro global.
 * Converte exceções do Ktor em [NetworkResult] amigáveis.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(apiCall())
    } catch (e: RedirectResponseException) {
        PaxLogger.e("Redirecionamento: ${e.response.status.description}", subTag = "API_CALL")
        NetworkResult.Error("Erro de redirecionamento: ${e.response.status.description}")
    } catch (e: ClientRequestException) {
        PaxLogger.e("Erro 4xx: ${e.response.status.value} - ${e.response.status.description}", subTag = "API_CALL")
        val message = when (e.response.status.value) {
            401 -> "Sessão expirada. Por favor, faça login novamente."
            403 -> "Acesso negado."
            404 -> "Serviço não encontrado."
            else -> "Erro na requisição: ${e.response.status.description}"
        }
        NetworkResult.Error(message, e)
    } catch (e: ServerResponseException) {
        PaxLogger.e("Erro 5xx: ${e.response.status.value}", subTag = "API_CALL")
        NetworkResult.Error("O servidor da Pax Rio Verde está temporariamente instável. Tente novamente em instantes.", e)
    } catch (e: kotlinx.io.IOException) {
        PaxLogger.e("Erro de Rede: ${e.message}", subTag = "API_CALL")
        NetworkResult.Error("Sem conexão com a internet. Verifique seu Wi-Fi ou dados móveis.", e)
    } catch (e: Exception) {
        PaxLogger.e("Erro Inesperado: ${e.message}", subTag = "API_CALL")
        
        // Senior UX: Tratamento para quando o servidor retorna HTML em vez de JSON (comum em erros 530/502/404)
        val errorMessage = if (e.message?.contains("SourceByteReadChannel") == true || e.message?.contains("NoTransformationFoundException") == true) {
            "O servidor está passando por uma manutenção rápida. Por favor, tente novamente em alguns minutos."
        } else {
            "Ocorreu um erro inesperado ao processar os dados. Tente novamente mais tarde."
        }
        
        NetworkResult.Error(errorMessage, e)
    }
}

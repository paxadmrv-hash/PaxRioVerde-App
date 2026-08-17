package com.example.paxrioverde.domain.model

/**
 * Representa o resultado de uma operação de rede seguindo o padrão Senior.
 * [T] é o tipo de dado em caso de sucesso.
 */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val exception: Throwable? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()

    /**
     * Utilitário para facilitar a execução de blocos em caso de sucesso.
     */
    inline fun onSuccess(action: (T) -> Unit): NetworkResult<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Utilitário para facilitar a execução de blocos em caso de erro.
     */
    inline fun onFailure(action: (String) -> Unit): NetworkResult<T> {
        if (this is Error) action(message)
        return this
    }
}

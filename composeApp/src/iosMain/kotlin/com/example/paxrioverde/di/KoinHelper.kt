package com.example.paxrioverde.di

/**
 * Helper para inicialização do Koin no iOS.
 * Senior Multiplatform Note: Exposto para o Swift inicializar o grafo de dependências
 * antes da UI ser construída.
 */
object KoinHelper {
    fun doInit() {
        initKoin {}
    }
}

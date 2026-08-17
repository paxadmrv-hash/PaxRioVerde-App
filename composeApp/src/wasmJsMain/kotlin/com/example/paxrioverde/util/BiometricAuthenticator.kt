package com.example.paxrioverde.util

actual class BiometricAuthenticator {
    actual fun canAuthenticate(): Boolean = false
    actual fun authenticate(
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        onError("Não suportado nesta plataforma")
    }
}

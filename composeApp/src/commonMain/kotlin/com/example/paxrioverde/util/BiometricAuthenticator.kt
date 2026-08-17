package com.example.paxrioverde.util

expect class BiometricAuthenticator() {
    fun canAuthenticate(): Boolean
    fun authenticate(
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}

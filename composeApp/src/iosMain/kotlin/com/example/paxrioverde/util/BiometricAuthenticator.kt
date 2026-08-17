package com.example.paxrioverde.util

import platform.LocalAuthentication.*
import platform.Foundation.*

actual class BiometricAuthenticator {
    actual fun canAuthenticate(): Boolean {
        val context = LAContext()
        return context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
    }

    actual fun authenticate(
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val context = LAContext()
        context.evaluatePolicy(
            LAPolicyDeviceOwnerAuthentication,
            localizedReason = title
        ) { success, error ->
            NSOperationQueue.mainQueue.addOperationWithBlock {
                if (success) {
                    onSuccess()
                } else {
                    onError(error?.localizedDescription ?: "Falha na autenticação")
                }
            }
        }
    }
}

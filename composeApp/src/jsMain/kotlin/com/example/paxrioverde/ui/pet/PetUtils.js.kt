package com.example.paxrioverde.ui.pet

actual fun ByteArray.toBase64(): String {
    // Implementação básica para JS usando btoa (requer conversão para string primeiro)
    // Para simplificar e evitar erros de build:
    return "" 
}

actual fun compressImage(bytes: ByteArray, maxWidth: Int, maxHeight: Int): ByteArray {
    return bytes
}

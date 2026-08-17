package com.example.paxrioverde.ui.pet

actual fun ByteArray.toBase64(): String {
    return ""
}

actual fun compressImage(bytes: ByteArray, maxWidth: Int, maxHeight: Int): ByteArray {
    return bytes
}

package com.example.paxrioverde.ui.pet

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.Foundation.getBytes
import kotlinx.cinterop.BetaInteropApi

@OptIn(ExperimentalForeignApi::class)
actual fun ByteArray.toBase64(): String {
    if (isEmpty()) return ""
    // Executamos a conversão para Base64 dentro do bloco usePinned para garantir
    // que o ponteiro para os bytes do ByteArray permaneça válido durante a criação do NSData.
    return usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
            .base64EncodedStringWithOptions(0u)
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val byteArray = ByteArray(size)
    if (size > 0) {
        byteArray.usePinned { pinned ->
            getBytes(pinned.addressOf(0), length)
        }
    }
    return byteArray
}

package com.example.paxrioverde.ui.pet

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.Foundation.getBytes
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake

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
actual fun compressImage(bytes: ByteArray, maxWidth: Int, maxHeight: Int): ByteArray {
    val data = bytes.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
    }
    val image = UIImage.imageWithData(data) ?: return bytes
    
    val size = image.size.useContents { this }
    var newWidth = size.width
    var newHeight = size.height
    
    if (newWidth > maxWidth || newHeight > maxHeight) {
        val ratio = newWidth / newHeight
        if (ratio > 1) {
            newWidth = maxWidth.toDouble()
            newHeight = newWidth / ratio
        } else {
            newHeight = maxHeight.toDouble()
            newWidth = newHeight * ratio
        }
    }
    
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(newWidth, newHeight), false, 1.0)
    image.drawInRect(CGRectMake(0.0, 0.0, newWidth, newHeight))
    val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    
    val compressedData = resizedImage?.let { UIImageJPEGRepresentation(it, 0.8) } ?: return bytes
    return compressedData.toByteArray()
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

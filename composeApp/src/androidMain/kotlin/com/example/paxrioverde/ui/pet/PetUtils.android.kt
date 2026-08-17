package com.example.paxrioverde.ui.pet

import android.util.Base64

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

actual fun ByteArray.toBase64(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP)
}

actual fun compressImage(bytes: ByteArray, maxWidth: Int, maxHeight: Int): ByteArray {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    
    var width = options.outWidth
    var height = options.outHeight
    
    var inSampleSize = 1
    if (height > maxHeight || width > maxWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while (halfHeight / inSampleSize >= maxHeight && halfWidth / inSampleSize >= maxWidth) {
            inSampleSize *= 2
        }
    }
    
    options.inJustDecodeBounds = false
    options.inSampleSize = inSampleSize
    
    val decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    
    val outputStream = ByteArrayOutputStream()
    // Comprimimos em JPEG com 80% de qualidade (Padrão Senior para UX equilibrada)
    decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    
    return outputStream.toByteArray()
}

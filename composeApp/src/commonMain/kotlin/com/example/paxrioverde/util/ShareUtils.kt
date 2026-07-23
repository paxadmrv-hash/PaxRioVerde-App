package com.example.paxrioverde.util

expect fun shareText(text: String, title: String = "Compartilhar")
expect fun shareImage(bytes: ByteArray, fileName: String, title: String = "Compartilhar")
expect fun saveImageToGallery(bytes: ByteArray, fileName: String)

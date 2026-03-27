package com.example.paxrioverde.ui.pet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.preat.peekaboo.image.picker.SelectionMode
import java.io.ByteArrayOutputStream

@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (ByteArray?) -> Unit): ImagePickerLauncher {
    val scope = rememberCoroutineScope()
    
    // Na versão 0.5.2, a função da biblioteca se chama rememberImagePickerLauncher.
    // Usamos o nome completo do pacote para evitar conflito com a nossa própria função 'actual'.
    val launcher = com.preat.peekaboo.image.picker.rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays: List<ByteArray> ->
            val compressed = byteArrays.firstOrNull()?.let { bytes ->
                try {
                    // 1. Decodifica apenas as dimensões para calcular o sample size
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                    
                    // 2. Calcula o sample size para reduzir a memória (alvo ~800px)
                    val targetSize = 800
                    var inSampleSize = 1
                    if (options.outHeight > targetSize || options.outWidth > targetSize) {
                        val halfHeight = options.outHeight / 2
                        val halfWidth = options.outWidth / 2
                        while (halfHeight / inSampleSize >= targetSize && halfWidth / inSampleSize >= targetSize) {
                            inSampleSize *= 2
                        }
                    }
                    
                    // 3. Decodifica o bitmap real redimensionado
                    val decodeOptions = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
                    var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOptions)
                    
                    // 4. Redimensionamento fino para garantir o limite de 800px
                    if (bitmap != null && (bitmap.width > targetSize || bitmap.height > targetSize)) {
                        val scale = targetSize.toFloat() / maxOf(bitmap.width, bitmap.height)
                        bitmap = Bitmap.createScaledBitmap(
                            bitmap, 
                            (bitmap.width * scale).toInt(), 
                            (bitmap.height * scale).toInt(), 
                            true
                        )
                    }
                    
                    // 5. Comprime para JPEG 60% para reduzir o tamanho do Base64
                    val out = ByteArrayOutputStream()
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 60, out)
                    out.toByteArray()
                } catch (e: Exception) {
                    bytes // Se falhar em qualquer etapa, tenta enviar o original
                }
            }
            onImagePicked(compressed)
        }
    )

    return object : ImagePickerLauncher {
        override fun launch() {
            launcher.launch()
        }
    }
}

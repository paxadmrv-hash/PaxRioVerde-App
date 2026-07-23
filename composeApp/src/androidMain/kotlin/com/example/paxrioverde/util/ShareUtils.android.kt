package com.example.paxrioverde.util

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.paxrioverde.AndroidContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

actual fun shareText(text: String, title: String) {
    val context = AndroidContext.get()
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    val shareIntent = Intent.createChooser(sendIntent, title).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(shareIntent)
}

actual fun shareImage(bytes: ByteArray, fileName: String, title: String) {
    val context = AndroidContext.get()
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "$fileName.png")
        val stream = FileOutputStream(file)
        stream.write(bytes)
        stream.close()

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(contentUri, context.contentResolver.getType(contentUri))
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = "image/png"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val shareIntent = Intent.createChooser(intent, title).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

actual fun saveImageToGallery(bytes: ByteArray, fileName: String) {
    val context = AndroidContext.get()
    try {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: run {
            Toast.makeText(context, "Erro ao processar imagem", Toast.LENGTH_SHORT).show()
            return
        }
        
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/PaxRioVerde")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri == null) {
            Toast.makeText(context, "Erro ao criar arquivo na galeria", Toast.LENGTH_SHORT).show()
            return
        }

        val outputStream: OutputStream? = resolver.openOutputStream(uri)
        outputStream?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }
        
        Toast.makeText(context, "Imagem salva na galeria!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

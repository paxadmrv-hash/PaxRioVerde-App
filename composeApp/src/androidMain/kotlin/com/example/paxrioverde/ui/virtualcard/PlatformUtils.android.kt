package com.example.paxrioverde.ui.virtualcard

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.paxrioverde.AndroidContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val cardDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

actual fun isCardExpired(validity: String): Boolean {
    return try {
        val expiryDate = LocalDate.parse(validity, cardDateFormatter)
        val today = LocalDate.now()
        expiryDate.isBefore(today)
    } catch (e: Exception) {
        false
    }
}

actual suspend fun renderPdfBase64ToBitmap(base64Str: String): ImageBitmap? = withContext(Dispatchers.IO) {
    try {
        val context = AndroidContext.get()
        val pdfData = Base64.decode(base64Str, Base64.DEFAULT)
        val tempFile = File(context.cacheDir, "temp_card_${System.currentTimeMillis()}.pdf")
        FileOutputStream(tempFile).use { it.write(pdfData) }

        val fd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(fd)
        val page = renderer.openPage(0)

        val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        page.close()
        renderer.close()
        fd.close()
        tempFile.delete()

        bitmap.asImageBitmap()
    } catch (e: Exception) {
        Log.e("PDF_RENDER", "Erro ao converter PDF", e)
        null
    }
}

actual fun ImageBitmap.toByteArray(): ByteArray? {
    val bitmap = this.asAndroidBitmap()
    val stream = ByteArrayOutputStream()
    return if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
        stream.toByteArray()
    } else {
        null
    }
}

actual fun getAppVersionCode(): Int {
    return try {
        val context = AndroidContext.get()
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pInfo.longVersionCode.toInt()
        } else {
            @Suppress("DEPRECATION")
            pInfo.versionCode
        }
    } catch (e: Exception) {
        0
    }
}

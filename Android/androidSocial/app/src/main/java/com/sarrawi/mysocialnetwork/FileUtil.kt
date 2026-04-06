package com.sarrawi.mysocialnetwork

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtil {

    fun from(context: Context, uri: Uri): File? {
        val fileName = getFileName(uri)
        val file = File(context.cacheDir, fileName ?: "temp_file")

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return file
    }

    private fun getFileName(uri: Uri): String? {
        return uri.lastPathSegment?.substringAfterLast('/')
    }
}

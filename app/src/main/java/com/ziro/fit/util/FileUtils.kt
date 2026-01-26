package com.ziro.fit.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {

    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
            val extension = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            
            var fileName = getFileName(context, uri)
            if (extension != null && !fileName.endsWith(".$extension", ignoreCase = true)) {
                fileName = "$fileName.$extension"
            }
            
            val tempFile = File(context.cacheDir, fileName)
            // Delete if exists to ensure fresh copy
            if (tempFile.exists()) {
                tempFile.delete()
            }
            tempFile.createNewFile()

            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name = "temp_file"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        // Sanitize filename
        return name.replace("[^a-zA-Z0-9._-]".toRegex(), "_")
    }
}

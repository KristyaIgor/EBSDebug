@file:Suppress("unused")

package com.ebs.integrator.ebsdebug.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileManager {

    @Suppress("DEPRECATION")
    fun createEnvironmentFolder(environment: String, postFix: String): FileManagerStatus {
        val env = Environment.getExternalStoragePublicDirectory(environment)
        val folder = File("${env.absolutePath}/$postFix")
        return if (!folder.exists()) {
            if (folder.mkdirs()) {
                FileManagerStatus.ExistedFile(folder)
            } else {
                FileManagerStatus.FileCreationFailed
            }
        } else FileManagerStatus.ExistedFile(folder)
    }

    fun createExternalFile(context: Context): FileManagerStatus {
        val folder = context.filesDir
        return if (folder.exists()) FileManagerStatus.ExistedFile(folder) else FileManagerStatus.FileCreationFailed
    }

    fun createInternalFile(
        context: Context
    ): FileManagerStatus {
        val file = File("${context.filesDir.path}/screens/scene.jpg")
        return if (file.exists()) FileManagerStatus.ExistedFile(file) else run {
            file.createFileAndDirs()
            FileManagerStatus.ExistedFile(file)
        }
    }

    fun getInternalFile(
        context: Context
    ): FileManagerStatus {
        val file = File("${context.filesDir.path}/screens/scene.jpg")
        return if (file.exists()) FileManagerStatus.ExistedFile(file) else run {
            file.createFileAndDirs()
            FileManagerStatus.ExistedFile(file)
        }
    }

    fun getTimeStampForFileName(): String {
        val formatter = SimpleDateFormat("dd-MM-yy-HH-mm", Locale.US)
        return tryNull { formatter.format(Date()) } ?: UUID.randomUUID().toString().take(8)
    }

    fun provideSafeFileUri(file: File, activity: Activity): Uri {
        return FileProvider.getUriForFile(
            activity,
            activity.applicationContext.packageName + ".provider",
            file
        )
    }

    fun writeBitmapToFile(context: Context, bitmap: Bitmap) {
        File("${context.filesDir.path}/scene.jpg").apply {
            outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
            }
        }
    }

    /**
     * Important! Use only for direct insertion of files.
     */
    @Suppress("DEPRECATION")
    fun pingGalleryToTakeAnUpdate(context: Context, directory: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(directory)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

}

inline fun FileManagerStatus.guaranteeExistence(action: File.() -> Unit): FileManagerStatus {
    if (this is FileManagerStatus.ExistedFile) {
        action.invoke(file)
    }
    return this
}

inline fun FileManagerStatus.onFileCreationError(action: () -> Unit): FileManagerStatus {
    if (this.isFailed()) {
        action()
    }
    return this
}

inline fun FileManagerStatus.onFailRetryWith(action: () -> FileManagerStatus): FileManagerStatus {
    return if (this.isFailed()) {
        action()
    } else this
}

fun FileManagerStatus.unwrap(): File? {
    return when (this) {
        is FileManagerStatus.ExistedFile -> file
        FileManagerStatus.FileCreationFailed -> null
    }
}

fun FileManagerStatus.isFailed(): Boolean {
    return this in listOf(
        FileManagerStatus.FileCreationFailed
    )
}

fun File.createFileAndDirs() = apply {
    parentFile?.mkdirs()
    createNewFile()
}


sealed class FileManagerStatus {
    data class ExistedFile(val file: File) : FileManagerStatus()
    object FileCreationFailed : FileManagerStatus()
}
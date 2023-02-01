package com.ebs.integrator.ebsdebug

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.ebs.integrator.ebsdebug.interceptor.EbsInterceptor
import com.ebs.integrator.ebsdebug.interfaces.OnShakeListener
import com.ebs.integrator.ebsdebug.presentation.InfoFragment
import com.ebs.integrator.ebsdebug.utils.SDK
import java.io.File
import java.io.FileOutputStream
import java.util.*


class EBSDebug(val context: Context) {

    private var ebsShake: EbsShake = EbsShake(context)
    private var ebsInterceptorBuilder = EbsInterceptor.Builder(context)

    init {
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        SDK.init(context)

        ebsShake.setOnShakeListener(object : OnShakeListener {
            override fun onShake(count: Int) {
                if (count >= 2) {
                    Toast.makeText(context, "Shaked!!!", Toast.LENGTH_SHORT).show()
                    fragmentManager.apply {
                        for (fragment in fragments) {
                            if (fragment.tag == InfoFragment::class.simpleName) {
                                beginTransaction().remove(fragment).commit()
                            }
                        }
                    }

                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.add(
                        InfoFragment.newInstance(takeScreenshot(context)),
                        InfoFragment::class.simpleName
                    ).commit()
                }
            }
        })
    }

    private fun takeScreenshot(context: Activity): Uri? {
        var uri: Uri? = null
        try {
            // image naming and path  to include sd card  appending name you choose for file
            val mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/sceen.jpg"

            // create bitmap screen capture
            val v1: View = context.getWindow().getDecorView().getRootView()
            v1.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(v1.drawingCache)
            v1.isDrawingCacheEnabled = false
            val imageFile = File(mPath)
            if(!imageFile.exists())
                imageFile.createNewFile()
            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            uri = FileProvider.getUriForFile(context, SDK.fileProviderAuthority, imageFile)
//            uri = FileProvider.getUriForFile(
//                context,
//                "com.ebs.integrator.ebsdebug.provider",
//                imageFile)
        } catch (e: Throwable) {
            // Several error may come out with file handling or DOM
            e.printStackTrace()
        }
        return uri
    }

    fun getShake(): EbsShake {
        return ebsShake
    }

    fun getInterceptor(): EbsInterceptor.Builder {
        return ebsInterceptorBuilder
    }

}
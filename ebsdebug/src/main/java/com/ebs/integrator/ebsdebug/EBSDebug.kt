package com.ebs.integrator.ebsdebug

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.ebs.integrator.ebsdebug.interceptor.EbsInterceptor
import com.ebs.integrator.ebsdebug.interfaces.OnShakeListener
import com.ebs.integrator.ebsdebug.presentation.InfoFragment
import com.ebs.integrator.ebsdebug.utils.FileManager
import com.ebs.integrator.ebsdebug.utils.SDK
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.util.*


class EBSDebug(val context: Context) {

    private var ebsShake: EbsShake = EbsShake(context)
    private var ebsInterceptorBuilder = EbsInterceptor.Builder(context)

    init {
        val permission = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            permission.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        Dexter.withContext(context).withPermissions(permission)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    when (report.areAllPermissionsGranted()) {
                        true -> {

                        }
                        false -> {
                            Toast.makeText(
                                context,
                                "Some of permissions are disabled. Please go to Settings and enable them",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    Toast.makeText(
                        context,
                        "Some of permissions are disabled. Please go to Settings and enable them",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }).check()
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
                    val photo = takeScreenshot(context)
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.add(
                        InfoFragment.newInstance(),
                        InfoFragment::class.simpleName
                    ).commit()
                }
            }
        })
    }

    private fun takeScreenshot(context: Activity): Bitmap? {
        var bitmapPhoto: Bitmap? = null
        try {
            // image naming and path  to include sd card  appending name you choose for file
            val mPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/scene.png"

            // create bitmap screen capture
            val v1: View = context.window.decorView.rootView
            v1.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(v1.drawingCache)
            v1.isDrawingCacheEnabled = false
            val imageFile = File(mPath)
            if (imageFile.exists())
                imageFile.delete()

            imageFile.createNewFile()
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
//            uri = FileProvider.getUriForFile(
//                context,
//                "com.ebs.integrator.ebsdebug.provider",
//                imageFile)
        } catch (e: Throwable) {
            // Several error may come out with file handling or DOM
            e.printStackTrace()
        }
        return bitmapPhoto
    }

    fun getShake(): EbsShake {
        return ebsShake
    }

    fun getInterceptor(): EbsInterceptor.Builder {
        return ebsInterceptorBuilder
    }

}
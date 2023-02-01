package com.ebs.integrator.ebsdebug.presentation.device_info

import android.app.Dialog
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.ebs.integrator.ebsdebug.databinding.DeviceInfoDialogFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.text.SimpleDateFormat


class DeviceInfoFragment : BottomSheetDialogFragment() {

    val binding by lazy {
        DeviceInfoDialogFragmentBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textDeviceModel.text = Build.MANUFACTURER + "/" + Build.MODEL
        binding.textOSVersion.text = "SDK ${Build.VERSION.SDK}"
        binding.textDeviceId.text = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)

        var version = ""
        var versionCode = 0
        try {
            val pInfo = context?.packageManager?.getPackageInfo(context?.packageName!!, 0)
             version = pInfo?.versionName.toString()
            versionCode = pInfo?.versionCode!!
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }


        binding.textBuildDate.text = getAppTimeStamp(requireContext())
        binding.textPackage.text = context?.packageName
        binding.textVersionCode.text = versionCode.toString()
        binding.textVersionName.text = version
    }

    fun getAppTimeStamp(context: Context): String {
        var timeStamp = ""
        try {
            val appInfo: ApplicationInfo =
                context.packageManager.getApplicationInfo(context.packageName, 0)
            val appFile = appInfo.sourceDir
            val time: Long = File(appFile).lastModified()
            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            timeStamp = formatter.format(time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return timeStamp
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { layout ->
                val behaviour = BottomSheetBehavior.from(layout)
                setupFullHeight(layout)
                behaviour.skipCollapsed = true
                behaviour.isHideable = true
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    companion object {

        fun newInstance(): DeviceInfoFragment =
            DeviceInfoFragment()
//                .apply {
////                arguments = Bundle().apply {
////                    putString(ARG_ITEM_ID, billId)
////                }
//            }
    }

}
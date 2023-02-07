package com.ebs.integrator.ebsdebug.presentation.report

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.ebs.integrator.ebsdebug.databinding.ReportFragmentBinding
import com.ebs.integrator.ebsdebug.databinding.TeamworkDialogFragmentBinding
import com.ebs.integrator.ebsdebug.presentation.network.NetworkDetailsFragment
import com.ebs.integrator.ebsdebug.presentation.report.teamwork_task.TeamWorkFragment
import com.ebs.integrator.ebsdebug.presentation.report.teamwork_task.TeamWorkSignedFragment
import com.ebs.integrator.ebsdebug.utils.SDK
import com.ebs.integrator.ebsdebug.utils.TransferUtils
import com.ebs.integrator.ebsdebug.utils.log
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File


class ReportFragment : BottomSheetDialogFragment() {

    val binding by lazy {
        ReportFragmentBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemSendMail.setOnClickListener {
            context?.let {
                val mPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/scene.png"
                val mPathLogs =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/${it.packageName}-logs.txt"
                val imageFile = File(mPath)
                val logFile = File(mPathLogs)
                if(logFile.exists())
                    logFile.delete()
                logFile.createNewFile()

                TransferUtils.log.forEach { log ->
                    logFile.appendText(log, Charsets.UTF_8)
                }

                val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
                emailIntent.type = "vnd.android.cursor.dir/email"
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Bug Report")
                emailIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Report from application with id: ${context?.packageName}"
                )
                val imageUri = FileProvider.getUriForFile(it, SDK.fileProviderAuthority, imageFile)
                val logUri = FileProvider.getUriForFile(it, SDK.fileProviderAuthority, logFile)
//                emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
                val listUri = arrayListOf<Uri>()
                listUri.add(imageUri)
                listUri.add(logUri)
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listUri)

                try {
                    it.startActivity(
                        Intent.createChooser(
                            emailIntent,
                            "Send email using..."
                        )
                    )
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        it,
                        "No email clients installed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.itemCreateTask.setOnClickListener {
            if(!TransferUtils.teamWorkUserAuth){
                val fragment = TeamWorkFragment.newInstance()
                fragment.show(childFragmentManager, TeamWorkFragment::class.simpleName)
            }else{
                val fragment = TeamWorkSignedFragment.newInstance()
                fragment.show(childFragmentManager, TeamWorkSignedFragment::class.simpleName)
            }
        }
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

        fun newInstance(): ReportFragment =
            ReportFragment()
//                .apply {
//                arguments = Bundle().apply {
//                    putString("photo",takeScreenshot)
//                }
//            }
    }

}
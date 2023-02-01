package com.ebs.integrator.ebsdebug.presentation.report

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.ebs.integrator.ebsdebug.databinding.ReportFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


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
            val uri = arguments?.getString("photo")


            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "*/*"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, "")
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Bug Report")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Report from app ${context?.packageName}")
            // the attachment
            emailIntent .putExtra(Intent.EXTRA_STREAM, Uri.parse(uri))

            try {
                startActivity(
                    Intent.createChooser(
                        emailIntent,
                        "Send email using..."
                    )
                )
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    activity,
                    "No email clients installed.",
                    Toast.LENGTH_SHORT
                ).show()
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

        fun newInstance(takeScreenshot: String?): ReportFragment =
            ReportFragment().apply {
                arguments = Bundle().apply {
                    putString("photo",takeScreenshot)
                }
            }
    }

}
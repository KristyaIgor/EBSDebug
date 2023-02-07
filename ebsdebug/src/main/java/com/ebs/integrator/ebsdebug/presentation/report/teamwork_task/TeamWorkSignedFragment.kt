package com.ebs.integrator.ebsdebug.presentation.report.teamwork_task

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.ebs.integrator.ebsdebug.databinding.TeamworkDialogFragmentBinding
import com.ebs.integrator.ebsdebug.databinding.TeamworkSignedDialogFragmentBinding
import com.ebs.integrator.ebsdebug.utils.TransferUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class TeamWorkSignedFragment : BottomSheetDialogFragment() {

    val binding by lazy {
        TeamworkSignedDialogFragmentBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonLogin.setOnClickListener {
            dialogShowLogin("Autentificare", "Introdu login si parola in TeamWork!")
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

        fun newInstance(): TeamWorkSignedFragment =
            TeamWorkSignedFragment()
//                .apply {
//                arguments = Bundle().apply {
//                    putString("photo",takeScreenshot)
//                }
//            }
    }


    private fun dialogShowLogin(title: String, description: String?) {
        context?.let {
            DialogAction(it, title, description, "OK", "Renunta", {
                it.dismiss()
            }, {
                it.dismiss()

            }).show()
        }
    }
}
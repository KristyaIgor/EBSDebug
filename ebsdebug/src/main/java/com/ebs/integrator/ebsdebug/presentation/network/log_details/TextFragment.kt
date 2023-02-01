package com.ebs.integrator.ebsdebug.presentation.network.log_details

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ebs.integrator.ebsdebug.common.delegates.CompositeAdapter
import com.ebs.integrator.ebsdebug.common.delegates.DelegateAdapterItem
import com.ebs.integrator.ebsdebug.databinding.InfoDialogFragmentBinding
import com.ebs.integrator.ebsdebug.databinding.LogsDialogFragmentBinding
import com.ebs.integrator.ebsdebug.databinding.NetworkDialogFragmentBinding
import com.ebs.integrator.ebsdebug.databinding.RequestDetailsDialogFragmentBinding
import com.ebs.integrator.ebsdebug.databinding.TextFragmentBinding
import com.ebs.integrator.ebsdebug.logger.LogsRepository
import com.ebs.integrator.ebsdebug.presentation.network.items.ItemRequests
import com.ebs.integrator.ebsdebug.presentation.network.items.ItemRequestsBinder
import com.ebs.integrator.ebsdebug.presentation.network.items.ItemRequestsDelegate
import com.ebs.integrator.ebsdebug.utils.TransferUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TextFragment : BottomSheetDialogFragment() {

    val binding by lazy {
        TextFragmentBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textTitle.text = arguments?.getString("title")
        binding.logContent.text = arguments?.getString("data")
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

        fun newInstance(title:String, data: String): TextFragment =
            TextFragment()
                .apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("data", data)
                }
            }
    }

}
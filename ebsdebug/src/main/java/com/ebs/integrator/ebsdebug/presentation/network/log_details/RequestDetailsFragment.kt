package com.ebs.integrator.ebsdebug.presentation.network.log_details

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.ebs.integrator.ebsdebug.databinding.RequestDetailsDialogFragmentBinding
import com.ebs.integrator.ebsdebug.utils.TransferUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class RequestDetailsFragment : BottomSheetDialogFragment() {

    val binding by lazy {
        RequestDetailsDialogFragmentBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model = TransferUtils.getNetworkLogModel()
        model?.let { netModel ->
            binding.textUrl.text = netModel.request.url
            binding.textMethod.text = netModel.request.method
            binding.textResponseCode.text = netModel.response.code

            binding.textUrl.setOnClickListener{
                val clipboard =
                    context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", binding.textUrl.text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Copied: ${binding.textUrl.text}!", Toast.LENGTH_SHORT).show()
            }

            binding.itemRawRequestBody.setOnClickListener {
                val netDetails = TextFragment.newInstance("Request body", netModel.request.body)
                netDetails.show(childFragmentManager, TextFragment::class.simpleName)
            }

            binding.itemRawResponseBody.setOnClickListener {
                val netDetails = TextFragment.newInstance("Response body", netModel.response.body ?: "")
                netDetails.show(childFragmentManager, TextFragment::class.simpleName)
            }

            binding.itemRequestHeaders.setOnClickListener {
                val netDetails = TextFragment.newInstance("Request headers", netModel.request.headers)
                netDetails.show(childFragmentManager, TextFragment::class.simpleName)
            }

            binding.itemResponseHeaders.setOnClickListener {
                val netDetails = TextFragment.newInstance("Response headers", netModel.response.headers)
                netDetails.show(childFragmentManager, TextFragment::class.simpleName)
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

        fun newInstance(): RequestDetailsFragment =
            RequestDetailsFragment()
//                .apply {
////                arguments = Bundle().apply {
////                    putString(ARG_ITEM_ID, billId)
////                }
//            }
    }

}
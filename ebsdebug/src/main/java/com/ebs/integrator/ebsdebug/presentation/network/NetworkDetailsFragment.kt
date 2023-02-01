package com.ebs.integrator.ebsdebug.presentation.network

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ebs.integrator.ebsdebug.common.delegates.CompositeAdapter
import com.ebs.integrator.ebsdebug.common.delegates.DelegateAdapterItem
import com.ebs.integrator.ebsdebug.databinding.InfoDialogFragmentBinding
import com.ebs.integrator.ebsdebug.databinding.NetworkDialogFragmentBinding
import com.ebs.integrator.ebsdebug.logger.LogsRepository
import com.ebs.integrator.ebsdebug.presentation.device_info.DeviceInfoFragment
import com.ebs.integrator.ebsdebug.presentation.network.items.ItemRequests
import com.ebs.integrator.ebsdebug.presentation.network.items.ItemRequestsBinder
import com.ebs.integrator.ebsdebug.presentation.network.items.ItemRequestsDelegate
import com.ebs.integrator.ebsdebug.presentation.network.log_details.RequestDetailsFragment
import com.ebs.integrator.ebsdebug.utils.TransferUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NetworkDetailsFragment : BottomSheetDialogFragment() {

    val binding by lazy {
        NetworkDialogFragmentBinding.inflate(LayoutInflater.from(context))
    }

    val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(ItemRequestsDelegate { req ->
                TransferUtils.setNetworkLogModel(req)
                val netDetails = RequestDetailsFragment.newInstance()
                netDetails.show(childFragmentManager, RequestDetailsFragment::class.simpleName)
            })
            .build()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.list.layoutManager = GridLayoutManager(context, 1)

        binding.list.adapter = compositeAdapter

        lifecycleScope.launch(Dispatchers.Main) {
            val repo = LogsRepository(requireContext())
            val resp = repo.getRequestsModels().toMutableList()

            val adapter = mutableListOf<DelegateAdapterItem>()

            resp.forEach {
                adapter.add(
                    ItemRequestsBinder(
                        ItemRequests(
                            tag = "",
                            requestModel = it.request,
                            responseModel = it.response,
                            networkModel = it
                        )
                    )
                )
            }

            compositeAdapter.submitList(adapter)
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

        fun newInstance(): NetworkDetailsFragment =
            NetworkDetailsFragment()
//                .apply {
////                arguments = Bundle().apply {
////                    putString(ARG_ITEM_ID, billId)
////                }
//            }
    }

}
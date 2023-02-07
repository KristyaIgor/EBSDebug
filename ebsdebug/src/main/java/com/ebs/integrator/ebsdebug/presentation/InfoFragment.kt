package com.ebs.integrator.ebsdebug.presentation

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.ebs.integrator.ebsdebug.databinding.InfoDialogFragmentBinding
import com.ebs.integrator.ebsdebug.logger.Logger
import com.ebs.integrator.ebsdebug.logger.LogsRepository
import com.ebs.integrator.ebsdebug.presentation.device_info.DeviceInfoFragment
import com.ebs.integrator.ebsdebug.presentation.logs.LogsDetailsFragment
import com.ebs.integrator.ebsdebug.presentation.network.NetworkDetailsFragment
import com.ebs.integrator.ebsdebug.presentation.report.ReportFragment
import com.ebs.integrator.ebsdebug.utils.TransferUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.*


class InfoFragment : BottomSheetDialogFragment() {

    val binding by lazy {
        InfoDialogFragmentBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch(Dispatchers.Main) {
            val repo = LogsRepository(requireContext())
            val resp = repo.getRequestsModels().toMutableList()
            binding.textCountNetLogs.text = resp.size.toString()
//            binding.button.text = binding.button.text.toString() + ": " + resp.size
//            resp.forEach{
//                binding.textView.append("\n" + it.toString())
//            }
        }
        val logger = Logger("SupperLog")
        logger.message("fedsfsdfg")
        logger.warning("sfkjgndfkjgndfk")

        binding.itemNetLogs.setOnClickListener {
            val netDetails = NetworkDetailsFragment.newInstance()
            netDetails.show(childFragmentManager, NetworkDetailsFragment::class.simpleName)
        }

        binding.itemDeviceInfo.setOnClickListener {
            val netDetails = DeviceInfoFragment.newInstance()
            netDetails.show(childFragmentManager, DeviceInfoFragment::class.simpleName)
        }

        val log = mutableListOf<String>()
        lifecycleScope.launch(Dispatchers.IO) {
            val command = "logcat grep ${context?.packageName} *:E | *:W"
            Runtime.getRuntime().exec(command)
                .inputStream
                .bufferedReader()
                .useLines { lines ->
                    lines.forEach { newLine ->
                        log.add(newLine)
                    }
                }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            delay(250)
            binding.textCountLogs.text = "${log.size}"
            TransferUtils.setLogs(log)
        }

        binding.itemLogs.setOnClickListener {
            TransferUtils.setLogs(log)
            val logDetails = LogsDetailsFragment.newInstance()
            logDetails.show(childFragmentManager, LogsDetailsFragment::class.simpleName)
        }

        binding.itemReport.setOnClickListener {
            val logDetails = ReportFragment.newInstance()
            logDetails.show(childFragmentManager, ReportFragment::class.simpleName)
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
        fun newInstance(): InfoFragment = InfoFragment()
//            .apply {
//                arguments = Bundle().apply {
//                    putString("photo",takeScreenshot.toString())
//                }
//            }
    }

}
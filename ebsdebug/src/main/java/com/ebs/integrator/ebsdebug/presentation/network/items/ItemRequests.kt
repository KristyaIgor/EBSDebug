package com.ebs.integrator.ebsdebug.presentation.network.items

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ebs.integrator.ebsdebug.common.delegates.DelegateAdapterItem
import com.ebs.integrator.ebsdebug.common.delegates.DelegateBinder
import com.ebs.integrator.ebsdebug.common.delegates.Item
import com.ebs.integrator.ebsdebug.databinding.ItemNetworkRequestBinding
import com.ebs.integrator.ebsdebug.models.NetworkModel
import com.ebs.integrator.ebsdebug.models.RequestModel
import com.ebs.integrator.ebsdebug.models.ResponseModel
import java.text.SimpleDateFormat
import java.util.*

data class ItemRequests(
    val tag: String,
    val requestModel: RequestModel,
    val responseModel: ResponseModel,
    val networkModel: NetworkModel
) : Item

class ItemRequestsBinder(val item: ItemRequests) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemRequestsBinder) {
            payloads.apply {

            }
        }
        return payloads
    }

    sealed class Payloads : Payloadable {
        data class OnSumChanged(val number: Int, val sum: Double) : Payloads()
        data class OnTableChanged(val tableId: String) : Payloads()
    }
}

class ItemRequestsDelegate(
    private val onItemClick: (item: NetworkModel) -> Unit,
) :
    DelegateBinder<ItemRequestsBinder, ItemRequestsDelegate.ItemBillViewHolder>(
        ItemRequestsBinder::class.java
    ) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {

        val view = ItemNetworkRequestBinding.inflate(inflater, parent, false)

        return ItemBillViewHolder(view)
    }

    override fun bindViewHolder(
        model: ItemRequestsBinder,
        viewHolder: ItemBillViewHolder,
        payloads: List<DelegateAdapterItem.Payloadable>
    ) {
        if (payloads.isEmpty())
            viewHolder.bind(model.item)
        else {
            payloads.forEach {
                when (it) {
//                    is ItemBillBinder.Payloads.OnSumChanged -> {
//                        viewHolder.loadSum(it.number, it.sum)
//                    }
//                    is ItemBillBinder.Payloads.OnTableChanged -> {
//                        viewHolder.loadTable(it.tableId)
//                    }
                }
            }
            viewHolder.setClicks(model.item.networkModel)
        }
    }

    inner class ItemBillViewHolder(
        private val binding: ItemNetworkRequestBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemRequests) {
            binding.method.text = item.requestModel.method
            binding.code.text = item.responseModel.code
            if(item.responseModel.code.equals("200"))
                binding.code.setTextColor(Color.GREEN)
            else
                binding.code.setTextColor(Color.RED)
            binding.url.text = item.requestModel.url
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val mDate = formatter.format(item.requestModel.time)
            binding.data.text = mDate
            val dif = item.responseModel.time - item.requestModel.time
            binding.duration.text =  "${dif/1000.0}s"

            setClicks(item.networkModel)
        }

        fun setClicks(item: NetworkModel) {
           itemView.setOnClickListener {
                onItemClick.invoke(item)
            }
        }

    }
}

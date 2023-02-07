package com.ebs.integrator.ebsdebug.utils

import android.graphics.Bitmap
import com.ebs.integrator.ebsdebug.models.NetworkModel

object TransferUtils {

    val log = mutableListOf<String>()
    var networkModel: NetworkModel? = null
    var teamWorkUserAuth = false

    fun setLogs(logs: List<String>){
        log.clear()
        log.addAll(logs)
    }

    fun getLogs(): MutableList<String> {
        return log
    }

    fun setNetworkLogModel (model : NetworkModel){
        networkModel = model
    }

    fun getNetworkLogModel(): NetworkModel? {
        return networkModel
    }
}
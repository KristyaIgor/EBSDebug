package com.ebs.integrator.ebsdebug.utils

import android.content.Context

object SDK {
    lateinit var fileProviderAuthority: String
    fun init(context: Context) {
        fileProviderAuthority = "${context.packageName}.library.file.provider"
    }
}
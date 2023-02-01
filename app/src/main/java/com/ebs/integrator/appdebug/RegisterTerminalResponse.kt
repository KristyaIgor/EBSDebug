package com.ebs.integrator.appdebug

data class RegisterTerminalResponse(
    val DeviceId: String? = null,
    val DeviceNumber: Int = -1,
    val Result: Int = -1,
    val ResultMessage: String? = null
)

package com.ebs.integrator.appdebug

data class BillListResponse(
    val BillsList: List<String>,
    val Result: Int,
    val ResultMessage: String
)

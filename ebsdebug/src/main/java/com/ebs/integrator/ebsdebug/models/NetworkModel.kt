package com.ebs.integrator.ebsdebug.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkModel(
    @Json(name = "request")
    val request: RequestModel,
    @Json(name = "response")
    val response: ResponseModel
)
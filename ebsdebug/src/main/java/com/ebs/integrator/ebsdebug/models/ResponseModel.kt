package com.ebs.integrator.ebsdebug.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseModel(
    @Json(name = "time")
     val time: Long,
    @Json(name = "code")
     val code: String,
    @Json(name = "headers")
     val headers: String,
    @Json(name = "body")
     val body: String?,
)

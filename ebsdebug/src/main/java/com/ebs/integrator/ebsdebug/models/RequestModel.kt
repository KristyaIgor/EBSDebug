package com.ebs.integrator.ebsdebug.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestModel(
    @Json(name = "time")
     val time: Long,
    @Json(name = "method")
     val method: String,
    @Json(name = "url")
     val url: String,
    @Json(name = "headers")
     val headers: String,
    @Json(name = "body")
     val body: String,
)

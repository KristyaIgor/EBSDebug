package com.ebs.integrator.appdebug

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface RemoteApiInterface {

    @GET("/MobileCash/json/Ping")
    suspend fun ping(): Response<Boolean>

    @GET("/MobileCash/json/RegisterTerminal")
    suspend fun registerTerminal(@Query("deviceId") deviceId: String): Response<RegisterTerminalResponse>

    @POST("/MobileCash/json/SaveBill")
    suspend fun addOrders(
        @Body orders: List<String>
    ): Response<BillListResponse>

}
package com.ebs.integrator.ebsdebug.interceptor

import android.content.Context
import android.util.Log
import com.ebs.integrator.ebsdebug.enums.EbsLevel
import com.ebs.integrator.ebsdebug.logger.Logger
import com.ebs.integrator.ebsdebug.models.NetworkModel
import com.ebs.integrator.ebsdebug.models.RequestModel
import com.ebs.integrator.ebsdebug.models.ResponseModel
import com.ebs.integrator.ebsdebug.logger.LogsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import okio.IOException
import java.util.*


class EbsInterceptor : Interceptor {
    private var httpLoggingInterceptor: HttpLoggingInterceptor
    private  var context: Context

    constructor(context: Context,httpLoggingInterceptor: HttpLoggingInterceptor) {
        this.httpLoggingInterceptor = httpLoggingInterceptor
        this.context = context
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        val requestModel = RequestModel(
            time = Date().time,
            method = request.method,
            url = request.url.toString(),
            headers = request.headers.toString(),
            body = stringifyRequestBody(request)
        )

        Log.e("Request", requestModel.toString())


        val response = chain.proceed(request)
        val responseModel = ResponseModel(
            time = Date().time,
            code = response.code.toString(),
            headers = response.headers.toString(),
            body = response.body?.string()
        )
        val repo = LogsRepository(context)
        CoroutineScope(Dispatchers.IO).launch {
            val currentModels = repo.getRequestsModels().toMutableList()
            currentModels.add(NetworkModel(requestModel, responseModel))
            repo.addRequestModel(currentModels)
        }

        Log.e("Response", responseModel.toString())

        return response
    }

    class Builder(val context: Context) {
        private val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()

        fun setLevelInterceptor(level: EbsLevel): Builder {
            loggingInterceptor.setLevel(level.value)
            return this
        }

        fun build(): EbsInterceptor {
            return EbsInterceptor(context ,loggingInterceptor)
        }
    }

    private fun stringifyRequestBody(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            return "Cannot parse body"
        }
    }
}
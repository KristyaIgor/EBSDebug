package com.ebs.integrator.ebsdebug.interceptor

import android.content.Context
import android.util.Log
import com.ebs.integrator.ebsdebug.enums.EbsLevel
import com.ebs.integrator.ebsdebug.logger.LogsRepository
import com.ebs.integrator.ebsdebug.models.NetworkModel
import com.ebs.integrator.ebsdebug.models.RequestModel
import com.ebs.integrator.ebsdebug.models.ResponseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import okio.IOException
import java.util.*


class EbsInterceptor : Interceptor {
    private  var context: Context

    constructor(context: Context) {
        this.context = context
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        val response = chain.proceed(request)
        val repo = LogsRepository(context)

        val contentType: MediaType? = response.body!!.contentType()
        val bodyString = response.body?.string()

        CoroutineScope(Dispatchers.IO).launch {
            val requestModel = RequestModel(
                time = Date().time,
                method = request.method,
                url = request.url.toString(),
                headers = request.headers.toString(),
                body = stringifyRequestBody(request)
            )
            Log.e("Request", requestModel.toString())

            val responseModel = ResponseModel(
                time = Date().time,
                code = response.code.toString(),
                headers = response.headers.toString(),
                body = bodyString
            )
            Log.e("Response", responseModel.toString())

            val currentModels = repo.getRequestsModels().toMutableList()
            currentModels.add(NetworkModel(requestModel, responseModel))
            repo.addRequestModel(currentModels)
        }
        val body = bodyString?.toResponseBody(contentType)
        return response.newBuilder().body(body).build()
    }

    class Builder(val context: Context) {
        private val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()

        fun setLevelInterceptor(level: EbsLevel): Builder {
            loggingInterceptor.setLevel(level.value)
            return this
        }

        fun build(): EbsInterceptor {
            return EbsInterceptor(context)
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
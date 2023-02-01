package com.ebs.integrator.ebsdebug.utils

import com.ebs.integrator.ebsdebug.utils.adapters.JsonObjectAdapter
import com.ebs.integrator.ebsdebug.utils.adapters.ListJsonAdapter
import com.squareup.moshi.Moshi
import org.json.JSONObject

object MoshiProvider {

    val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(JsonObjectAdapter())
            .add(ListJsonAdapter.FACTORY)
            .build()
    }

}

fun getMoshi() = MoshiProvider.moshi

inline fun <reified T> inlineInString(data: T): String {
    return getMoshi().adapter(T::class.java).toJson(data)
}

inline fun <reified T> inlineInJson(data: T): JSONObject {
    return JSONObject(getMoshi().adapter(T::class.java).toJson(data))
}

@JvmName("inlineInJson1")
inline fun <reified T> T.inlineInJson(): JSONObject {
    return JSONObject(getMoshi().adapter(T::class.java).toJson(this))
}
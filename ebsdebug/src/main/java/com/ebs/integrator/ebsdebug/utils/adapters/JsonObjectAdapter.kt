package com.ebs.integrator.ebsdebug.utils.adapters

import com.ebs.integrator.ebsdebug.models.NetworkModel
import com.ebs.integrator.ebsdebug.models.ResponseModel
import com.squareup.moshi.*
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject

class JsonObjectAdapter {

    @Suppress("UNCHECKED_CAST")
    @FromJson
    fun fromJson(reader: JsonReader): JSONObject? {
        // Here we're expecting the JSON object, it is processed as Map<String, Any> by Moshi
        return (reader.readJsonValue() as? Map<String, Any>)?.let { data ->
            try {
                JSONObject(data)
            } catch (e: JSONException) {
                // Handle error if arises
                null
            }
        }
    }

    @FromJson
    fun fromJson(reader: JsonReader, jsonAdapter: JsonAdapter<NetworkModel>): List<NetworkModel> {
        val list = ArrayList<NetworkModel>()
        if (reader.hasNext()) {
            val token = reader.peek()
            if (token == JsonReader.Token.BEGIN_ARRAY) {
                reader.beginArray()
                while (reader.hasNext()) {
                    val yourResponse = jsonAdapter.fromJsonValue(reader.readJsonValue())
                    yourResponse?.let {
                        list.add(yourResponse)
                    }
                }
                reader.endArray()
            }
        }
        return list.toList()
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: JSONObject?) {
        value?.let { writer.value(Buffer().writeUtf8(value.toString())) }
    }

    inline fun <reified T> Moshi.parseList(jsonString: String): List<T>? {
        return adapter<List<T>>(Types.newParameterizedType(List::class.java, T::class.java)).fromJson(jsonString)
    }
}
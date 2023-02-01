package com.ebs.integrator.ebsdebug.logger

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ebs.integrator.ebsdebug.models.NetworkModel
import com.ebs.integrator.ebsdebug.utils.getMoshi
import com.example.igor.restaurantmobile.data.datastore.DataStoreProvider
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


private val Context.settings: DataStore<Preferences> by preferencesDataStore(name = "logs")

class LogsRepository (private val context: Context) : DataStoreProvider() {

    override fun provideDataStore(): DataStore<Preferences> {
        return context.settings
    }

    suspend fun addRequestModel(value: List<NetworkModel>) {
        edit {
            val key = stringPreferencesKey(NETWORK)
            val listMyData = Types.newParameterizedType(List::class.java, NetworkModel::class.java)
            val jsonAdapter = getMoshi().adapter<List<NetworkModel>>(listMyData)
//            val adapter = getMoshi().adapter(LogModel::class.java)
            it[key] = jsonAdapter.toJson(value)
        }
    }

    suspend fun getRequestsModels(): List<NetworkModel>{
        return getFlow {
            val key = stringPreferencesKey(NETWORK)
            val listMyData = Types.newParameterizedType(List::class.java, NetworkModel::class.java)
            val jsonAdapter = getMoshi().adapter<List<NetworkModel>>(listMyData)
            it[key]?.let { data -> jsonAdapter.fromJson(data) } ?: emptyList()
        }.first()
    }

//    fun getRequestModels(): Flow<LogModel?> {
//        return getFlow {
//            val key = stringPreferencesKey("requests")
//            val adapter = getMoshi().adapter(LogModel::class.java)
//            it[key]?.let { data -> adapter.fromJson(data) }
//        }
//    }


    fun getLicense(): Flow<String> {
        return getFlow {
            val key = stringPreferencesKey(NETWORK)
            it[key] ?: ""
        }
    }

    suspend fun setLicense(value: String) {
        edit {
            val key = stringPreferencesKey(NETWORK)
            it[key] = value
        }
    }


    private companion object {
        //KEYS
        const val NETWORK = "net"
    }
}
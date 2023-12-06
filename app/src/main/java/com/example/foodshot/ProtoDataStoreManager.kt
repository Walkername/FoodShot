package com.example.foodshot

import android.content.Context
import androidx.datastore.dataStore

private val Context.protoDataStore by dataStore("actions.json", ActionsSerializer)
class ProtoDataStoreManager(val context: Context) {

    suspend fun saveActions(actionsData: ActionsData) {
        context.protoDataStore.updateData {
            actionsData
        }
    }
    fun getActions() = context.protoDataStore.data
}
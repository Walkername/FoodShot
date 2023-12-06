package com.example.foodshot

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object ActionsSerializer : Serializer<ActionsData> {
    override val defaultValue: ActionsData
        get() = ActionsData(mutableListOf())

    override suspend fun readFrom(input: InputStream): ActionsData {
        return try {
            Json.decodeFromString(
                deserializer = ActionsData.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            ActionsData(mutableListOf())
        }
    }

    override suspend fun writeTo(t: ActionsData, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(
                    serializer = ActionsData.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}
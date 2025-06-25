package com.linari.data.common.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

class StringOrObjectDeserializer : JsonDeserializer<Any> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Any {
        return try {
            if (json.isJsonObject) {
                context.deserialize<JsonObject>(json, JsonObject::class.java)
            } else {
                json.asString
            }
        } catch (e: Exception) {
            json.asString
        }
    }
}

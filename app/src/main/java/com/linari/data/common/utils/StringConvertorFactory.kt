package com.linari.data.common.utils

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class StringConverterFactory(private val gson: Gson) : Converter.Factory() {

    companion object {
        fun create(gson: Gson): StringConverterFactory {
            return StringConverterFactory(gson)
        }
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {

        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonConverter(gson,adapter)
    }
}

class GsonConverter<T:Any>(private val gson: Gson,private val adapter: TypeAdapter<T>) : Converter<ResponseBody,T> {
    override fun convert(p0: ResponseBody): T {
        return p0.use {
            val contentType = p0.contentType()?.toString()

            if (contentType == null || !contentType.contains("application/json")) {
                if (contentType != null && contentType.contains("text/html")) {
                    throw NoJsonException()
                } else {
                    throw IllegalArgumentException()
                }
            }
            val reader = gson.newJsonReader(it.charStream())
            val result = adapter.read(reader)
            if (reader.peek() != JsonToken.END_DOCUMENT){
                throw JsonIOException("Json not fully consumed")
            }
            result
        }
    }
}
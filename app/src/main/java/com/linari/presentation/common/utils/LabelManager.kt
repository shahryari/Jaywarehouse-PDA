package com.linari.presentation.common.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

object LabelManager {
    private val _labels = MutableStateFlow<Map<String, String>>(emptyMap())
    val labels = _labels.asStateFlow()

    fun loadFromJson(json: String) {
        val map = mutableMapOf<String, String>()
        val jsonObject = JSONObject(json)
        for (key in jsonObject.keys()){
            map[key] = jsonObject.getString(key)
        }
        _labels.value = map
    }


    fun simpleGet(key: String) : String {
        val map  = _labels.value
        Log.i("language labels", "get: $map")
        return map.getOrElse(key) {

            if (!map.containsKey(key)) {
                val newMap = map.toMutableMap()
                newMap[key] = key
                _labels.value = newMap
            }

            key
        }
    }

    @Composable
    fun get(key: String) : String {
        val map by labels.collectAsState()
        Log.i("language labels", "get: $map")
        return map.getOrElse(key) {

            if (!map.containsKey(key)) {
                val newMap = map.toMutableMap()
                newMap[key] = key
                _labels.value = newMap
            }

            key
        }
    }

    fun toJson() : String {
        return JSONObject(_labels.value as Map<*,*>).toString()
    }
}


@Composable
fun getLabelOf(key: String) : String {
    return LabelManager.get(key)
}
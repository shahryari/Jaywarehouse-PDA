package com.example.jaywarehouse.data.common.utils

import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.datastore.core.DataStoreFactory
import com.example.jaywarehouse.localWindowFactor

val Int.mdp: Dp @Composable get(){
    val factor = localWindowFactor.current
    return Dp(value = this.toFloat()*factor)
}

val Double.mdp: Dp
    @Composable get(){
    val factor = localWindowFactor.current
    return Dp(value = this.toFloat()*factor)
}


fun Context.restartActivity(){
    val intent = this.packageManager.getLaunchIntentForPackage(this.packageName)
    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    this.startActivity(intent)
}

fun <T> List<T>.addAll(list: List<T>?) : List<T>{
    val tempList = this.toMutableList()
    if (list!=null) for (item in list){
        if (!tempList.contains(item)) tempList.add(item)
    }
    return tempList
}

fun String.endsWithEnter(): Boolean {
    return this.endsWith('\n')
}

fun Double.removeZeroDecimal() : String {
    val df = DecimalFormat("#.##")
    return df.format(this)
}

fun String.withEnglishDigits() : String {
    var result = ""
    for (char in this){
        if (char.isDigit()){
            result += char.digitToInt().toString()
        }else {
            result += char
        }
    }
    return result
}
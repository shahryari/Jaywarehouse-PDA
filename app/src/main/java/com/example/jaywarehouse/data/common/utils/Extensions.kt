package com.example.jaywarehouse.data.common.utils

import android.content.Context
import android.content.Intent
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
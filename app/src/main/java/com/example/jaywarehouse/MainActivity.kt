package com.example.jaywarehouse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.presentation.NavGraphs
import com.example.jaywarehouse.presentation.destinations.DashboardScreenDestination
import com.example.jaywarehouse.presentation.destinations.LoginScreenDestination
import com.example.jaywarehouse.presentation.destinations.MainScreenDestination
import com.example.jaywarehouse.ui.theme.JayWarehouseTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import org.koin.compose.koinInject

val localWindowFactor = staticCompositionLocalOf { 1f }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            val prefs : Prefs = koinInject()

            val width = LocalConfiguration.current.screenWidthDp
            val factor = when{
                width<300 -> 0.9f
                width<400 -> 0.95f
                width<450 -> 0.97f
                width<500 -> 1f
                width<600 -> 1.02f
                width<700 -> 1.05f
                width<800 -> 1.1f
                else -> 1.15f
            }
            val route = if (prefs.getToken().isNotEmpty()) DashboardScreenDestination else LoginScreenDestination
            JayWarehouseTheme {
                CompositionLocalProvider(localWindowFactor provides factor) {
                    DestinationsNavHost(navGraph = NavGraphs.root, startRoute = route)
                }
            }
        }
    }
}

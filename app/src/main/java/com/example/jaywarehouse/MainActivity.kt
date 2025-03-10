package com.example.jaywarehouse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.presentation.NavGraphs
import com.example.jaywarehouse.presentation.destinations.DashboardScreenDestination
import com.example.jaywarehouse.presentation.destinations.LoginScreenDestination
import com.example.jaywarehouse.ui.theme.JayWarehouseTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import org.koin.compose.koinInject

val localWindowFactor = staticCompositionLocalOf { 1f }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            val prefs : Prefs = koinInject()

            val factor = getScaleFactor()
            val route = if (prefs.getToken().isNotEmpty()) DashboardScreenDestination else LoginScreenDestination
            JayWarehouseTheme {
                CompositionLocalProvider(localWindowFactor provides factor) {
                    DestinationsNavHost(navGraph = NavGraphs.root, startRoute = route)
                }
            }
        }
    }
}

@Composable
fun getScaleFactor() : Float {
    val configuration = LocalConfiguration.current

    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    val densityDpi = configuration.densityDpi
    val density = configuration.fontScale  // or density, depending on use case

    // Reference values (Based on a standard phone, e.g., Pixel 4a)
    val baseWidth = 411f
    val baseHeight = 891f
    val baseDpi = 420f  // Default mdpi baseline

    // Calculate scaling components
    val widthFactor = screenWidthDp / baseWidth
    val heightFactor = screenHeightDp / baseHeight
    val dpiFactor = densityDpi / baseDpi
    val densityFactor = density  // Uses system font scale

    // Combine all factors (weighted average)
    val scaleFactor = ((widthFactor * 0.4f) + (heightFactor * 0.4f) + (dpiFactor * 0.2f)) / densityFactor

    // Limit scaling to prevent excessive growth
    return scaleFactor.coerceIn(0.9f, 1.1f)// Restricts scaling to a safe range
}

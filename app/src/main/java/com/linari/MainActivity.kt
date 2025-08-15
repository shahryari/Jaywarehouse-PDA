package com.linari

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.OnSuccessListener
import com.linari.data.auth.AuthRepository
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.presentation.NavGraphs
import com.linari.presentation.common.utils.LabelManager
import com.linari.presentation.destinations.DashboardScreenDestination
import com.linari.presentation.destinations.LoginScreenDestination
import com.linari.ui.theme.JayWarehouseTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject
import org.koin.java.KoinJavaComponent.inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

val localWindowFactor = staticCompositionLocalOf { 1f }
class MainActivity : ComponentActivity() {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private var lastLocation: Location? = null
    private var isLocaionUpdatesStarted: Boolean = false

    private var isTrackingEnabled : MutableStateFlow<Boolean> = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    getLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    getLocation()
                }
                else -> {}

            }
        }




        val prefs = Prefs(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                prefs.getTracking().collect {
                    if (prefs.getToken().isNotEmpty())  {
                        if (it){

                            if (ActivityCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                locationPermissionRequest.launch(arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION))
                            } else {
                                getLocation()
                            }
                        } else {
                            stopUpdates()
                        }
                    }
                }
            }
        }
        setContent {


            val labels by prefs.getLabels().collectAsState("{}")

            LaunchedEffect(labels) {
                Log.i("language labels", "onCreate: $labels")
                LabelManager.loadFromJson(labels)
            }
            val scope = rememberCoroutineScope()

            DisposableEffect(Unit) {
                onDispose {
                    scope.launch(Dispatchers.IO) {
                        prefs.setLabels(LabelManager.toJson())
                    }
                }
            }

            val factor = getScaleFactor()
            val route = if (prefs.getToken().isNotEmpty()) DashboardScreenDestination else LoginScreenDestination
            JayWarehouseTheme {
                CompositionLocalProvider(localWindowFactor provides factor ) {
                    DestinationsNavHost(navGraph = NavGraphs.root, startRoute = route)
                }
            }
        }
    }

    fun getLocation() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,20000)
            .setIntervalMillis(20000)
            .setMinUpdateDistanceMeters(10f)
            .build()

        val prefs = Prefs(this)

        val repository : AuthRepository by inject<AuthRepository>()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest?:return)

        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnFailureListener {exception ->

            if (exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(this@MainActivity,
                        1002)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult) {
                lifecycleScope.launch(Dispatchers.IO){
                    val newLocation = location.lastLocation ?: return@launch
                    if (lastLocation!= null){
                        val distance = lastLocation!!.distanceTo(newLocation)
                        if (distance>10f){
                            launch(Dispatchers.IO){
                                repository.createVehicleTracking(
//                                    prefs.getUserID(),
//                                    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Calendar.getInstance().time),
                                    newLocation.latitude,
                                    newLocation.longitude,
                                    newLocation.speed
                                ).collect {
                                    when(it){
                                        is BaseResult.Error -> {}
                                        is BaseResult.Success -> {}
                                        BaseResult.UnAuthorized -> {}
                                    }
                                }
                            }
                        }
                    }
                    prefs.setLatitude(newLocation.latitude.toString())
                    prefs.setLongitude(newLocation.longitude.toString())
                }
            }


        }

        task.addOnSuccessListener(OnSuccessListener<LocationSettingsResponse> { startLocationUpdates() })


        startLocationUpdates()

    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
//            locationPermissionRequest.launch(arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION))

            return
        }
        isLocaionUpdatesStarted = true
        if (fusedLocationProviderClient != null && locationCallback != null && locationRequest != null) {
            fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest?:return,
                locationCallback?:return,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopUpdates() {
        if (fusedLocationProviderClient != null && locationCallback != null) {

            fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        stopUpdates()
        lifecycleScope.launch(Dispatchers.IO) {
            Prefs(this@MainActivity).setLabels(LabelManager.toJson())
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

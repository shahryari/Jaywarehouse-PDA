package com.example.jaywarehouse.presentation.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.common.utils.restartActivity
import com.example.jaywarehouse.presentation.NavGraphs
import com.example.jaywarehouse.presentation.appCurrentDestinationAsState
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.utils.DashboardItem
import com.example.jaywarehouse.presentation.common.utils.MainGraph
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.destinations.CountingDetailScreenDestination
import com.example.jaywarehouse.presentation.destinations.CountingScreenDestination
import com.example.jaywarehouse.presentation.destinations.PackingDetailScreenDestination
import com.example.jaywarehouse.presentation.destinations.ProfileScreenDestination
import com.example.jaywarehouse.presentation.destinations.PutawayDetailScreenDestination
import com.example.jaywarehouse.presentation.destinations.TypedDestination
import com.example.jaywarehouse.presentation.startAppDestination
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import org.koin.androidx.compose.koinViewModel


@MainGraph(start = true)
@Destination
@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val currentDestination: TypedDestination<out Any?> = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination
    val context = LocalContext.current


    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                is MainContract.Effect.RestartActivity -> {
                    context.restartActivity()
                }

                is MainContract.Effect.OpenUpdateUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.uri))
                    context.startActivity(intent)
                }

                MainContract.Effect.Exit -> {
                    (context as Activity).finish()
                }
            }
        }
    }

    MainContent(navController = navController, currentDestination = currentDestination,viewModel.state,viewModel::setEvent)
}

@Composable
fun MainContent(
    navController: NavHostController = rememberNavController(),
    currentDestination: TypedDestination<*>,
    state: MainContract.State = MainContract.State(),
    onEvent: (MainContract.Event) -> Unit
) {
    val showAppbar = when(currentDestination){
        is PutawayDetailScreenDestination,
            CountingDetailScreenDestination,
            CountingScreenDestination,
            PackingDetailScreenDestination,
            ProfileScreenDestination->false
        else -> true
    }
    MyScaffold {
        Box(modifier = Modifier.fillMaxSize()){
            Column(Modifier.fillMaxSize()) {
                if (showAppbar)Row(
                    Modifier
                        .fillMaxWidth()
                        .focusable(false)
                        .clip(RoundedCornerShape(5.mdp))
                        .clickable {
                            navController.navigate(ProfileScreenDestination)
                        }
                        .padding(15.mdp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_user),
                            contentDescription ="",
                            Modifier.size(40.mdp)
                        )
                        Spacer(modifier = Modifier.size(12.mdp))
                        Column(Modifier) {
                            MyText(
                                text = state.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Row (verticalAlignment = Alignment.CenterVertically){
                                MyText(
                                    text = "Shams abad warehouse",
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.size(7.mdp))
                                Icon(
                                    Icons.Filled.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(10.mdp))
                        .background(Black)
                        .clickable {
                            navController.navigate(ProfileScreenDestination)
                        }
                        .padding(12.mdp)){
                        Icon(
                            painter = painterResource(id = R.drawable.equal),
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }
//                DestinationsNavHost(
//                    navGraph = NavGraphs.mainGraph,
//                    navController = navController,
//                )

            }
            if(showAppbar)Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 15.mdp, vertical = 15.mdp)
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(Black)
                    .padding(vertical = 7.mdp, horizontal = 10.mdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DashboardItem.entries.forEach { item ->
                    val selected = item.subDestinations.find { it == currentDestination }
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(if (selected != null) Orange else Color.DarkGray)
                            .clickable {
                                if (item.destination != null) navController.navigate(item.destination) {
                                    launchSingleTop = true
                                }
                            }
                            .padding(6.mdp)
                    ) {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = null,
                            tint = if (selected!=null) Black else Gray3
                        )
                    }
                }
            }
        }
    }
    if (state.showUpdateDialog){
        UpdateDialog(state = state, onEvent = onEvent )
    }
}

@Composable
fun UpdateDialog(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit
) {
    Dialog(
        onDismissRequest = {
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 30.mdp), contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.mdp))
                    .background(Black)
                    .padding(12.mdp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(40.mdp))
                MyText(
                    text = "New version is available please update your app.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyText(
                    text = "Current Version: ${state.currentVersion}",
                    color = Color.White.copy(0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.size(5.mdp))
                MyText(
                    text = "New Version: ${state.newVersion}",
                    color = Color.White.copy(0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.size(30.mdp))
                Row(Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            onEvent(MainContract.Event.OnExit)
                        },
                        shape = RoundedCornerShape(20.mdp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray
                        ),
                        contentPadding = PaddingValues(15.mdp),
                        modifier = Modifier.weight(1f)
                    ) {
                        MyText(
                            text = "Exit",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Spacer(modifier = Modifier.size(5.mdp))
                    Button(
                        onClick = {
                            onEvent(MainContract.Event.OnUpdate)
                        },
                        shape = RoundedCornerShape(20.mdp),
                        contentPadding = PaddingValues(vertical = 15.mdp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        MyText(
                            text = "Update",
                            color = Black,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

            }
        }
    }
}

@Preview
@Composable
private fun MainPreview() {
    MainContent(currentDestination = CountingScreenDestination){}
}
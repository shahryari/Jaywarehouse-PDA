package com.example.jaywarehouse.presentation.dashboard

import android.opengl.Visibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.jaywarehouse.MainActivity
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.auth.models.DashboardModel
import com.example.jaywarehouse.data.common.utils.restartActivity
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MySwitch
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.utils.MainItems
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.destinations.CountingScreenDestination
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.ErrorRed
import com.example.jaywarehouse.ui.theme.Gray4
import com.example.jaywarehouse.ui.theme.Primary
import com.example.jaywarehouse.ui.theme.Red
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Destination(style = ScreenTransition::class)
@Composable
fun DashboardScreen(
    navigator: DestinationsNavigator,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent
    val activity = LocalContext.current as MainActivity
    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                is DashboardContract.Effect.Navigate -> navigator.navigate(it.destination)
                DashboardContract.Effect.RestartActivity -> {
                    activity.restartActivity()
                }
            }
        }

    }

    DashboardContent(state,onEvent = onEvent)
}



@Composable
private fun DashboardContent(
    state: DashboardContract.State = DashboardContract.State(),
    onEvent: (DashboardContract.Event)->Unit = {}
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        onEvent(DashboardContract.Event.FetchData)
    }
    MyScaffold(refreshable = false) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(drawerShape = RectangleShape, drawerContainerColor = Color.White) {
                    Column(
                        Modifier
                            .fillMaxWidth(0.8f)
                            .padding(12.mdp)) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .shadow(5.mdp, RoundedCornerShape(8.mdp))
                                .clip(RoundedCornerShape(8.mdp))
                                .background(Color.White)
                                .padding(8.mdp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painterResource(R.drawable.user_prof),
                                contentDescription = "",
                                modifier = Modifier.size(40.mdp),
                                tint = Primary
                            )
                            Spacer(modifier = Modifier.size(12.mdp))
                            MyText(
                                text = state.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.W500,
                                color = Primary
                            )
                        }
                        Spacer(modifier = Modifier.size(12.mdp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .shadow(5.mdp, RoundedCornerShape(8.mdp))
                                .clip(RoundedCornerShape(8.mdp))
                                .background(Color.White)
                                .padding(5.mdp)
                        ) {
                            DashboardTab.entries.forEach {
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.mdp))
                                        .background(if (state.selectedTab == it) Primary else Color.Transparent)
                                        .clickable {
                                            onEvent(DashboardContract.Event.OnSelectTab(it))
//                                            scope.launch {
//                                                drawerState.close()
//                                            }
                                        }
                                        .padding(8.mdp),
                                    contentAlignment = Alignment.Center

                                ) {
                                    MyText(
                                        text = it.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.W500,
                                        color = if (state.selectedTab == it) Color.White else Primary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(15.mdp))
                        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            AnimatedContent(state.subDrawerState, label = "drawer Animation") { subState ->
                                when(subState){
                                    SubDrawerState.Drawers -> {
                                        LazyColumn(Modifier.fillMaxWidth()) {
                                            item("Home"){

                                                DrawerItem(
                                                    title = "Home",
                                                    icon = R.drawable.home_02,
                                                    selected = true,
                                                    modifier = Modifier.animateEnterExit(),
                                                    onClick = {

                                                    }
                                                )
                                            }
                                            if (state.selectedTab == DashboardTab.Picking){
                                                items(state.dashboards.toList(),key = {it.first}){
                                                    DrawerItem(
                                                        title = it.first,
                                                        icon = R.drawable.truck_next,
                                                        modifier = Modifier.animateEnterExit(),
                                                        onClick = {
                                                            onEvent(DashboardContract.Event.OnShowSubDrawers(it.second))
                                                        }
                                                    )
                                                }
                                            } else {
                                                items(state.crossDockDashboards.values.first()){
                                                    DrawerItem(
                                                        it.title.replace('\n',' '),
                                                        it.icon,
                                                        modifier = Modifier.animateEnterExit(
                                                            enter = slideInHorizontally(),
                                                            exit = slideOutHorizontally()
                                                        ),
                                                    ) {
                                                        when(it){
                                                            MainItems.Receiving -> {
                                                                onEvent(DashboardContract.Event.OnNavigate(
                                                                    CountingScreenDestination(isCrossDock = true)))
                                                            }
                                                           else -> {}
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    SubDrawerState.SubDrawers -> {
                                        if (state.subDrawers!=null)LazyColumn(Modifier.fillMaxWidth()) {
                                            item(key = "Main Menu") {
                                                DrawerItem(
                                                    title = "Main Menu",
                                                    icon = R.drawable.category_2,
                                                    selected = true,
                                                    modifier = Modifier.animateEnterExit(),
                                                    onClick = {
                                                        onEvent(DashboardContract.Event.OnShowSubDrawers(null))
                                                    }
                                                )
                                            }
                                            items(state.subDrawers, key = {
                                                it.title
                                            }){

                                                DrawerItem(
                                                    title = it.title.replace('\n',' '),
                                                    icon = it.icon,
                                                    modifier = Modifier.animateEnterExit(
                                                        enter = slideInHorizontally(),
                                                        exit = slideOutHorizontally()
                                                    ),
                                                    onClick = {
                                                        if (it.destination!=null){
                                                            onEvent(DashboardContract.Event.OnNavigate(it.destination))
                                                        }
                                                        onEvent(DashboardContract.Event.OnShowSubDrawers(null))
                                                    }
                                                )
                                            }
                                        }

                                    }
                                    SubDrawerState.Settings -> {
                                        Column {
                                            DrawerItem(
                                                title = "Main Menu",
                                                icon = R.drawable.category_2,
                                                selected = true,
                                                modifier = Modifier.animateEnterExit(),
                                                onClick = {
                                                    onEvent(DashboardContract.Event.ShowSettings(false))
                                                }
                                            )
                                            HorizontalDivider(color = Gray4)
                                            SettingDrawer(
                                                title = "Change password",
                                                icon = R.drawable.unlock_3,
                                                showSwitch = false,
                                                onClick = {

                                                }
                                            )
                                            HorizontalDivider(color = Gray4)
                                            SettingDrawer(
                                                title = "Forward ot dashboard",
                                                icon = R.drawable.home_02,
                                                showSwitch = true,
                                                switchState = state.forwardToDashboard,
                                                onSwitchChange = {
                                                    onEvent(DashboardContract.Event.OnForwardToDashboard(it))
                                                }
                                            )
                                            HorizontalDivider(color = Gray4)
                                            SettingDrawer(
                                                title = "Open detail",
                                                icon = R.drawable.notes,
                                                showSwitch = true,
                                                switchState = state.openDetail,
                                                onSwitchChange = {
                                                    onEvent(DashboardContract.Event.OnOpenDetail(it))
                                                }
                                            )
                                            HorizontalDivider(color = Gray4)
                                            SettingDrawer(
                                                title = "Enable Add Extra In Cycle Count",
                                                icon = R.drawable.add_square,
                                                showSwitch = true,
                                                switchState = state.addExtraCycle,
                                                onSwitchChange = {
                                                    onEvent(DashboardContract.Event.OnAddExtraCycleChange(it))
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Column {

                                SettingDrawer(
                                    title = "Lock keyboard",
                                    icon = R.drawable.keyboard,
                                    showSwitch = true,
                                    switchState = state.lockKeyboard,
                                    onSwitchChange = {
                                        onEvent(DashboardContract.Event.OnLockKeyboardChange(it))
                                    }
                                )
                                HorizontalDivider(color = Gray4)
                                Row(
                                    Modifier
                                        .padding(top = 5.mdp, bottom = 2.mdp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.mdp))
                                        .background(Color.White)
                                        .clickable {
                                            onEvent(DashboardContract.Event.ShowSettings(true))
                                        }
                                        .padding(12.mdp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Outlined.Settings,
                                        contentDescription = "",
                                        modifier = Modifier.size(24.mdp),
                                        tint = Black
                                    )
                                    Spacer(Modifier.size(20.mdp))
                                    MyText(
                                        "Settings",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.W500,
                                        color = Black
                                    )
                                }
                                Row(
                                    Modifier
                                        .padding(top = 5.mdp, bottom = 2.mdp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.mdp))
                                        .background(Color.White)
                                        .border(1.mdp, ErrorRed, RoundedCornerShape(6.mdp))
                                        .clickable {
                                            onEvent(DashboardContract.Event.OnLogout)
                                        }
                                        .padding(12.mdp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painterResource(R.drawable.logout_01),
                                        contentDescription = "",
                                        modifier = Modifier.size(24.mdp),
                                        tint = ErrorRed
                                    )
                                    Spacer(Modifier.size(20.mdp))
                                    MyText(
                                        "Logout",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.W500,
                                        color = ErrorRed
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ){
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(1.mdp, RoundedCornerShape(6.mdp))
                            .clip(RoundedCornerShape(6.mdp))
                            .background(Color.White)
                            .clickable {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                            .padding(12.mdp)
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "",
                            modifier = Modifier.size(24.mdp),
                            tint = Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .shadow(1.mdp, RoundedCornerShape(6.mdp))
                            .clip(RoundedCornerShape(6.mdp))
                            .background(Color.White)
                            .padding(13.mdp)
                    ) {
                        MyText(
                            text = state.selectedTab.title,
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.size(10.mdp))
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.size(10.mdp))
                    if (state.selectedTab == DashboardTab.Picking){
                        state.dashboards.forEach {entry ->
                            Column {
                                val labelVisible = when(entry.key){
                                    "Count"->{
                                        true
                                    }
                                    "Shipping"->{
                                        state.dashboardsVisibility.filter{it.key.category == "Count"}.all { it.value }
                                    }
                                    "Stock"->{
                                        state.dashboardsVisibility.filter{it.key.category == "Shipping"}.all { it.value }

                                    }
                                    "Integration"-> {
                                        state.dashboardsVisibility.filter{it.key.category == "Stock"}.all { it.value }

                                    }
                                    else -> {
                                        true
                                    }
                                }
//                                val labelVisible = !state.dashboardsVisibility.any { it.key.category == entry.key && !it.value }
                                DashboardListItem(entry,labelVisible,state.dashboardsVisibility,state.dashboard) {
                                    if(it.destination!=null)onEvent(DashboardContract.Event.OnNavigate(it.destination))
                                }
                            }
                            Spacer(modifier = Modifier.size(15.mdp))
                        }
                    } else {
                        state.crossDockDashboards.forEach { entry->

                            Column {
                                DashboardListItem(entry,true,state.crossDockDashboardsVisibility,null){
                                    when(it){
                                        MainItems.Receiving-> {
                                            onEvent(DashboardContract.Event.OnNavigate(
                                                CountingScreenDestination(isCrossDock = true)))
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        }
//                        CrossDashboard {  }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingDrawer(
    modifier: Modifier = Modifier,
    title: String,
    icon: Int,
    switchState: Boolean = false,
    showSwitch: Boolean = true,
    onClick: (() -> Unit)? = null,
    onSwitchChange: (Boolean)->Unit = {}
) {
    Row(
        modifier
            .padding(top = 5.mdp, bottom = 2.mdp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .padding(12.mdp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(icon),
                contentDescription = "",
                modifier = Modifier.size(24.mdp),
                tint = Black
            )
            Spacer(Modifier.size(20.mdp))
            MyText(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W500,
                color = Black
            )
        }
        Spacer(Modifier.size(5.mdp))
        if (showSwitch) MySwitch(switchState) {
            onSwitchChange(it)
        } else {
            Icon(
                painterResource(R.drawable.direction_right_2),
                contentDescription = "",
                modifier = Modifier.size(24.mdp),
                tint = Black
            )
        }
    }
}

@Composable
fun DrawerItem(
    title: String,
    icon: Int,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier
            .padding(top = 5.mdp, bottom = 2.mdp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(if (selected) Primary.copy(0.2f) else Color.White)
            .clickable(
                indication = ripple(color = Primary),
                interactionSource = interactionSource,
                onClick = onClick
            )
            .padding(12.mdp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painterResource(icon),
            contentDescription = "",
            modifier = Modifier.size(24.mdp),
            tint = if (selected) Primary else Black
        )
        Spacer(Modifier.size(20.mdp))
        MyText(
            title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.W500,
            color = if (selected) Primary else Black
        )
    }
    HorizontalDivider(
        color = if (!selected) Gray4 else Color.White
    )
}

@Composable
fun DashboardListItem(
    item: Map.Entry<String, List<MainItems>>,
    labelVisibility: Boolean = false,
    visibility: Map<MainItems, Boolean>,
    dashboardModel: DashboardModel?,
    onItemClick: (MainItems)-> Unit
) {
    AnimatedVisibility(labelVisibility) {
        MyText(
            item.key,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W500,
            fontSize = 16.sp,
            color = Black
        )
    }
    Spacer(Modifier.size(5.mdp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        for (i in 0..3){
            val it = item.value.getOrNull(i)
            if (it != null){

                DashboardSubItem(
                    item = it,
                    count = dashboardModel?.getCount(it) ,
                    isVisible = visibility.getOrDefault(it,false),
                    onClick = {
                        onItemClick(it)
                    }
                )
            } else {
                Spacer(Modifier.size(80.mdp))
            }
        }
    }
    if (item.value.size > 4)Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        for (i in 4..7){
            val it = item.value.getOrNull(i)
            if (it != null){
                DashboardSubItem(
                    item = it,
                    count = dashboardModel?.getCount(it),
                    isVisible = visibility.getOrDefault(it,false),
                    onClick = {
                        onItemClick(it)
                    }
                )
            } else {
                Spacer(Modifier.size(80.mdp))
            }
        }
    }
}

@Composable
fun DashboardSubItem(
    item: MainItems,
    count: Int? = null,
    isVisible: Boolean = false,
    onClick: () -> Unit
) {

    AnimatedVisibility (
        isVisible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box {

                Box(
                    Modifier
                        .padding(10.mdp)
                        .clip(RoundedCornerShape(6.mdp))
                        .background(item.color)
                        .clickable {
                            onClick()
                        }
                        .padding(14.mdp)
                ) {
                    Icon(
                        painterResource(item.icon),
                        contentDescription = "",
                        modifier = Modifier.size(32.mdp),
                        tint = Color.White
                    )
                }
                if (count!=null && count > 0) {
                    Box(
                        Modifier
                            .padding(3.mdp)
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(4.mdp))
                            .background(Red)
                            .padding(vertical = 2.mdp, horizontal = 8.mdp)
                    ) {
                        MyText(
                            count.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.W500,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
//                if (showDot){
//                    Box(
//                        Modifier
//                            .padding(2.mdp)
//                            .align(Alignment.TopEnd)
//                            .clip(CircleShape)
//                            .border(1.mdp, Red.copy(0.45f), CircleShape)
//                            .padding(3.mdp)
//                    ){
//                        Box(
//                            Modifier
//                                .size(14.mdp)
//                                .clip(CircleShape)
//                                .background(Red)
//                        )
//                    }
//                }
            }
            Spacer(Modifier.size(5.mdp))
            MyText(
                item.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
                color = Black
            )
        }
    }
    if (!isVisible) Spacer(Modifier.size(height = 100.mdp,width = 78.mdp))
}

@Preview
@Composable
private fun DashboardPreview() {
    DashboardContent()
}
package com.example.jaywarehouse.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.jaywarehouse.MainActivity
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.restartActivity
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.utils.MainGraph
import com.example.jaywarehouse.presentation.common.utils.MainItems
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.destinations.CountingScreenDestination
import com.example.jaywarehouse.presentation.destinations.PackingScreenDestination
import com.example.jaywarehouse.presentation.destinations.PickingCustomerScreenDestination
import com.example.jaywarehouse.presentation.destinations.PutawayScreenDestination
import com.example.jaywarehouse.presentation.destinations.ShippingScreenDestination
import com.example.jaywarehouse.presentation.destinations.TransferPickScreenDestination
import com.example.jaywarehouse.presentation.destinations.TransferPutScreenDestination
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray1
import com.example.jaywarehouse.ui.theme.Orange
import com.example.jaywarehouse.ui.theme.Primary
import com.example.jaywarehouse.ui.theme.Red
import com.example.jaywarehouse.ui.theme.poppins
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
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
    MyScaffold {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(drawerShape = RectangleShape, drawerContainerColor = Color.White) {
                    Column(Modifier.fillMaxWidth(0.8f).padding(12.mdp)) {
                        Row(
                            Modifier.fillMaxWidth()
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
                            Modifier.fillMaxWidth()
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
                        Spacer(modifier = Modifier.size(15.mdp
                        ))
                        Column(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.mdp))
                                    .background(Primary.copy(0.2f))
                                    .clickable {  }
                                    .padding(12.mdp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painterResource(R.drawable.home_02),
                                    contentDescription = "",
                                    modifier = Modifier.size(24.mdp),
                                    tint = Primary
                                )
                                Spacer(Modifier.size(20.mdp))
                                MyText(
                                    "Home",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.W500,
                                    color = Primary
                                )
                            }
                            HorizontalDivider()
                            state.dashboards.forEach {
                                Row(Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.mdp))
                                    .clickable {  }
                                    .padding(12.mdp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painterResource(R.drawable.truck_next),
                                        contentDescription = "",
                                        modifier = Modifier.size(24.mdp),
                                        tint = Black
                                    )
                                    Spacer(Modifier.size(20.mdp))
                                    MyText(
                                        it.key,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.W500
                                    )
                                }
                                HorizontalDivider()
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
                        modifier =Modifier
                            .shadow(1.mdp,RoundedCornerShape(6.mdp))
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
                        modifier =Modifier
                            .shadow(1.mdp,RoundedCornerShape(6.mdp))
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

                Spacer(Modifier.size(20.mdp))
                state.dashboards.forEach { entry ->
                    DashboardListItem(entry) {
                        if(it.destination!=null)onEvent(DashboardContract.Event.OnNavigate(it.destination))
                    }
                    Spacer(modifier = Modifier.size(15.mdp))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    icon: Int,
    statistic: String,
    backgroundColor: Color,
    contentColor: Color
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.mdp))
            .background(backgroundColor)
            .padding(vertical = 7.mdp, horizontal = 5.mdp)

    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                MyText(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor
                )
                MyText(
                    text = text,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(0.7f)
                )
            }
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "",
                tint = contentColor
            )
        }
        Spacer(modifier = Modifier.size(25.mdp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.mdp))
                .background(
                    Color.LightGray.copy(0.4f)
                )
                .padding(vertical = 3.mdp, horizontal = 7.mdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MyText(
                text = statistic,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(0.7f),
                fontWeight = FontWeight.Medium
            )
            Icon(painter = painterResource(
                id = R.drawable.vuesax_bulk_box),
                contentDescription = "",
                tint = Orange
            )
        }
    }

}

@Composable
fun IconCard(
    icon: Int,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 10.mdp, horizontal = 10.mdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painterResource(id = icon),
            contentDescription = "",
            modifier = Modifier
                .size(24.mdp),
            tint = Black
        )
        Spacer(modifier = Modifier.size(7.mdp))
        MyText(
            text = title,
            fontFamily = poppins,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun AccessCard(
    modifier: Modifier = Modifier,
    onClick: ()->Unit = {},
    title: String,
    description: String,
    icon: Int
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.mdp))
            .background(Color.White)
            .clickable {
                onClick()
            }
            .padding(10.mdp)
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(10.mdp))
                .background(Gray1)
                .padding(7.mdp)
        ) {
            Icon(
                painterResource(id = icon),
                contentDescription = "",
                modifier = Modifier
                    .size(40.mdp),
                tint = Black
            )
        }
        Spacer(modifier = Modifier.size(7.mdp))
        MyText(
            text = title,
            fontFamily = poppins,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(5.mdp))
        MyText(
            text = description,
            fontFamily = poppins,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            minLines = 2,
            color = MaterialTheme.colorScheme.outline
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardListItem(
    item: Map.Entry<String, List<MainItems>>,
    onItemClick: (MainItems)-> Unit
) {
    MyText(
        item.key,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        color = Black
    )
    Spacer(Modifier.size(5.mdp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        for (i in 0..3){
            val it = item.value.getOrNull(i)
            if (it != null){
                DashboardSubItem(
                    item = it,
                    showCount = it == MainItems.StockTaking,
                    showDot = it == MainItems.Transfer,
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
                    showCount = it == MainItems.StockTaking,
                    showDot = it == MainItems.Transfer,
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
    showCount: Boolean = false,
    showDot: Boolean = false,
    onClick: () -> Unit
) {
    Column(
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
                    if (item.icon!=null){
                        Icon(
                            painterResource(item.icon),
                            contentDescription = "",
                            modifier = Modifier.size(32.mdp),
                            tint = Color.White
                        )
                    } else {
                        Spacer(Modifier.size(32.mdp))
                    }
                }
                if (showCount) {
                    Box(
                        Modifier
                            .padding(3.mdp)
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(4.mdp))
                            .background(Red)
                            .padding(vertical = 2.mdp, horizontal = 8.mdp)
                    ) {
                        MyText(
                            "1",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.W500,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
                if (showDot){
                    Box(
                        Modifier
                            .padding(2.mdp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .border(1.mdp, Red.copy(0.8f), CircleShape)
                            .padding(3.mdp)
                    ){
                        Box(
                            Modifier
                                .size(14.mdp)
                                .clip(CircleShape)
                                .background(Red)
                        )
                    }
                }
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
}

@Preview
@Composable
private fun DashboardPreview() {
    DashboardContent()
}
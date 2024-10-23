package com.example.jaywarehouse.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.utils.DashboardItem
import com.example.jaywarehouse.presentation.common.utils.MainGraph
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
import com.example.jaywarehouse.ui.theme.poppins
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.selects.whileSelect
import org.koin.androidx.compose.koinViewModel

@MainGraph(start = true)
@Destination(style = ScreenTransition::class)
@Composable
fun DashboardScreen(
    navigator: DestinationsNavigator,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                is DashboardContract.Effect.Navigate -> navigator.navigate(it.destination)
            }
        }
        
    }

    DashboardContent(onEvent)
}



@Composable
private fun DashboardContent(
    onEvent: (DashboardContract.Event)->Unit = {}
) {
    MyScaffold(offset = (-70).mdp) {

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(15.mdp)) {
            Row(Modifier.fillMaxWidth()) {
                StatCard(
                    title = "Available",
                    text = "item 150",
                    icon = R.drawable.vuesax_linear_box,
                    statistic = "45 x 45",
                    backgroundColor = Color.White,
                    modifier = Modifier.weight(1f),
                    contentColor = Black
                )
                Spacer(modifier = Modifier.size(7.mdp))
                StatCard(
                    title = "Next Truck",
                    text = "10 July",
                    icon = R.drawable.truck_next,
                    statistic = "36 Trailers",
                    backgroundColor = Black,
                    modifier = Modifier.weight(1f),
                    contentColor = Color.White
                )
                Spacer(modifier = Modifier.size(7.mdp))
                StatCard(
                    title = "Scanned",
                    text = "item 3",
                    icon = R.drawable.barcode,
                    statistic = "13 Containers",
                    backgroundColor = Orange,
                    modifier = Modifier.weight(1f),
                    contentColor = Black.copy(0.8f)
                )
            }
            Spacer(modifier = Modifier.size(30.mdp))
            Row(Modifier.fillMaxWidth()) {
                IconCard(
                    icon = R.drawable.vuesax_bulk_convert_3d_cube,
                    title = "Transfer Pick",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onEvent(DashboardContract.Event.OnNavigate(TransferPickScreenDestination))
                    }
                )
                Spacer(modifier = Modifier.size(7.mdp))
                IconCard(
                    icon = R.drawable.vuesax_bulk_note,
                    title = "Transfer Put",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onEvent(DashboardContract.Event.OnNavigate(TransferPutScreenDestination))
                    }
                )
            }
            Spacer(modifier = Modifier.size(30.mdp))
            MyText(
                text = "Quick Access",
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.titleLarge,
                color = Black,
                fontFamily = poppins
            )
            Spacer(modifier = Modifier.size(10.mdp))
            Row(Modifier.fillMaxWidth()) {
                AccessCard(
                    title = stringResource(id = R.string.counting),
                    description = stringResource(id = R.string.counting_notice),
                    icon = R.drawable.vuesax_bulk_box_search,
                    onClick = {
                        onEvent(DashboardContract.Event.OnNavigate(CountingScreenDestination))
                    },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(10.mdp))
                AccessCard(
                    title = stringResource(id = R.string.putaway),
                    description = stringResource(id = R.string.putaway_notice),
                    icon = R.drawable.put_icon,
                    onClick = {
                        onEvent(DashboardContract.Event.OnNavigate(PutawayScreenDestination))
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.size(10.mdp))
            Row(Modifier.fillMaxWidth()) {
                AccessCard(
                    title = stringResource(id =R.string.picking ),
                    description = stringResource(id = R.string.picking_notice),
                    icon = R.drawable.picking_icon,
                    onClick = {
                        onEvent(DashboardContract.Event.OnNavigate(PickingCustomerScreenDestination))
                    },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(10.mdp))
                AccessCard(
                    title = stringResource(id = R.string.packing),
                    description = stringResource(id = R.string.packing_notice),
                    icon = R.drawable.vuesax_bulk_dropbox,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onEvent(DashboardContract.Event.OnNavigate(PackingScreenDestination))
                    }
                )
            }
            Spacer(modifier = Modifier.size(10.mdp))
            AccessCard(
                title = stringResource(id = R.string.shipping),
                description = stringResource(id = R.string.shipping_notice),
                icon = R.drawable.vuesax_bulk_group,
                onClick = {
                    onEvent(DashboardContract.Event.OnNavigate(ShippingScreenDestination))
                }
            )
            Spacer(modifier = Modifier.size(75.mdp))
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
            .clip(RoundedCornerShape(10.mdp))
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
                .clip(RoundedCornerShape(5.mdp))
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
            .clip(RoundedCornerShape(10.mdp))
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
                .size(35.mdp),
            tint = Black
        )
        Spacer(modifier = Modifier.size(5.mdp))
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


@Preview
@Composable
private fun DashboardPreview() {
    DashboardContent()
}
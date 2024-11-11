package com.example.jaywarehouse.presentation.common.utils

import androidx.compose.ui.graphics.Color
import com.example.jaywarehouse.R
import com.example.jaywarehouse.presentation.destinations.CheckingScreenDestination
import com.example.jaywarehouse.presentation.destinations.CountingScreenDestination
import com.example.jaywarehouse.presentation.destinations.ManualPutawayScreenDestination
import com.example.jaywarehouse.presentation.destinations.PickingScreenDestination
import com.example.jaywarehouse.presentation.destinations.PutawayScreenDestination
import com.example.jaywarehouse.ui.theme.Blue
import com.example.jaywarehouse.ui.theme.Primary
import com.example.jaywarehouse.ui.theme.SkyBlue
import com.example.jaywarehouse.ui.theme.Yellow
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class MainItems(
    val title: String,
    val icon: Int,
    val category: String,
    val color: Color,
    val destination: DirectionDestinationSpec? = null
) {
    Receiving(
        "Receiving",
        R.drawable.delivery,
        "Count",
        Primary,
        CountingScreenDestination
    ),
    Putaway(
        "Putaway",
        R.drawable.putaway,
        "Count",
        Primary,
        PutawayScreenDestination
    ),
    ManualPutaway(
        "Manual\nPutaway",
        R.drawable.manual_putaway,
        "Count",
        Primary,
        ManualPutawayScreenDestination
    ),
    ReturnReceiving(
        "Return\nReceiving",
        R.drawable.delivery_return,
        "Count",
        Primary
    ),
    Picking(
        "Picking",
        R.drawable.trolley,
        "Shipping",
        Blue,
        PickingScreenDestination
    ),
    Checking(
        "Checking",
        R.drawable.trolley_check,
        "Shipping",
        Blue,
        CheckingScreenDestination
    ),
    PalletConfirm(
        "Pallet\nConfirm",
        R.drawable.pallet,
        "Shipping",
        Blue
    ),
    Loading(
        "Loading",
        R.drawable.cargo,
        "Shipping",
        Blue
    ),
    ShippingTruck(
        "Shipping\nTruck",
        R.drawable.fast_delivery,
        "Shipping",
        Blue
    ),
    Inventory(
        "Inventory",
        R.drawable.warehouse,
        "Stock",
        Yellow
    ),
    Transfer(
        "Transfer",
        R.drawable.parcel_stock,
        "Stock",
        Yellow
    ),
    StockTaking(
        "Stock\nTaking",
        R.drawable.task,
        "Stock",
        Yellow
    ),
    RS(
        "RS",
        R.drawable.rs,
        "Integration",
        SkyBlue
    )
}
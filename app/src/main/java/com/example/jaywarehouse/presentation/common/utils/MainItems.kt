package com.example.jaywarehouse.presentation.common.utils

import androidx.compose.ui.graphics.Color
import com.example.jaywarehouse.R
import com.example.jaywarehouse.presentation.destinations.CheckingScreenDestination
import com.example.jaywarehouse.presentation.destinations.CountingScreenDestination
import com.example.jaywarehouse.presentation.destinations.CycleScreenDestination
import com.example.jaywarehouse.presentation.destinations.LoadingScreenDestination
import com.example.jaywarehouse.presentation.destinations.ManualPutawayScreenDestination
import com.example.jaywarehouse.presentation.destinations.PalletScreenDestination
import com.example.jaywarehouse.presentation.destinations.PickingScreenDestination
import com.example.jaywarehouse.presentation.destinations.PurchaseOrderScreenDestination
import com.example.jaywarehouse.presentation.destinations.PutawayScreenDestination
import com.example.jaywarehouse.presentation.destinations.RSScreenDestination
import com.example.jaywarehouse.presentation.destinations.ShippingScreenDestination
import com.example.jaywarehouse.presentation.destinations.TransferScreenDestination
import com.example.jaywarehouse.presentation.destinations.TypedDestination
import com.example.jaywarehouse.ui.theme.Blue
import com.example.jaywarehouse.ui.theme.Primary
import com.example.jaywarehouse.ui.theme.SkyBlue
import com.example.jaywarehouse.ui.theme.Yellow
import com.ramcosta.composedestinations.spec.Direction

enum class MainItems(
    val title: String,
    val icon: Int,
    val category: String,
    val color: Color,
    val destination: Direction? = null
) {
    Receiving(
        "Count",
        R.drawable.delivery,
        "Count",
        Primary,
        CountingScreenDestination(isCrossDock = false)
    ),
//    Putaway(
//        "Putaway",
//        R.drawable.putaway,
//        "Count",
//        Primary,
//        PutawayScreenDestination
//    ),
    ManualPutaway(
        "Putaway",
        R.drawable.manual_putaway,
        "Count",
        Primary,
        PutawayScreenDestination
    ),
    ReturnReceiving(
        "Return",
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
    PickingBD(
        "Picking",
        R.drawable.inventory_management,
        "Shipping",
        Blue,
        PurchaseOrderScreenDestination
    ),
    Checking(
        "Checking",
        R.drawable.trolley_check,
        "Shipping",
        Blue,
        CheckingScreenDestination
    ),
    PalletConfirm(
        "Completion",
        R.drawable.pallet,
        "Shipping",
        Blue,
        PalletScreenDestination
    ),
    Loading(
        "Loading",
        R.drawable.cargo,
        "Shipping",
        Blue,
        LoadingScreenDestination
    ),
    ShippingTruck(
        "Shipping",
        R.drawable.fast_delivery,
        "Shipping",
        Blue,
        ShippingScreenDestination
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
        Yellow,
        TransferScreenDestination
    ),
    CycleCount(
        "Cycle Count",
        R.drawable.task,
        "Stock",
        Yellow,
        CycleScreenDestination
    ),
    RS(
        "RS",
        R.drawable.rs,
        "Integration",
        SkyBlue,
        RSScreenDestination
    )
}
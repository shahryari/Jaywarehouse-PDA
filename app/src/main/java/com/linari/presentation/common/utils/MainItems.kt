package com.linari.presentation.common.utils

import androidx.compose.ui.graphics.Color
import com.linari.R
import com.linari.presentation.destinations.CheckingScreenDestination
import com.linari.presentation.destinations.CountingScreenDestination
import com.linari.presentation.destinations.CycleScreenDestination
import com.linari.presentation.destinations.LoadingScreenDestination
import com.linari.presentation.destinations.ManualPutawayScreenDestination
import com.linari.presentation.destinations.PalletScreenDestination
import com.linari.presentation.destinations.PickingScreenDestination
import com.linari.presentation.destinations.PurchaseOrderScreenDestination
import com.linari.presentation.destinations.PutawayScreenDestination
import com.linari.presentation.destinations.RSScreenDestination
import com.linari.presentation.destinations.ReturnScreenDestination
import com.linari.presentation.destinations.ShippingScreenDestination
import com.linari.presentation.destinations.TransferScreenDestination
import com.linari.presentation.destinations.TypedDestination
import com.linari.presentation.destinations.WaybillScreenDestination
import com.linari.ui.theme.Blue
import com.linari.ui.theme.Primary
import com.linari.ui.theme.SkyBlue
import com.linari.ui.theme.Yellow
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
        Primary,
        ReturnScreenDestination
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
        Yellow,
        TransferScreenDestination,
    ),
//    Transfer(
//        "Transfer",
//        R.drawable.parcel_stock,
//        "Stock",
//        Yellow,
//        TransferScreenDestination
//    ),
    CycleCount(
        "Cycle Count",
        R.drawable.task,
        "Stock",
        Yellow,
        CycleScreenDestination
    ),
//    RS(
//        "RS",
//        R.drawable.rs,
//        "Integration",
//        SkyBlue,
//        RSScreenDestination
//    ),
    Waybill(
        "Waybill",
        R.drawable.rs,
        "Integration",
        SkyBlue,
        WaybillScreenDestination
    )
}
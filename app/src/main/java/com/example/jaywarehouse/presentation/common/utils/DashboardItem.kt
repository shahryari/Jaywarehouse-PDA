package com.example.jaywarehouse.presentation.common.utils

import com.example.jaywarehouse.R
import com.example.jaywarehouse.presentation.destinations.CountingDetailScreenDestination
import com.example.jaywarehouse.presentation.destinations.CountingScreenDestination
import com.example.jaywarehouse.presentation.destinations.DashboardScreenDestination
import com.example.jaywarehouse.presentation.destinations.PackingDetailScreenDestination
import com.example.jaywarehouse.presentation.destinations.PackingScreenDestination
import com.example.jaywarehouse.presentation.destinations.PutawayDetailScreenDestination
import com.example.jaywarehouse.presentation.destinations.PutawayScreenDestination
import com.example.jaywarehouse.presentation.destinations.ShippingDetailScreenDestination
import com.example.jaywarehouse.presentation.destinations.ShippingScreenDestination
import com.example.jaywarehouse.presentation.destinations.TypedDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class DashboardItem(
    val title: Int,
    val icon: Int,
    val destination: DirectionDestinationSpec? = null,
    val subDestinations: List<TypedDestination<*>> = emptyList()
) {
    Dashboard(
        R.string.dashboard,
        R.drawable.vuesax_bold_home_2,
        DashboardScreenDestination,
        listOf(DashboardScreenDestination)
    ),
    Counting(
        R.string.counting,
        R.drawable.vuesax_bulk_box_search,
        CountingScreenDestination,
        listOf(
            CountingScreenDestination,
            CountingDetailScreenDestination
        )
    ),
    Putaway(
        R.string.putaway,
        R.drawable.put_icon,
        PutawayScreenDestination,
        listOf(PutawayScreenDestination,PutawayDetailScreenDestination)
    ),
    Packing(
        R.string.packing,
        R.drawable.vuesax_bulk_dropbox,
        PackingScreenDestination,
        listOf(
            PackingScreenDestination,
            PackingDetailScreenDestination
        )
    ),
    Shipping(
        R.string.shipping,
        R.drawable.vuesax_bulk_group,
        ShippingScreenDestination,
        listOf(
            ShippingScreenDestination,
            ShippingDetailScreenDestination
        )
    )
}
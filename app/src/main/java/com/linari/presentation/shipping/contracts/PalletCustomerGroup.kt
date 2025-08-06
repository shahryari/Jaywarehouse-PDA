package com.linari.presentation.shipping.contracts

import com.linari.data.picking.models.PalletManifest
import com.linari.presentation.common.composables.Animatable

data class PalletCustomerGroup(
    val customer: String,
    val items: List<PalletManifest>
) : Animatable {
    override fun key(): String {
        return customer
    }
}
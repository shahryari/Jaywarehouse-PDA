package com.linari.presentation.common.utils

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.material3.RippleDefaults
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.unit.dp
import com.linari.data.common.utils.mdp
import com.linari.ui.theme.Primary

object ColoredIndication : IndicationNodeFactory{
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return createRippleModifierNode(bounded = true, radius = 6.dp, color = {
            Primary.copy(0.2f)
        }, rippleAlpha = { RippleDefaults.RippleAlpha}, interactionSource = interactionSource)
    }

    override fun equals(other: Any?): Boolean {
        return other === ColoredIndication
    }

    override fun hashCode(): Int {
        return 102
    }
}
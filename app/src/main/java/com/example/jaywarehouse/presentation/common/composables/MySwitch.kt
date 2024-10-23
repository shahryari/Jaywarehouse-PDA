package com.example.jaywarehouse.presentation.common.composables


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.jaywarehouse.data.common.utils.mdp

@Composable
fun MySwitch(
    active: Boolean,
    enabled: Boolean = true,
    onStateChange: (Boolean) -> Unit
) {
    Box(
        contentAlignment = Alignment.CenterStart
    ) {

        val color by animateColorAsState(
            targetValue = if (active) MaterialTheme.colorScheme.primary else Color.Gray,
            label = ""
        )
        Spacer(
            modifier = Modifier
                .width(50.mdp)
                .height(25.mdp)
                .clip(RoundedCornerShape(15.mdp))
                .background(color.copy(0.3f))
        )

        val x by animateDpAsState(
            targetValue = if (active) 25.mdp else 0.mdp,
            label = ""
        )



//        val endValue = 20.mdp.mdpToPx()
//        val anchors = DraggableAnchors {
//            0f at 0f
//            1f at endValue
//        }
//        val xfloat = x.mdpToPx()
//        val state = remember {
//            AnchoredDraggableState(
//                initialValue = xfloat,
//                anchors = anchors,
//                positionalThreshold = {
//                    it*0.5f
//                },
//                velocityThreshold = {0f},
//                animationSpec = tween(150),
//                confirmValueChange = {true}
//            )
//        }
//
//        LaunchedEffect(key1 = state.requireOffset()) {
//            isActive = state.requireOffset() == endValue
//        }

        Spacer(
            modifier = Modifier
                .padding(start = 3.mdp)
                .size(19.mdp)
//                .anchoredDraggable(
//                    state = state,
//                    orientation = Orientation.Horizontal,
//                    reverseDirection = true
//                )
                .offset(x)
//                .offset {
//                    IntOffset(
//                        state
//                            .requireOffset()
//                            .toInt(), 0
//                    )
//                }
                .clip(CircleShape)
                .background(color)
                .clickable {
                    if (enabled){
                        onStateChange(!active)
                    }
                }
        )
    }
}
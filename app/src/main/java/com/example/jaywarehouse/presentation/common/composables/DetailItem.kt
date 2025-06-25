package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.ui.theme.ErrorRed

@Composable
fun DetailItem(
    i: Int,
    first: String,
    firstIcon: Int = R.drawable.barcode,
    second: String,
    secondIcon: Int = R.drawable.box_search,
    selected: Boolean = false,
    removable: Boolean = true,
    onRemove: ()->Unit = {},
    onClick: ()->Unit = {}
) {

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it){
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove()
                    false
                }
                SwipeToDismissBoxValue.Settled -> {
                    removable
                }
            }
        },
        positionalThreshold = {
            it*0.25f
        }
    )
    if (!selected && dismissState.currentValue != SwipeToDismissBoxValue.Settled)LaunchedEffect(Unit) {
        dismissState.reset()
    }
    @Composable
    fun ItemBox( content: @Composable ()-> Unit){
        if (removable) {
            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.mdp))
                            .background(ErrorRed)
                            .padding(10.mdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if(dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Spacer(Modifier.size(5.mdp))
                        AnimatedVisibility(dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "",
                                modifier = Modifier.size(20.mdp),
                                tint = Color.White
                            )
                        }
                        AnimatedVisibility(dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "",
                                modifier = Modifier.size(20.mdp),
                                tint = Color.White
                            )
                        }
                    }
                },
                enableDismissFromStartToEnd = removable,
                enableDismissFromEndToStart = removable
            ) {
                content()
            }
        }
        else {
            Box {
                content()
            }
        }
    }
    ItemBox {
        Column(
            modifier = Modifier
                .shadow(1.mdp, RoundedCornerShape(6.mdp))
                .fillMaxWidth()
                .clickable(!removable){
                    onClick()
                }
                .clip(RoundedCornerShape(6.mdp))
                .background(Color.White)
                .padding(10.mdp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MyText(
                    text = i.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W500,
                    color = Color.Black
                )
                Spacer(Modifier.size(15.mdp))

                Row(modifier = Modifier.weight(0.5f),verticalAlignment = Alignment.CenterVertically) {
                    if (first.isNotEmpty())Icon(
                        painterResource(firstIcon),
                        contentDescription = "",
                        modifier = Modifier.size(20.mdp),
                        tint = Color.Black
                    )
                    Spacer(Modifier.size(10.mdp))
                    MyText(
                        text = first,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W500,
                        color = Color.Black
                    )
                }
                Spacer(Modifier.size(5.mdp))
                Row(modifier = Modifier.weight(1f),verticalAlignment = Alignment.CenterVertically) {
                    if (second.isNotEmpty())Icon(
                        painterResource(secondIcon),
                        contentDescription = "",
                        modifier = Modifier.size(20.mdp),
                        tint = Color.Black
                    )
                    Spacer(Modifier.size(10.mdp))
                    MyText(
                        text = second,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W500,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    i: Int,
    first: String,
    second: String,
    third: String,
    forth: String,
    removable: Boolean = true,
    selected: Boolean = false,
    onRemove: ()->Unit = {}
) {

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it){
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove()
                }
                SwipeToDismissBoxValue.Settled -> {}
            }
            true
        },
        positionalThreshold = {
            it*0.25f
        }
    )
    if (!selected && dismissState.currentValue != SwipeToDismissBoxValue.Settled)LaunchedEffect(Unit) {
        dismissState.reset()
    }
    @Composable
    fun ItemBox( content: @Composable ()-> Unit){
        if (removable) {
            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.mdp))
                            .background(ErrorRed)
                            .padding(10.mdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if(dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Spacer(Modifier.size(5.mdp))
                        AnimatedVisibility(dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "",
                                modifier = Modifier.size(20.mdp),
                                tint = Color.White
                            )
                        }
                        AnimatedVisibility(dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "",
                                modifier = Modifier.size(20.mdp),
                                tint = Color.White
                            )
                        }
                    }
                },
                enableDismissFromStartToEnd = removable,
                enableDismissFromEndToStart = removable
            ) {
                content()
            }
        }
        else {
            Box {
                content()
            }
        }
    }
    ItemBox {
        Column(
            modifier = Modifier
                .shadow(1.mdp, RoundedCornerShape(6.mdp))
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.mdp))
                .background(Color.White)
                .padding(10.mdp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MyText(
                    text = i.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W500,
                    color = Color.Black
                )
                Spacer(Modifier.size(10.mdp))

                Row(modifier = Modifier.weight(1f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    MyText(
                        text = first,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W500,
                        color = Color.Black
                    )
                }
                Spacer(Modifier.size(5.mdp))
                Row(modifier = Modifier.weight(0.5f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    MyText(
                        text = second,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W500,
                        color = Color.Black
                    )
                }
                Spacer(Modifier.size(5.mdp))
                Row(modifier = Modifier.weight(1f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    MyText(
                        text = third,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W500,
                        color = Color.Black
                    )
                }
                Spacer(Modifier.size(5.mdp))
                Row(modifier = Modifier.weight(1.2f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    MyText(
                        text = forth,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W500,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DetailItemPreview() {
    var cancel by remember { mutableStateOf(false) }
    Column {
//        DetailItem(1,"test","test","test",selected = cancel)
        Button(onClick = {cancel = !cancel} ) { MyText("reset") }
    }
}
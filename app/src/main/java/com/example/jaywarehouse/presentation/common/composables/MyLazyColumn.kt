package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.jaywarehouse.data.common.utils.mdp

interface Animatable{
    fun key(): String
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : Animatable>MyLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    itemContent: @Composable (Int,T) -> Unit,
    state: LazyListState = rememberLazyListState(),
    header: @Composable ()->Unit = {},
    onReachEnd: ()->Unit,
    spacerSize: Dp = 10.mdp
) {

    LazyColumn(modifier,state = state) {
        stickyHeader { header() }
        itemsIndexed(items, key = {_,it->it.key()}){i,it->
            Column(Modifier.animateItem(
                fadeInSpec = tween(delayMillis = i*50)
            )) {
                itemContent(i,it)
                Spacer(modifier = Modifier.size(spacerSize))
            }
        }
        item {
            Spacer(Modifier.size(60.mdp))
        }
        item {
            onReachEnd()
        }
    }
}
package com.example.jaywarehouse.presentation.common.composables

import androidx.collection.mutableIntSetOf
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.presentation.counting.CountListItem
import com.example.jaywarehouse.presentation.counting.contracts.CountingContract
import kotlinx.coroutines.delay


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T>MyLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    itemContent: @Composable (Int,T) -> Unit,
    header: @Composable ()->Unit = {},
    onReachEnd: ()->Unit,
    spacerSize: Dp = 10.mdp
) {

    LazyColumn(modifier) {
        stickyHeader { header() }
        itemsIndexed(items){i,it->
            var isVisible by remember {
                mutableStateOf(false)
            }

            LaunchedEffect(Unit) {
//                delay(i*50L)
                isVisible = true
            }
            Column(Modifier.animateItem(
                fadeInSpec = tween(delayMillis = i*50)
            )) {
                itemContent(i,it)
                Spacer(modifier = Modifier.size(spacerSize))
            }
//            AnimatedVisibility(
//                visible = isVisible,
//                enter = slideInHorizontally(initialOffsetX = { it }),
//                exit = slideOutHorizontally(targetOffsetX = { it }),
//                modifier = Modifier.fillMaxSize()
//            ) {
//            }
        }
        item {
            onReachEnd()
        }
    }
}
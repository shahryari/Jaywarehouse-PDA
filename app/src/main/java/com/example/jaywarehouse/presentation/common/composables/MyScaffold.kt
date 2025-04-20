package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.ui.theme.Background
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScaffold(
    modifier: Modifier = Modifier,
    loadingState: Loading = Loading.NONE,
    error: String = "",
    onCloseError: ()-> Unit = {},
    toast: String = "",
    onHideToast: ()-> Unit = {},
    refreshable: Boolean = true,
    onRefresh: ()->Unit = {},
    content: @Composable ()->Unit
) {

    LaunchedEffect(key1 =toast) {
        if(toast.isNotEmpty()){
            delay(3000)
            onHideToast()
        }
    }
    Scaffold(modifier) {
        if (refreshable){
            PullToRefreshBox(
                isRefreshing = loadingState == Loading.REFRESHING,
                onRefresh = onRefresh,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(Background),
            ){
                content()
                if (loadingState == Loading.LOADING || loadingState == Loading.SEARCHING){
                    Box(modifier = Modifier.matchParentSize().clickable(false, onClick = {})){
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
                SuccessToast(message = toast, modifier = Modifier.align(Alignment.TopCenter))

            }
        } else {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(Background)
            ){
                content()
                if (loadingState == Loading.LOADING || loadingState == Loading.SEARCHING){
                    Box(modifier = Modifier.matchParentSize().clickable(false, onClick = {})){
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
                SuccessToast(message = toast, modifier = Modifier.align(Alignment.TopCenter))

            }
        }
    }
    if (error.isNotEmpty()){
        ErrorDialog(
            onDismiss = {
                onCloseError()
            },
            error
        )

    }
}

@Preview
@Composable
private fun MyScaffoldPreview() {
    MyScaffold(loadingState = Loading.LOADING){}
}
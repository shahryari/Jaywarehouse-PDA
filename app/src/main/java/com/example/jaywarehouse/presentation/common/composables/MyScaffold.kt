package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.counting.contracts.CountingDetailContract
import com.example.jaywarehouse.ui.theme.Background
import com.example.jaywarehouse.ui.theme.Gray1
import com.example.jaywarehouse.ui.theme.Gray2
import kotlinx.coroutines.delay

@Composable
fun MyScaffold(
    modifier: Modifier = Modifier,
    offset: Dp = 0.mdp,
    loadingState: Loading = Loading.NONE,
    error: String = "",
    onCloseError: ()-> Unit = {},
    toast: String = "",
    onHideToast: ()-> Unit = {},
    content: @Composable ()->Unit
) {

    LaunchedEffect(key1 =toast) {
        if(toast.isNotEmpty()){
            delay(3000)
            onHideToast()
        }
    }
    Scaffold(modifier) {
        Box(modifier = Modifier
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
    MyScaffold(loadingState = Loading.LOADING){

    }
}
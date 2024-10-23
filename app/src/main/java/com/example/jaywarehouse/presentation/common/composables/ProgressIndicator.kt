package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jaywarehouse.presentation.common.utils.Loading

@Composable
fun ProgressIndicator(
    loadingState: Loading,
    modifier: Modifier = Modifier
) {
    if (loadingState == Loading.LOADING || loadingState == Loading.SEARCHING){
        Box(modifier = modifier.fillMaxSize()){
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }
}
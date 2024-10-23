package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullToRefreshLayout(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .pullRefresh(
                onPull = {
                    it*2
                },
                onRelease = {
                    it
                }
            )
    ) {

    }
}


@Preview
@Composable
private fun PullToRefreshPreview() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)) {
        PullToRefreshLayout()
    }
}
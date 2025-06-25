package com.linari.presentation.common.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.presentation.common.utils.Loading
import com.linari.ui.theme.Primary

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ContainerBox(
    modifier: Modifier = Modifier,
    loadingState: Loading = Loading.NONE,
    showBackground: Boolean = false,
    content: @Composable BoxScope.()->Unit
) {
    Box(
        modifier
            .fillMaxSize()
    ){
        if (showBackground)BoxWithConstraints(
            Modifier.fillMaxSize()
                .background(Primary)
        ){
            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = "",
                Modifier
                    .fillMaxWidth()
//                    .scale(2.5f)
//                    .offset(x = maxWidth/5f, y = 10.mdp),
//                colorFilter = ColorFilter.tint(Color.White.copy(0.7f))
            )
        }
        content()
        if (loadingState == Loading.LOADING || loadingState == Loading.SEARCHING){

            Box(modifier = Modifier.matchParentSize()){
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }

    }
}



@Preview
@Composable
private fun ContainerBoxPreview() {
    ContainerBox(showBackground = true) {}
}
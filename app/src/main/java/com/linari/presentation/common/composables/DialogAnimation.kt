package com.linari.presentation.common.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun AnimateDialog(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        isVisible,
        modifier = modifier
    ) {
        content()
    }
}
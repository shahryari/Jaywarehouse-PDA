package com.example.jaywarehouse.presentation.common.utils

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph


@RootNavGraph
@NavGraph
annotation class MainGraph(
    val start: Boolean = false
)
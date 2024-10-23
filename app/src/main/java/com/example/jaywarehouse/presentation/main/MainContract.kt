package com.example.jaywarehouse.presentation.main

import com.example.jaywarehouse.BuildConfig
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class MainContract {
    data class State(
        val name: String = "",
        val showUpdateDialog: Boolean = false,
        val currentVersion: String = BuildConfig.VERSION_NAME,
        val newVersion: String = "",
        val updateUrl: String = ""
    ) : UiState

    sealed class Event : UiEvent {
        data object OnUpdate: Event()
        data object OnExit : Event()
    }

    sealed class Effect : UiSideEffect{
        data object RestartActivity: Effect()
        data class OpenUpdateUrl(val uri: String): Effect()
        data object Exit: Effect()
    }
}
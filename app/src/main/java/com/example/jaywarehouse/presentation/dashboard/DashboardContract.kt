package com.example.jaywarehouse.presentation.dashboard

import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState
import com.example.jaywarehouse.presentation.destinations.TypedDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

class DashboardContract {
    class State : UiState

    sealed class Event : UiEvent {
        data class OnNavigate(val destination: DirectionDestinationSpec) : Event()
    }

    sealed class Effect : UiSideEffect {
        data class Navigate(val destination: DirectionDestinationSpec) : Effect()
    }
}
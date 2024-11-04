package com.example.jaywarehouse.presentation.dashboard

import com.example.jaywarehouse.data.auth.models.DashboardModel
import com.example.jaywarehouse.presentation.common.utils.MainItems
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState
import com.example.jaywarehouse.presentation.destinations.TypedDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

class DashboardContract {
    data class State(
        val dashboards: Map<String, List<MainItems>> = MainItems.entries.groupBy { it.category },
        val name: String = "",
        val showUpdateDialog: Boolean = false,
        val newVersion: String = "",
        val updateUrl: String = "",
        val subDrawers: List<MainItems>? = null,
        val dashboard: DashboardModel? = null,
        val selectedTab: DashboardTab = DashboardTab.Picking
    ) : UiState

    sealed class Event : UiEvent {
        data class OnNavigate(val destination: DirectionDestinationSpec) : Event()
        data class OnSelectTab(val tab: DashboardTab) : Event()
        data class OnShowSubDrawers(val drawers: List<MainItems>?) : Event()
    }

    sealed class Effect : UiSideEffect {
        data class Navigate(val destination: DirectionDestinationSpec) : Effect()
        data object RestartActivity : Effect()
    }
}

enum class DashboardTab(val title: String) {
    Picking("Picking"),CrossDock("Cross Dock")
}
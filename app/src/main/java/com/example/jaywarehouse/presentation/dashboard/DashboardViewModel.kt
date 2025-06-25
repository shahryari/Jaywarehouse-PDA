package com.example.jaywarehouse.presentation.dashboard

import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.BuildConfig
import com.example.jaywarehouse.data.auth.AuthRepository
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.dashboard.DashboardContract.Effect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DashboardViewModel(
    val repository: AuthRepository,
    val prefs: Prefs
) : BaseViewModel<DashboardContract.Event,DashboardContract.State,DashboardContract.Effect>(){

    override fun setInitState(): DashboardContract.State {
        return DashboardContract.State()
    }
    init {
        setState {
            copy(
                name = prefs.getFullName(),
                forwardToDashboard = prefs.getIsNavToParent(),
                openDetail = prefs.getIsNavToDetail(),
                addExtraCycle = prefs.getAddExtraCycleCount(),
                validatePallet = prefs.getValidatePallet(),
                accessPermissions = prefs.getAccessPermission(),
                selectedWarehouse = prefs.getWarehouse()
            )
        }
        visibleDashboardItems()
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
//        getCurrentUser()
//        getVersionInfo()
    }

    override fun onEvent(event: DashboardContract.Event) {
        when(event){
            is DashboardContract.Event.OnNavigate -> setEffect {
                Navigate(event.destination)
            }

            is DashboardContract.Event.OnSelectTab -> {
                setState {
                    copy(selectedTab = event.tab)
                }
                visibleDashboardItems()
            }

            is DashboardContract.Event.OnShowSubDrawers -> {
                setState {
                    copy(subDrawers = event.drawers, subDrawerState = if(event.drawers!=null) SubDrawerState.SubDrawers else SubDrawerState.Drawers)
                }
            }

            DashboardContract.Event.OnLogout -> {
                prefs.setToken("")
                setEffect {
                    RestartActivity
                }
            }

            is DashboardContract.Event.OnLockKeyboardChange ->{
                viewModelScope.launch(Dispatchers.IO) {
                    prefs.setLockKeyboard(event.lock)
                }
                setState {
                    copy(lockKeyboard = event.lock)
                }
            }

            is DashboardContract.Event.ShowSettings -> {
                setState {
                    copy(subDrawerState = if (event.show) SubDrawerState.Settings else SubDrawerState.Drawers)
                }
            }

            is DashboardContract.Event.OnForwardToDashboard -> {
                prefs.setIsNavToParent(event.forward)
                setState {
                    copy(forwardToDashboard = event.forward)
                }
            }
            is DashboardContract.Event.OnOpenDetail -> {
                prefs.setIsNavToDetail(event.open)
                setState {
                    copy(openDetail = event.open)
                }
            }
            is DashboardContract.Event.OnShowChangePasswordDialog -> {
                setState {
                    copy(showChangPasswordDialog = event.show)
                }
            }

            is DashboardContract.Event.OnAddExtraCycleChange -> {
                prefs.setAddExtraCycleCount(event.add)
                setState {
                    copy(addExtraCycle = event.add)
                }
            }

            DashboardContract.Event.FetchData -> {
                getDashboard()
                getWarehouses()
            }

            is DashboardContract.Event.OnValidatePalletChange -> {
                prefs.setValidatePallet(event.validate)
                setState {
                    copy(validatePallet = event.validate)
                }
            }

            is DashboardContract.Event.OnSelectWarehouse -> {
                prefs.setWarehouse(warehouse = event.warehouse)
                setState {
                    copy(selectedWarehouse = event.warehouse)
                }
            }
        }
    }


    private fun visibleDashboardItems() {
        if (state.selectedTab == DashboardTab.Picking){
            setState {
                copy(crossDockDashboardsVisibility = crossDockDashboardsVisibility.mapValues { false })
            }
            viewModelScope.launch {
                state.dashboardsVisibility.onEachIndexed{i,item->
                    if (!item.value){
                        if (state.accessPermissions?.checkAccess(item.key) == true) delay(i*10L)
                        val visibilityItems = state.dashboardsVisibility.toMutableMap()
                        visibilityItems.put(item.key,true)
                        setSuspendedState {
                            copy(dashboardsVisibility = visibilityItems)
                        }
                    }
                }
            }
        } else {
            setState {
                copy(dashboardsVisibility = dashboardsVisibility.mapValues { false })
            }
            viewModelScope.launch {
                state.crossDockDashboardsVisibility.onEachIndexed{i,item->
                    if (!item.value){
                        delay(i*70L)
                        val visibilityItems = state.crossDockDashboardsVisibility.toMutableMap()
                        visibilityItems.put(item.key,true)
                        setSuspendedState {
                            copy(crossDockDashboardsVisibility = visibilityItems)
                        }
                    }
                }
            }
        }
    }

    private fun getDashboard(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDashboard()
                .catch {

                }
                .collect {
                    when(it){
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(dashboard = it.data)
                            }
                        }
                        is BaseResult.Error -> {}
                        is BaseResult.UnAuthorized -> {
                            prefs.setToken("")
                            setEffect {
                                DashboardContract.Effect.RestartActivity
                            }
                        }
                    }
                }
        }
    }

    private fun getWarehouses() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWarehouses()
                .catch{}
                .collect {
                    when(it){
                        is BaseResult.Error -> {}
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(warehouseList = it.data?:emptyList())
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }


    private fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCurrentUser()
                .catch {

                }
                .collect {
                    when(it){
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(name = it.data?.fullName?:"")
                            }
                        }
                        is BaseResult.Error -> {}
                        is BaseResult.UnAuthorized -> {
                            prefs.setToken("")
                            setEffect {
                                DashboardContract.Effect.RestartActivity
                            }
                        }
                    }
                }
        }

    }
    private fun getVersionInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCurrentVersionInfo()
                .catch {

                }
                .collect {
                    if (it is BaseResult.Success){
                        setSuspendedState {
                            copy(
                                showUpdateDialog = (it.data?.currentVersion ?: 0) > BuildConfig.VERSION_CODE,
                                newVersion = it.data?.showVersion?:"",
                                updateUrl = it.data?.downloadUrl?:""
                            )
                        }
                    }
                }
        }
    }
}
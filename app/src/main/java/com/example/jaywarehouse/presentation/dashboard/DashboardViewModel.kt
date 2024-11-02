package com.example.jaywarehouse.presentation.dashboard

import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.BuildConfig
import com.example.jaywarehouse.data.auth.AuthRepository
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.main.MainContract
import kotlinx.coroutines.Dispatchers
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
        prefs.getFullName().let {
            setState {
                copy(name = it)
            }
        }
        getCurrentUser()
        getVersionInfo()
    }

    override fun onEvent(event: DashboardContract.Event) {
        when(event){
            is DashboardContract.Event.OnNavigate -> setEffect {
                DashboardContract.Effect.Navigate(event.destination)
            }

            is DashboardContract.Event.OnSelectTab -> {
                setState {
                    copy(selectedTab = event.tab)
                }
            }

            is DashboardContract.Event.OnShowSubDrawers -> {
                setState {
                    copy(subDrawers = event.drawers)
                }
            }
        }
    }



    private fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCurrentUser()
                .catch {  }
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
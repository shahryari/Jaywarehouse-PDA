package com.example.jaywarehouse.presentation.main

import android.os.Build
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.BuildConfig
import com.example.jaywarehouse.data.auth.AuthRepository
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainViewModel(
    private val prefs: Prefs,
    private val repository: AuthRepository
) : BaseViewModel<MainContract.Event,MainContract.State,MainContract.Effect>(){
    init {
        prefs.getFullName().let {
            setState {
                copy(name = it)
            }
        }
        getCurrentUser()
        getVersionInfo()
    }

    private fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCurrentUser()
                .catch {  }
                .collect {
                    when(it){
                        is BaseResult.Success -> {
                            prefs.setFullName(it.data?.fullName?:"")
                        }
                        is BaseResult.Error -> {}
                        is BaseResult.UnAuthorized -> {
                            prefs.setToken("")
                            setEffect {
                                MainContract.Effect.RestartActivity
                            }
                        }
                    }
                }
        }

    }
    override fun setInitState(): MainContract.State {
        return MainContract.State()
    }

    override fun onEvent(event: MainContract.Event) {
        when(event){
            MainContract.Event.OnUpdate -> setEffect {
                MainContract.Effect.OpenUpdateUrl(state.updateUrl)
            }

            MainContract.Event.OnExit -> {
                setEffect {
                    MainContract.Effect.Exit
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
                        setState {
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
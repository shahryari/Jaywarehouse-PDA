package com.example.jaywarehouse.presentation.profile

import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.auth.AuthRepository
import com.example.jaywarehouse.data.auth.models.ChangePasswordModel
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanModel
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.packing.contracts.PackingDetailContract
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class ProfileViewModel(private val repository: AuthRepository,private val prefs: Prefs)
    : BaseViewModel<ProfileContract.Event,ProfileContract.State,ProfileContract.Effect>()
{

    init {
        setState {
            copy(
                userFullName = prefs.getFullName(),
                address = prefs.getAddress(),
                isNavToDetail = prefs.getIsNavToDetail(),
                isNavToParent = prefs.getIsNavToParent(),
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }

    override fun setInitState(): ProfileContract.State {
        return ProfileContract.State()
    }

    override fun onEvent(event: ProfileContract.Event) {
        when(event){
            ProfileContract.Event.OnLogout -> {
                prefs.setToken("")
                setEffect {
                    ProfileContract.Effect.RestartActivity
                }
            }
            ProfileContract.Event.OnNavBack -> {
                setEffect {
                    ProfileContract.Effect.NavBack
                }
            }
            is ProfileContract.Event.OnPasswordChange -> {
                setState {
                    copy(password = event.password)
                }
            }
            ProfileContract.Event.OnSubmitPassword -> {
                viewModelScope.launch(Dispatchers.IO) {
                    setSuspendedState {
                        copy(isLoading = true)
                    }
                    if (state.password.text.isNotEmpty())
                        repository.changePassword(state.password.text)
                            .catch {
                                setSuspendedState {
                                    copy(error = it.message.toString(), isLoading = false)
                                }
                            }
                            .collect {
                                setSuspendedState {
                                    copy(showChangePassword = false, isLoading = false)
                                }
                                when(it){
                                    is BaseResult.Success -> {
                                        setSuspendedState {
                                            copy(toast = "Password changed successfully")
                                        }
                                    }
                                    is BaseResult.Error -> {
                                        val data = if (it.message.isNotEmpty()) {
                                            try {
                                                Gson().fromJson(it.message,
                                                    ChangePasswordModel::class.java).message
                                            }catch (e:Exception){
                                                it.message
                                            }
                                        } else it.data?.message
                                        setSuspendedState {
                                            copy(error = data?:"")
                                        }
                                    }
                                    else -> {}
                                }
                            }
                }
            }
            is ProfileContract.Event.ShowChangeAddress -> {
                setState {
                    copy(showChangeAddress = event.showChangeAddress)
                }
            }
            is ProfileContract.Event.ShowChangePassword -> {
                setState {
                    copy(showChangePassword = event.showChangePassword)
                }
            }

            ProfileContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            ProfileContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            is ProfileContract.Event.OnNavToDetailChange -> {
                prefs.setIsNavToDetail(event.isNavToDetail)
                setState {
                    copy(isNavToDetail = event.isNavToDetail)
                }
            }
            is ProfileContract.Event.OnNavToParentChange -> {
                prefs.setIsNavToParent(event.isNavToParent)
                setState {
                    copy(isNavToParent = event.isNavToParent)
                }
            }

            is ProfileContract.Event.OnLockKeyboardChange -> {
                viewModelScope.launch(Dispatchers.IO) {1
                    prefs.setLockKeyboard(event.lock)
                }
                setState {
                    copy(lockKeyboard = event.lock)
                }
            }
        }
    }
}
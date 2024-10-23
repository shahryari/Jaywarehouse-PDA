package com.example.jaywarehouse.presentation.auth

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.auth.AuthRepository
import com.example.jaywarehouse.data.auth.authModule
import com.example.jaywarehouse.data.auth.models.LoginErrorModel
import com.example.jaywarehouse.data.auth.models.LoginModel
import com.example.jaywarehouse.data.common.modules.mainModule
import com.example.jaywarehouse.data.common.modules.networkModule
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Encryptor
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanModel
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class LoginViewModel(
    private val repository: AuthRepository,
    private val prefs: Prefs
) : BaseViewModel<LoginContract.Event,LoginContract.State,LoginContract.Effect>(){
    init {
        val encryptor = Encryptor.getInstance()
        setState {
            val pass = prefs.getPassword()
            copy(
                userName = TextFieldValue(prefs.getUserName()),
                password = if(pass.isNotEmpty()) TextFieldValue(encryptor.decode(pass)) else TextFieldValue(""),
                rememberMe = pass.isNotEmpty(),
                address = TextFieldValue(prefs.getAddress())
            )
        }

        setState {
            copy()
        }
    }
    override fun setInitState(): LoginContract.State {
        return LoginContract.State()
    }

    override fun onEvent(event: LoginContract.Event) {
        when(event) {
            LoginContract.Event.OnLoginClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    setSuspendedState { copy(isLoading = true) }
                    repository.login(state.userName.text,state.password.text,state.rememberMe)
                        .catch {
                            setSuspendedState {
                                copy(isLoading = false, error = it.message.toString())
                            }
                        }
                        .collect {
                            setSuspendedState {
                                copy(isLoading = false)
                            }
                            when(it){
                                is BaseResult.Error -> {
                                    val data = if (it.message.isNotEmpty()) {
                                        try {
                                            Gson().fromJson(it.message, LoginErrorModel::class.java).message
                                        }catch (e:Exception){
                                            it.message
                                        }
                                    } else it.data?.message
                                    setSuspendedState {
                                        copy(error = data?:"")
                                    }
                                    Log.i("login", "onEvent: ${it.message}")
                                }
                                is BaseResult.Success -> setEffect { LoginContract.Effect.NavToMain }
                                BaseResult.UnAuthorized -> {

                                }
                            }
                        }
                }
            }
            is LoginContract.Event.OnPasswordChange -> {
                setState {
                    copy(password = event.password)
                }
            }
            is LoginContract.Event.OnRememberMeChange -> {
                setState {
                    copy(rememberMe = event.rememberMe)
                }
            }
            is LoginContract.Event.OnUserNameChange -> {
                setState {
                    copy(userName = event.userName)
                }
            }

            LoginContract.Event.CloseError -> {
                setState { copy(error = "") }
            }

            is LoginContract.Event.OnShowPassword -> {
                setState {
                    copy(showPassword = event.show)
                }
            }

            LoginContract.Event.OnChangeDomain -> {
                if (state.address.text.toHttpUrlOrNull()!=null){
                    prefs.setAddress(state.address.text)
                    setState {
                        copy(toast = "Address changed successfully", showDomain = false)
                    }
                    unloadKoinModules(listOf(networkModule, authModule, mainModule))
                    loadKoinModules(listOf(networkModule, authModule, mainModule))
                    setEffect { LoginContract.Effect.RestartActivity }
                }
                else setState {
                    copy(error = "Invalid address")
                }
            }
            is LoginContract.Event.OnShowDomain -> {
                setState {
                    copy(showDomain = event.show)
                }
            }

            is LoginContract.Event.OnAddressChange -> {
                setState {
                    copy(address = event.address)
                }
            }

            LoginContract.Event.HideToast -> {
                setState { copy(toast = "") }
            }
        }
    }
}
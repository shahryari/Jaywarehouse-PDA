package com.example.jaywarehouse.presentation.auth

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class LoginContract {

    data class State(
        val userName: TextFieldValue = TextFieldValue(),
        val password: TextFieldValue = TextFieldValue(),
        val rememberMe: Boolean = false,
        val isLoading: Boolean = false,
        val showPassword: Boolean = false,
        val error: String = "",
        val address: TextFieldValue = TextFieldValue(),
        val toast: String = "",
        val showDomain: Boolean =false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnUserNameChange(val userName: TextFieldValue) : Event()
        data class OnPasswordChange(val password: TextFieldValue) : Event()
        data class OnRememberMeChange(val rememberMe: Boolean) : Event()
        data object OnLoginClick : Event()
        data class OnShowPassword(val show: Boolean) : Event()
        data object CloseError: Event()
        data object HideToast: Event()
        data class OnShowDomain(val show: Boolean) : Event()
        data object OnChangeDomain: Event()
        data class OnAddressChange(val address: TextFieldValue) : Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavToMain : Effect()
        data object RestartActivity : Effect()
    }
}
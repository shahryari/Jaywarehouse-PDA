package com.linari.presentation.auth

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState
import com.linari.presentation.dashboard.DashboardContract.Effect
import com.linari.presentation.dashboard.DashboardContract.Event

class LoginContract {

    data class State(
        val userName: TextFieldValue = TextFieldValue(),
        val password: TextFieldValue = TextFieldValue(),
        val rememberMe: Boolean = false,
        val isLoading: Boolean = false,
        val showPassword: Boolean = false,
        val error: Int? = null,
        val serverError: String = "",
        val address: TextFieldValue = TextFieldValue(),
        val toast: Int? = null,
        val serverToast: String = "",
        val showDomain: Boolean =false,
        val domainPrefix: String = "https://",
        val showUpdateDialog: Boolean = false,
        val newVersion: String = "",
        val updateUrl: String = "",
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
        data class OnChangePrefix(val prefix: String) : Event()
        data object DownloadUpdate : Event()
        data object OnCloseApp: Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavToMain : Effect()
        data object RestartActivity : Effect()

        data class DownloadUpdate(val url: String) : Effect()
        data object CloseApp : Effect()
    }
}
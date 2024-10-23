package com.example.jaywarehouse.presentation.profile

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class ProfileContract {
    data class State(
        val showChangePassword: Boolean = false,
        val showChangeAddress: Boolean = false,
        val userFullName: String = "",
        val toast: String = "",
        val isLoading: Boolean = false,
        val isNavToParent: Boolean = false,
        val isNavToDetail: Boolean = false,
        val lockKeyboard: Boolean = false,
        val error: String = "",
        val password: TextFieldValue = TextFieldValue(),
        val address: String = "",
    ) : UiState

    sealed class Event : UiEvent {
        data class ShowChangePassword(val showChangePassword: Boolean) : Event()
        data class ShowChangeAddress(val showChangeAddress: Boolean) : Event()
        data class OnPasswordChange(val password: TextFieldValue) : Event()
        data class OnNavToParentChange(val isNavToParent: Boolean) : Event()
        data class OnNavToDetailChange(val isNavToDetail: Boolean) : Event()
        data class OnLockKeyboardChange(val lock: Boolean) : Event()
        data object OnSubmitPassword: Event()
        data object OnLogout: Event()
        data object HideToast: Event()
        data object CloseError: Event()
        data object OnNavBack : Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack : Effect()
        data object RestartActivity: Effect()
    }
}
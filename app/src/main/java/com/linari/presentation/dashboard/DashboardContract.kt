package com.linari.presentation.dashboard

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.auth.models.AccessPermissionModel
import com.linari.data.auth.models.DashboardModel
import com.linari.data.auth.models.WarehouseModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.MainItems
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState
import com.ramcosta.composedestinations.spec.Direction
import java.io.File

class DashboardContract {
    data class State(
        val dashboards: Map<String, List<MainItems>> = MainItems.entries.groupBy { it.category },
        val dashboardsVisibility: Map<MainItems, Boolean> = MainItems.entries.associate {
            Pair(
                it,
                false
            )
        },
        val crossDockDashboards: Map<String, List<MainItems>> = mapOf(
            "" to listOf(
                MainItems.Receiving,
                MainItems.Picking,
                MainItems.Checking,
                MainItems.PalletConfirm
            )
        ),
        val crossDockDashboardsVisibility: Map<MainItems, Boolean> = mapOf(
            MainItems.Receiving to false,
            MainItems.Picking to false,
            MainItems.Checking to false,
            MainItems.PalletConfirm to false,
        ),
        val name: String = "",
        val showUpdateDialog: Boolean = false,
        val newVersion: String = "",
        val updateUrl: String = "",
        val subDrawerState: SubDrawerState = SubDrawerState.Drawers,
        val accessPermissions: AccessPermissionModel? = null,
        val subDrawers: List<MainItems>? = null,
        val dashboard: DashboardModel? = null,
        val lockKeyboard: Boolean = false,
        val forwardToDashboard: Boolean = false,
        val openDetail: Boolean = false,
        val selectedTab: DashboardTab = DashboardTab.Picking,
        val loadingState: Loading = Loading.NONE,
        val addExtraCycle: Boolean = false,
        val selectedWarehouse: WarehouseModel? = null,
        val warehouseList: List<WarehouseModel> = emptyList(),
        val validatePallet: Boolean = true,
        val enableTracking: Boolean = true,
        val showWarehouseList: Boolean = false,
        val password: TextFieldValue = TextFieldValue(""),
        val oldPassword: TextFieldValue = TextFieldValue(""),
        val confirmPassword: TextFieldValue = TextFieldValue(""),
        val showChangePasswordDialog: Boolean = false,
        val showPassword: Boolean = false,
        val showConfirmPassword: Boolean = false,
        val error : String = "",
        val isChangingPassword: Boolean = false,
        val toast: String = "",
        val profile: File? = null,
        val showChangeProfileDialog: Boolean = false,
        val showOldPassword: Boolean = false,
        val isSavingProfile: Boolean = false,
        val enableAutoOpenChecking: Boolean = false,
        val savedProfile: String = "",
        val cookie: String = ""
    ) : UiState

    sealed class Event : UiEvent {
        data class OnNavigate(val destination: Direction) : Event()
        data class OnSelectTab(val tab: DashboardTab) : Event()
        data class OnShowSubDrawers(val drawers: List<MainItems>?) : Event()
        data class ShowSettings(val show: Boolean) : Event()
        data object OnLogout: Event()
        data class FetchData(val loading: Loading = Loading.LOADING): Event()
        data class OnLockKeyboardChange(val lock: Boolean) : Event()
        data class OnForwardToDashboard(val forward: Boolean) : Event()
        data class OnOpenDetail(val open: Boolean) : Event()
        data class OnShowChangePasswordDialog(val show: Boolean) : Event()
        data class OnAddExtraCycleChange(val add: Boolean): Event()
        data class OnValidatePalletChange(val validate: Boolean): Event()
        data class OnEnableAutoOpenChecking(val enable: Boolean): Event()
        data class OnSelectWarehouse(val warehouse: WarehouseModel) : Event()
        data class OnShowWarehouseList(val show: Boolean) : Event()
        data object DownloadUpdate : Event()
        data object OnCloseApp: Event()
        data class OnChangePassword(val password: TextFieldValue) : Event()
        data class OnChangeConfirmPassword(val password: TextFieldValue) : Event()
        data class OnShowPassword(val show: Boolean) : Event()
        data class OnShowConfirmPassword(val show: Boolean) : Event()
        data class OnChangeOldPassword(val password: TextFieldValue) : Event()
        data class OnShowOldPassword(val show: Boolean) : Event()
        data object ChangePassword: Event()
        data object CloseError: Event()
        data object CloseToast: Event()
        data class ChangeProfile(val profile: File?) : Event()
        data class ShowChangeProfileDialog(val show: Boolean) : Event()
        data object OnOpenGallery: Event()
        data object OnOpenCamera: Event()
        data object SaveProfile: Event()
        data class ShowDownloadUpdate(val show: Boolean) : Event()
        data class EnableTracking(val enable: Boolean) : Event()
    }

    sealed class Effect : UiSideEffect {
        data class Navigate(val destination: Direction) : Effect()
        data object RestartActivity : Effect()
        data class DownloadUpdate(val url: String) : Effect()
        data object CloseApp : Effect()
        data object OpenGallery : Effect()
        data object OpenCamera : Effect()
    }
}

enum class DashboardTab(val title: String) {
    Picking("Picking"),CrossDock("Cross Dock")
}

enum class SubDrawerState {
    Drawers,SubDrawers,Settings
}
package com.linari.presentation.dashboard

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.BuildConfig
import com.linari.data.auth.AuthRepository
import com.linari.data.common.utils.BASE_PROFILE_URL
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.PROFILE_URL
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.compressImageFileToMaxSize
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.dashboard.DashboardContract.Effect.*
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
                selectedWarehouse = prefs.getWarehouse(),
                savedProfile = prefs.getAddress()+ BASE_PROFILE_URL +prefs.getProfile(),
                cookie = prefs.getToken(),
                enableAutoOpenChecking = prefs.getEnableAutoOpenChecking()
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
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getTracking().collect {
                setSuspendedState {
                    copy(enableTracking = it)
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
                    copy(showChangePasswordDialog = event.show, password = TextFieldValue(), confirmPassword = TextFieldValue(), oldPassword = TextFieldValue())
                }
            }

            is DashboardContract.Event.OnAddExtraCycleChange -> {
                prefs.setAddExtraCycleCount(event.add)
                setState {
                    copy(addExtraCycle = event.add)
                }
            }

            is DashboardContract.Event.FetchData -> {
                getDashboard(event.loading)
                getWarehouses()
                getVersionInfo()
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
                getDashboard()
            }

            is DashboardContract.Event.OnShowWarehouseList -> {
                setState {
                    copy(showWarehouseList =event.show)
                }
            }

            DashboardContract.Event.DownloadUpdate -> {
                setEffect {
                    DownloadUpdate(state.updateUrl)
                }
            }
            is DashboardContract.Event.OnChangeConfirmPassword ->{
                setState {
                    copy(confirmPassword = event.password)
                }
            }
            is DashboardContract.Event.OnChangePassword -> {
                setState {
                    copy(password = event.password)
                }
            }
            DashboardContract.Event.OnCloseApp -> {
                setEffect {
                    CloseApp
                }
            }
            is DashboardContract.Event.OnShowConfirmPassword -> {
                setState {
                    copy(showConfirmPassword = event.show)
                }
            }
            is DashboardContract.Event.OnShowPassword -> {
                setState {
                    copy(showPassword = event.show)
                }
            }

            DashboardContract.Event.ChangePassword -> {
                changePassword()
            }
            DashboardContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            DashboardContract.Event.CloseToast -> {
                setState {
                    copy(toast = "")
                }
            }

            is DashboardContract.Event.ChangeProfile -> {
                setState {
                    copy(profile = event.profile)
                }
            }
            is DashboardContract.Event.OnChangeOldPassword -> {
                setState {
                    copy(oldPassword = event.password)
                }
            }
            DashboardContract.Event.OnOpenCamera -> {
                setEffect {
                    OpenCamera
                }
            }
            DashboardContract.Event.OnOpenGallery -> {
                setEffect {
                    OpenGallery
                }
            }
            is DashboardContract.Event.OnShowOldPassword -> {
                setState {
                    copy(showOldPassword = event.show)
                }
            }
            is DashboardContract.Event.ShowChangeProfileDialog -> {
                setState {
                    copy(showChangeProfileDialog = event.show, profile = null)
                }
            }
            DashboardContract.Event.SaveProfile -> {
                saveProfile()
            }

            is DashboardContract.Event.ShowDownloadUpdate -> {
                setState {
                    copy(showUpdateDialog = event.show)
                }
            }

            is DashboardContract.Event.EnableTracking -> {
                viewModelScope.launch(Dispatchers.IO) {
                    prefs.setTracking(event.enable)
                }
                setState {
                    copy(enableTracking = event.enable)
                }
            }

            is DashboardContract.Event.OnEnableAutoOpenChecking -> {
                prefs.setEnableAutoOpenChecking(event.enable)
                setState {
                    copy(enableAutoOpenChecking = event.enable)
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

    private fun getDashboard(loading: Loading = Loading.LOADING) {
        setState {
            copy(loadingState = loading)
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDashboard(
                prefs.getWarehouse()!!.id
            )
                .catch {
                    setSuspendedState {
                        copy(loadingState = Loading.NONE)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(loadingState = Loading.NONE)
                    }
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

    private fun changePassword() {

        if (!state.isChangingPassword){
            setState {
                copy(isChangingPassword = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.changePassword(
                    oldPassword = state.oldPassword.text,
                    password = state.password.text,
                    confirmPassword = state.confirmPassword.text
                ).catch {
                        setSuspendedState {
                            copy(error = it.message.toString(), isChangingPassword = false)
                        }
                    }
                    .collect {
                        setSuspendedState {
                            copy(isChangingPassword = false)
                        }
                        when(it){
                            is BaseResult.Success -> {
                                if (it.data?.isSucceed == true){
                                    setSuspendedState {
                                        copy(toast = "Password changed successfully", showChangePasswordDialog = false)
                                    }
                                    prefs.setToken("")
                                    setEffect {
                                        RestartActivity
                                    }
                                }
                            }
                            is BaseResult.Error -> {
                                setSuspendedState {
                                    copy(error = it.message)
                                }
                            }
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
    }

    private fun saveProfile() {
        val file = state.profile
        if(file == null){
            setState {
                copy(error = "Please select profile image")
            }
            return
        }
        if (!state.isSavingProfile){
            setState {
                copy(isSavingProfile = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                val out = compressImageFileToMaxSize(file,200)
                repository.uploadFile(
                    out
                ).catch {
                    setSuspendedState {
                        copy(error = it.message.toString(), isSavingProfile = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isSavingProfile = false)
                    }
                    when(it){
                        is BaseResult.Success -> {
                            if (it.data?.isSucceed == false){
                                setSuspendedState {
                                    copy(error = it.data.messages.firstOrNull()?:"")
                                }
                                return@collect
                            }
                            setSuspendedState {
                                copy(toast = "Profile saved successfully", showChangeProfileDialog = false, savedProfile = "")
                            }
                            delay(50)
                            setSuspendedState {
                                copy(
                                    savedProfile = prefs.getAddress()+BASE_PROFILE_URL+prefs.getProfile()
                                )
                            }
                        }
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
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
    }
}
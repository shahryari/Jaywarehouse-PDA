package com.linari.presentation.rs.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.rs.RSRepository
import com.linari.data.rs.models.PODInvoiceRow
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.rs.contracts.RSIntegrationContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RSIntegrationViewModel(
    private val repository: RSRepository,
    private val prefs: Prefs
) : BaseViewModel<RSIntegrationContract.Event, RSIntegrationContract.State, RSIntegrationContract.Effect>(){

    init {
        val sortItem = state.sortList.find {
            it.sort == prefs.getRSSort() && it.order == Order.Companion.getFromValue(prefs.getRSOrder())
        }
        if (sortItem!=null) setState {
            copy(sort = sortItem)
        }


        viewModelScope.launch(Dispatchers.IO) {

            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }


    override fun setInitState(): RSIntegrationContract.State {
        return RSIntegrationContract.State()
    }

    override fun onEvent(event: RSIntegrationContract.Event) {
        when(event){
            RSIntegrationContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            RSIntegrationContract.Event.FetchData -> {
                setState {
                    copy(page = 1, loadingState = Loading.LOADING, rsList = emptyList())
                }
                getPODInvoice()
            }
            is RSIntegrationContract.Event.OnCarNumberChange -> {
                setState {
                    copy(carNumber = event.carNumber)
                }
            }
            is RSIntegrationContract.Event.OnDriverChange -> {
                setState {
                    copy(driver = event.driver)
                }
            }
            is RSIntegrationContract.Event.OnDriverTinChange -> {
                setState {
                    copy(driverTin = event.driverTin)
                }
            }
            RSIntegrationContract.Event.OnNavBack -> {
                setEffect {
                    RSIntegrationContract.Effect.NavBack
                }
            }
            RSIntegrationContract.Event.OnReachEnd -> {
                if (ROW_COUNT *state.page<=state.rsList.size) {
                    setState {
                        copy(page = state.page + 1, loadingState = Loading.LOADING)
                    }
                    getPODInvoice()
                }
            }
            RSIntegrationContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, loadingState = Loading.REFRESHING, rsList = emptyList())
                }
                getPODInvoice()
            }
            RSIntegrationContract.Event.OnScanDriverTin -> {
                getDriverInfo()
            }
            is RSIntegrationContract.Event.OnSearch -> {
                setState {
                    copy(page =1, loadingState = Loading.SEARCHING, rsList = emptyList(), keyword = event.keyword)
                }
                getPODInvoice()
            }
            is RSIntegrationContract.Event.OnSelectRs -> {
                setState {
                    copy(
                        selectedRs = event.rs,
                        driver = TextFieldValue(),
                        driverTin = TextFieldValue(),
                        carNumber = TextFieldValue(),
                        trailer = TextFieldValue(),
                        selectedDriver = null
                    )
                }
            }
            is RSIntegrationContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }
            is RSIntegrationContract.Event.OnSortChange -> {
                prefs.setRSSort(event.sort.sort)
                prefs.setRSOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort, page = 1, rsList = emptyList(), loadingState = Loading.LOADING)
                }
                getPODInvoice()
            }
            is RSIntegrationContract.Event.OnSubmit -> {
                updateDriver(event.rs)
            }
            is RSIntegrationContract.Event.OnTrailerChange -> {
                setState {
                    copy(trailer = event.trailer)
                }
            }
        }
    }

    private fun getPODInvoice(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPODInvoice(state.keyword,state.page,state.sort.sort,state.sort.order.value)
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"", loadingState = Loading.NONE)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(loadingState = Loading.NONE)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(
                                    rsList = rsList + (it.data?.rows ?: emptyList()),
                                    rowCount = it.data?.total?:0
                                )
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }


    private fun updateDriver(
        selectedPod: PODInvoiceRow
    ) {
        if (!state.isSubmitting) {
            setState {
                copy(isSubmitting = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateDriver(
                    shippingId = selectedPod.shippingID.toString(),
                    driverFullName = state.driver.text,
                    driverTin = state.driverTin.text,
                    carNumber = state.carNumber.text,
                    trailerNumber = state.trailer.text
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isSubmitting = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isSubmitting = false)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            if (it.data?.isSucceed == true){
                                setSuspendedState {
                                    copy(selectedRs = null, page = 1, rsList = emptyList(), loadingState = Loading.LOADING)
                                }
                                getPODInvoice()
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
            }
        }
    }
    private fun getDriverInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDriverInfo(state.driverTin.text)
                .catch {
                    setSuspendedState {
                        copy(error =  it.message?:"")
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(isDriverScanned = true)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            val driver = it.data
                            if (driver!=null){
                                setSuspendedState {
                                    copy(
                                        selectedDriver = driver,
                                        driver = TextFieldValue(driver.fullName),
                                        carNumber = TextFieldValue(driver.carNumber),
                                        trailer = TextFieldValue(driver.trailerNumber)
                                    )
                                }

                            }
                        }
                        BaseResult.UnAuthorized -> {
                        }
                    }
                }
        }
    }
}
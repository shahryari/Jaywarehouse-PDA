package com.linari.presentation.rs.viewmodels

import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.rs.RSRepository
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.rs.contracts.WaybillContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WaybillViewModel(
    private val rsRepository: RSRepository,
    private val prefs: Prefs
) : BaseViewModel<WaybillContract.Event, WaybillContract.State, WaybillContract.Effect>() {

    init {
        val sortItem = state.sortList.find { it.sort == prefs.getWaybillSort() && it.order.value == prefs.getWaybillOrder() }
        if (sortItem != null) {
            setState { copy(sort = sortItem) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }

    override fun setInitState(): WaybillContract.State {
        return WaybillContract.State()
    }

    override fun onEvent(event: WaybillContract.Event) {
        when(event){
            is WaybillContract.Event.ChangeSort -> {
                prefs.setWaybillSort(event.sort.sort)
                prefs.setWaybillOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort)
                }
                getWaybills()
            }
            WaybillContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            WaybillContract.Event.CloseToast -> {
                setState {
                    copy(toast = "")
                }
            }
            WaybillContract.Event.FetchData -> {
                getWaybills()
            }
            is WaybillContract.Event.OnIntegrateWaybill -> {
                integrateWithRS(event.waybill.waybillInfoID)
            }
            WaybillContract.Event.OnNavBack -> {
                setEffect {
                    WaybillContract.Effect.NavBack
                }
            }
            WaybillContract.Event.OnReachEnd -> {
                if (state.page* ROW_COUNT <= state.waybillList.size){
                    getWaybills(loadNext = true)
                }
            }
            WaybillContract.Event.OnRefresh -> {
                getWaybills(Loading.REFRESHING)
            }
            is WaybillContract.Event.OnSearch -> {
                setState {
                    copy(keyword = event.keyword)
                }
                getWaybills(Loading.SEARCHING)
            }
            is WaybillContract.Event.OnSelectWaybill -> {
                setState {
                    copy(selectedWaybill = event.waybill)
                }
            }
            is WaybillContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }
        }
    }

    private fun getWaybills(loading: Loading = Loading.LOADING, loadNext: Boolean = false) {
        if (state.loadingState == Loading.NONE){
            if (loadNext){
                setState {
                    copy(page = page  + 1, loadingState = loading)
                }
            } else {
                setState {
                    copy(page = 1, waybillList = emptyList(), loadingState = loading)
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                rsRepository.getWaybillInfoes(
                    state.keyword,
                    state.page,
                    state.sort.sort,
                    state.sort.order.value
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", loadingState = Loading.NONE)
                    }
                }.collect {
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
                                    waybillList = waybillList + (it.data?.rows ?: emptyList()),
                                    rowCount = it.data?.total?:0
                                )
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
            }
        }
    }

    private fun integrateWithRS(waybillInfoID: Int) {
        if (!state.isIntegrating){
            setState {
                copy(isIntegrating = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                rsRepository.integrateWithRS(waybillInfoID)
                    .catch {
                        setSuspendedState {
                            copy(error = it.message?:"", isIntegrating = false, selectedWaybill = null)
                        }
                    }.collect {
                        setSuspendedState {
                            copy(isIntegrating = false, selectedWaybill = null)
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
                                        copy(toast = it.data.messages?.firstOrNull()?: "Completed Successfully")
                                    }
                                    getWaybills()
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
}
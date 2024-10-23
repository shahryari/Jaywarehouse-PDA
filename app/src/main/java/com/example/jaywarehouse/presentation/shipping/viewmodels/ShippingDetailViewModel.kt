package com.example.jaywarehouse.presentation.shipping.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.shipping.ShippingRepository
import com.example.jaywarehouse.data.shipping.models.ShippingRow
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.shipping.contracts.ShippingDetailContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ShippingDetailViewModel(
    private val repository: ShippingRepository,
    private val prefs: Prefs,
    private val row: ShippingRow
) : BaseViewModel<ShippingDetailContract.Event, ShippingDetailContract.State, ShippingDetailContract.Effect>() {

    init {
        setState {
            copy(shippingRow = row, sort = prefs.getShippingDetailSort(), order = prefs.getShippingDetailOrder())
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
        getShippingDetail(row.shippingID, sort = prefs.getShippingDetailSort(), order = prefs.getShippingDetailOrder())
    }

    override fun setInitState(): ShippingDetailContract.State {
        return ShippingDetailContract.State()
    }

    override fun onEvent(event: ShippingDetailContract.Event) {
        when(event){
            ShippingDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }
            is ShippingDetailContract.Event.OnBarcodeChange -> {
                setState {
                    copy(barcode = event.barcode)
                }
                if ((event.barcode.text.endsWith('\n') || (event.barcode.text.endsWith('\r'))) && state.shippingRow!=null){
                    if (state.loadingState == Loading.NONE)addShippingDetail(event.barcode.text)
                }
            }
            ShippingDetailContract.Event.OnClearError -> {
                setState {
                    copy(error = "")
                }
            }
            ShippingDetailContract.Event.OnInvoice -> {
                if (state.shippingRow!=null)invoice(state.shippingRow!!.shippingID)
            }
            ShippingDetailContract.Event.OnNavBack -> {
                setEffect {
                    ShippingDetailContract.Effect.NavBack
                }
            }
            is ShippingDetailContract.Event.OnOrderChange -> {
                prefs.setShippingDetailOrder(event.order)
                setState {
                    copy(order = event.order, page = 1, shippingDetailList = emptyList(), loadingState = Loading.LOADING)
                }
                getShippingDetail(
                    state.shippingRow!!.shippingID,
                    state.keyword.text,
                    sort = state.sort,
                    order = event.order
                )
            }
            is ShippingDetailContract.Event.OnRemoveShippingDetail -> {
                removeShippingDetail(event.packingId)
            }
            is ShippingDetailContract.Event.OnKeywordChange -> {
                setState {
                    copy(keyword = event.keyword)
                }
            }
            is ShippingDetailContract.Event.OnSelectShippingDetail -> {
                setState {
                    copy(selectedShippingDetail = event.packingId)
                }
            }
            is ShippingDetailContract.Event.OnShowFilterList -> {
                setState {
                    copy(showFilterList = event.showFilterList)
                }
            }
            is ShippingDetailContract.Event.OnSortChange -> {
                prefs.setShippingDetailSort(event.sort)
                setState {
                    copy(sort = event.sort, page = 1, shippingDetailList = emptyList(), loadingState = Loading.LOADING)
                }
                getShippingDetail(
                    state.shippingRow!!.shippingID,
                    state.keyword.text,
                    sort = event.sort,
                    order = state.order
                )
            }
            ShippingDetailContract.Event.ScanBarcode -> {
                if (state.loadingState == Loading.NONE) addShippingDetail(state.barcode.text)
            }

            is ShippingDetailContract.Event.OnShowInvoiceConfirm -> {
                setState {
                    copy(showInvoiceConfirm = event.show)
                }
            }

            ShippingDetailContract.Event.OnReachEnd -> {
                if (10*state.page<=state.shippingDetailList.size){
                    setState {
                        copy(page = page + 1, loadingState = Loading.LOADING)
                    }
                    getShippingDetail(row.shippingID)
                }
            }

            ShippingDetailContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, shippingDetailList = emptyList(), loadingState = Loading.SEARCHING)
                }
                getShippingDetail(
                    state.shippingRow!!.shippingID,
                    state.keyword.text,
                    sort = state.sort,
                    order = state.order
                )
            }

            ShippingDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, shippingDetailList = emptyList(), loadingState = Loading.REFRESHING)
                }
                getShippingDetail(
                    state.shippingRow!!.shippingID,
                    state.keyword.text,
                    sort = state.sort,
                    order = state.order
                )
            }

            ShippingDetailContract.Event.OnShowAll -> {
                setState {
                    copy(showAll = !showAll)
                }
            }
        }
    }

    private fun getShippingDetail(
        shippingId: Int,
        keyword: String = "",
        page: Int = 1,
        sort: String = "CreatedOn",
        order: String = Order.Asc.value
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            repository.getShippingDetail(shippingId,keyword,page,10,sort,order)
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString(), loadingState = Loading.NONE)
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
                                    shippingDetailModel = it.data,
                                    shippingDetailList = shippingDetailList+(it.data?.rows?: emptyList()),
                                    shippingRow = it.data?.shipping
                                )
                            }
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun removeShippingDetail(packingId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            setSuspendedState {
                copy(loadingState = Loading.LOADING)
            }
            repository.removeShippingDetail(packingId)
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString(), loadingState = Loading.NONE)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(selectedShippingDetail = null)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message, loadingState =Loading.NONE)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(toast = it.data?.messages?.firstOrNull() ?: "", page = 1, shippingDetailList = emptyList(), loadingState = Loading.LOADING)
                            }
                            getShippingDetail(row.shippingID)
                        }
                        else -> {
                            setSuspendedState {
                                copy(loadingState = Loading.NONE)
                            }
                        }
                    }

                }
        }
    }

    private fun addShippingDetail(packingNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            setSuspendedState {
                copy(loadingState = Loading.LOADING)
            }
            repository.addShippingDetail(state.shippingRow?.shippingID?:-1, packingNumber)
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString(), loadingState = Loading.NONE)
                    }
                }
                .collect {
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message, loadingState = Loading.NONE)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(toast = it.data?.messages?.firstOrNull() ?: "", page = 1, shippingDetailList = emptyList(), barcode = TextFieldValue(), loadingState = loadingState)
                            }
                            getShippingDetail(row.shippingID)
                        }
                        else -> {
                            setSuspendedState {
                                copy(loadingState = Loading.NONE)
                            }
                        }
                    }
                }
        }
    }

    private fun invoice(shippingId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.invoice(shippingId)
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString(), showInvoiceConfirm = false)
                    }
                }
                .collect {
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message, showInvoiceConfirm = false)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(toast = it.data?.messages?.firstOrNull() ?: "", showInvoiceConfirm = false)
                            }
                            setEffect {
                                ShippingDetailContract.Effect.NavBack
                            }
                        }
                        else -> {}
                    }
                }
        }
    }
}
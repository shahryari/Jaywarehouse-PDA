package com.linari.presentation.transfer.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.transfer.TransferRepository
import com.linari.data.transfer.models.TransferRow
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.transfer.contracts.TransferContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TransferViewModel(
    private val repository: TransferRepository,
    private val prefs: Prefs
) : BaseViewModel<TransferContract.Event,TransferContract.State,TransferContract.Effect>(){

    init {
        val sort = state.sortList.find {
            it.sort == prefs.getTransferSort() && it.order == Order.getFromValue(prefs.getTransferOrder())
        }
        if (sort!=null) setState {
            copy(sort = sort)
        }
        setState {
            copy(hasTransfer = prefs.getAccessPermission()?.hasTransfer == true, warehouse = prefs.getWarehouse())
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }

    override fun setInitState(): TransferContract.State {
        return TransferContract.State()
    }

    override fun onEvent(event: TransferContract.Event) {
        when(event){
            TransferContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            is TransferContract.Event.OnChangeSort -> {
                prefs.setTransferSort(event.sort.sort)
                prefs.setTransferOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort, transferList = emptyList(), page = 1)
                }
                getTransferList()
            }
            is TransferContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }

            TransferContract.Event.ReloadScreen -> {

                setState {
                    copy(page = 1, transferList = emptyList(), keyword = "")
                }

                getTransferList()
            }

            TransferContract.Event.OnReachedEnd -> {
                if (ROW_COUNT*state.page<=state.transferList.size) {
                    setState {
                        copy(page = state.page+1)
                    }
                    getTransferList()
                }
            }

            is TransferContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, transferList = emptyList(), keyword = event.keyword)
                }
                getTransferList(Loading.SEARCHING)
            }

            TransferContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, transferList = emptyList())
                }
                getTransferList(Loading.REFRESHING)
            }

            TransferContract.Event.OnBackPressed -> {
                setEffect {
                    TransferContract.Effect.NavBack
                }
            }

            TransferContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }
            is TransferContract.Event.OnChangeDestination -> {
                setState {
                    copy(destination = event.destination)
                }
//                getWarehouseLocations(event.destination.text)
            }
            is TransferContract.Event.OnChangeExpirationDate -> {
                setState {
                    copy(expirationDate = event.expirationDate)
                }
            }
            is TransferContract.Event.OnChangeProductStatus -> {
                setState {
                    copy(productStatus = event.productStatus)
                }
            }
            is TransferContract.Event.OnChangeQuantity -> {
                setState {
                    copy(quantity = event.quantity)
                }
            }
            is TransferContract.Event.OnSelectTransfer -> {
                setState {
                    copy(selectedTransfer = event.transferRow)
                }
                if (event.transferRow!=null){
                    getProductStatuses()
                    getWarehouseLocations("",event.transferRow.warehouseID)
                }
            }
            is TransferContract.Event.OnTransfer -> {
                transfer(event.row)
            }

            is TransferContract.Event.OnShowDatePicker -> {
                setState {
                    copy(showDatePicker = event.show)
                }
            }

            is TransferContract.Event.OnSelectProductStatus -> {
                setState {
                    copy(selectedProductStatus = event.productStatus)
                }
            }
            is TransferContract.Event.OnSelectWarehouseLocation -> {
                setState {
                    copy(selectedLocation = event.location)
                }
            }

            is TransferContract.Event.OnShowStatusList -> {
                setState {
                    copy(showStatusList = event.show)
                }
            }

            is TransferContract.Event.ChangeBarcodeKeyword ->  {
                setState {
                    copy(barcodeKeyword = event.keyword)
                }
            }
            is TransferContract.Event.ChangeLocationKeyword -> {
                setState {
                    copy(locationKeyword = event.keyword)
                }
            }
            is TransferContract.Event.ChangeProductCodeKeyword -> {
                setState {
                    copy(productCodeKeyword = event.keyword)
                }
            }
        }
    }



    private fun getWarehouseLocations(location: String,warehouseID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWarehouseLocations(
                location,warehouseID
            ).catch {
                setSuspendedState {
                    copy(error = it.message?:"")
                }
            }.collect {
                when(it){
                    is BaseResult.Error -> {
                        setSuspendedState {
                            copy(error = it.message)
                        }
                    }
                    is BaseResult.Success -> {
                        setSuspendedState {
                            copy(
                                locationList = it.data?.rows?: emptyList()
                            )
                        }
                    }
                    BaseResult.UnAuthorized -> {}
                }
            }
        }
    }

    private fun getProductStatuses(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProductStatuses()
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"")
                    }
                }
                .collect {
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(productStatusList = it.data?.rows?: emptyList())
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }
    private fun getTransferList(
        loading:Loading = Loading.LOADING
    ) {

        if (state.loadingState== Loading.NONE){
            setState {
                copy(loadingState = loading)
            }
            viewModelScope.launch {
                repository.getTransfers(
                    productCode = state.productCodeKeyword.text,
                    locationCode = state.locationKeyword.text,
                    barcode = state.barcodeKeyword.text,
                    warehouseID = prefs.getWarehouse()!!.id,
                    page =state.page,
                    sort =state.sort.sort,
                    order = state.sort.order.value
                )
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
                            is BaseResult.Error ->  {
                                setSuspendedState {
                                    copy(error = it.message)
                                }
                            }
                            is BaseResult.Success -> {
                                setSuspendedState {
                                    copy(
                                        transferList = transferList + (it.data?.rows?: emptyList()),
                                        rowCount = it.data?.total ?: 0
                                    )
                                }
                            }
                            BaseResult.UnAuthorized -> {

                            }
                        }
                    }
            }
        }

    }


    private fun transfer(transfer: TransferRow) {
        if (state.quantity.text.isEmpty()){
            setState {
                copy(error = "Quantity can't be empty")
            }
            return
        }
        val quantity = state.quantity.text.toDoubleOrNull()?:0.0
        if (quantity<=0.0){
            setState {
                copy(error = "Quantity can't be zero or less then zero")
            }
        }
        if (state.selectedLocation==null){
            setState {
                copy(error = "Destination location is not selected")
            }
            return
        }
        if (state.selectedProductStatus==null){
            setState {
                copy(error = "Product status is not selected")
            }
            return
        }
        if (!state.isSaving){
            setState {
                copy(isSaving = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.transferLocation(
                    state.selectedProductStatus!!.quiddityTypeId,
                    state.selectedLocation!!.locationId,
                    state.selectedLocation!!.locationCode,
                    transfer.locationInventoryID,
                    state.expirationDate.text,
                    quantity,
                    transfer.warehouseID
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isSaving = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(
                            isSaving = false
                        )
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
                                    copy(
                                        selectedTransfer = null,
                                        transferList = emptyList(),
                                        page = 1,
                                    )
                                }
                                getTransferList()
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
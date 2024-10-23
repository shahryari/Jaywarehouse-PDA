package com.example.jaywarehouse.presentation.picking.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.picking.PickingRepository
import com.example.jaywarehouse.data.picking.models.CustomerToPickRow
import com.example.jaywarehouse.data.picking.models.ReadyToPickRow
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanRemoveModel
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.picking.contracts.PickingDetailContract
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PickingDetailViewModel(
    private val repository: PickingRepository,
    private val prefs: Prefs,
    private val row: ReadyToPickRow,
    private val customer: CustomerToPickRow,
    private val fillLocation: Boolean = false,
) : BaseViewModel<PickingDetailContract.Event, PickingDetailContract.State, PickingDetailContract.Effect>() {
    init {
        setState {
            copy(pickingRow = row,customer = this@PickingDetailViewModel.customer)
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
        getPickedItems(customer.customerID,row.barcode,row.locationCode)
    }

    override fun setInitState(): PickingDetailContract.State {
        return PickingDetailContract.State()
    }

    override fun onEvent(event: PickingDetailContract.Event) {
        when(event){
            PickingDetailContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            is PickingDetailContract.Event.OnBarcodeChanged -> {
                setState {
                    copy(barcode = event.barcode)
                }
                if ((event.barcode.text.endsWith('\n') || (event.barcode.text.endsWith('\r'))) && state.pickingRow!=null){
                    if (state.loadingState == Loading.NONE)scanBarcode(event.barcode.text)
                }
            }
            PickingDetailContract.Event.OnCheckLocation -> {
                setState {
                    copy(isScanning = true)
                }
                if (state.pickingRow?.locationCode == state.location.text){
                    setState {
                        copy(isScanning = false, enableLocation = false)
                    }
                } else {
                    setState {
                        copy(isScanning = false, error = "Invalid Location")
                    }
                }
            }
            is PickingDetailContract.Event.OnLocationChanged -> {
                setState {
                    copy(location = event.location)
                }
                if((event.location.text.endsWith('\n') || event.location.text.endsWith('\r'))&& state.pickingRow!=null){
                    setState {
                        copy(isScanning = true)
                    }
                    if (state.pickingRow?.locationCode == event.location.text){
                        setState {
                            copy(isScanning = false, enableLocation = false)
                        }
                    } else {
                        setState {
                            copy(isScanning = false, error = "Invalid Location")
                        }
                    }
                }
            }
            PickingDetailContract.Event.OnNavBack -> {
                setEffect {
                    PickingDetailContract.Effect.NavigateBack
                }
            }
            is PickingDetailContract.Event.OnRemovePickedItem -> {
                removePicked(event.pickingScanId)
            }
            PickingDetailContract.Event.OnScan -> {
                if (state.loadingState == Loading.NONE)scanBarcode(state.barcode.text)
            }
            PickingDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            is PickingDetailContract.Event.OnSelectPickedItem -> {
                setState {
                    copy(selectedItem = event.pickingScanId)
                }
            }

            PickingDetailContract.Event.OnReachToEnd -> {
                if (10*state.page <= state.pickings.size){
                    setState {
                        copy(page = page + 1, loadingState = Loading.LOADING)
                    }
                    getPickedItems(customer.customerID,row.barcode,row.locationCode,state.page)
                }
            }

            PickingDetailContract.Event.OnRefresh ->{
                setState {
                    copy(page = 1, pickings = emptyList(), loadingState = Loading.REFRESHING)
                }
            }

            PickingDetailContract.Event.HideFinish -> {
                setState {
                    copy(showFinishDialog = false)
                }
                if (state.navigateToParent){
                    setEffect {
                        PickingDetailContract.Effect.NavigateToParent
                    }
                } else {
                    setEffect {
                        PickingDetailContract.Effect.NavigateBack
                    }
                }
            }

            is PickingDetailContract.Event.OnShowDetailChange -> {
                setState {
                    copy(showDetail = event.show)
                }
            }
        }
    }

    private fun getPickedItems(customerId: Int,barcode: String,locationCode: String, page: Int = 1){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPickedScanItems("",customerId,barcode,locationCode,page,10,"CreatedOn", Order.Asc.value)
                .catch {
                    setState {
                        copy(error = it.message.toString(), loadingState = Loading.NONE)
                    }
                }
                .collect {
                    setSuspendedState { copy(loadingState = Loading.NONE) }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            val enb = it.data?.rows?.isEmpty()==true && !fillLocation
                            setSuspendedState {
                                copy(
                                    pickedScanList = it.data,
                                    pickings = pickings + (it.data?.rows?: emptyList()),
                                    pickingRow = it.data?.picking,
                                    enableLocation =  enb,
                                    location = if (!enb) TextFieldValue(row.locationCode) else TextFieldValue(),
                                )
                            }
                        }
                        else -> {}
                    }
                }
        }
    }
    private fun scanBarcode(barcode: String) {
        if (state.pickingRow?.barcode == barcode) {
            viewModelScope.launch(Dispatchers.IO) {
                setSuspendedState {
                    copy(isScanning = true, loadingState = Loading.LOADING)
                }
                repository.scanPicking(
                    barcode,
                    1,
                    row.locationCode,
                    customer.customerID
                )
                    .catch {
                        setState {
                            copy(error = it.message.toString(), isScanning = false, loadingState = Loading.NONE)
                        }
                    }
                    .collect {
                        setSuspendedState {
                            copy(isScanning = false)
                        }
                        when (it) {
                            is BaseResult.Error -> {

                                val data = if (it.message.isNotEmpty()) {
                                    try {
                                        Gson().fromJson(it.message, ScanModel::class.java).message
                                    } catch (e: Exception) {
                                        it.message
                                    }
                                } else it.data?.message
                                setSuspendedState {
                                    copy(error = data ?: "", loadingState = Loading.NONE)
                                }
                            }

                            is BaseResult.Success -> {
                                if (it.data?.isSucceed == true) {
                                    setSuspendedState {
                                        copy(
                                            toast = "Item Scanned Successfully",
                                            navigateToParent = it.data.isNavigateToParent == true
                                        )
                                    }
                                    if ((state.pickingRow?.scanCount?:0) >= (state.pickingRow?.quantity?:0)-1){
                                        setSuspendedState {
                                            copy(showFinishDialog = true)
                                        }
                                    } else {
                                        setSuspendedState {
                                            copy(
                                                page = 1,
                                                pickings = emptyList(),
                                                barcode = TextFieldValue(),
                                                loadingState = Loading.LOADING,
                                            )
                                        }
                                        getPickedItems(customer.customerID,row.barcode,row.locationCode)
                                    }
                                }
                            }

                            else -> {
                                setSuspendedState {
                                    copy(loadingState = Loading.NONE)
                                }
                            }
                        }
                    }
            }
        } else {
            setState {
                copy(error = "wrong barcode", barcode = TextFieldValue())
            }
        }
    }


    private fun removePicked(pickingScanId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            setSuspendedState {
                copy(loadingState = Loading.LOADING)
            }
            repository.removePickedScan(pickingScanId)
                .catch {
                    setState {
                        copy(error = it.message.toString(), loadingState = Loading.NONE)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(selectedItem = null)
                    }
                    when (it) {
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message, loadingState = Loading.NONE)
                            }
                        }

                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(toast = "Item Removed", page = 1, pickings = emptyList(), loadingState = Loading.LOADING)
                            }
                            getPickedItems(customer.customerID,row.barcode,row.locationCode)
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
}
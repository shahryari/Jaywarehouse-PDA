package com.example.jaywarehouse.presentation.packing.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.packing.PackingRepository
import com.example.jaywarehouse.data.packing.model.PackingRow
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.packing.contracts.PackingDetailContract
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PackingDetailViewModel(
    private val repository: PackingRepository,
    private val prefs: Prefs,
    private val packing: PackingRow
) : BaseViewModel<PackingDetailContract.Event,PackingDetailContract.State,PackingDetailContract.Effect>(){

    init {
        setState {
            copy(packingRow = packing)
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
        getScanItems(packing.packingID)
    }
    override fun setInitState(): PackingDetailContract.State {
        return PackingDetailContract.State()
    }

    override fun onEvent(event: PackingDetailContract.Event) {
        when(event){
            PackingDetailContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            PackingDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }
            is PackingDetailContract.Event.OnBarcodeChange -> {
                setState {
                    copy(barcode = event.barcode)
                }
                if ((event.barcode.text.endsWith('\n') || (event.barcode.text.endsWith('\r'))) && state.packingRow!=null){
                    if (state.loadingState == Loading.NONE)scanBarcode(state.packingRow!!.packingID,state.packingRow!!.customerID,event.barcode.text)
                }
            }
            PackingDetailContract.Event.OnNavBack -> {
                setEffect {
                    PackingDetailContract.Effect.NavBack
                }
            }
            is PackingDetailContract.Event.OnRemove ->{
                removePackingDetail(event.id)
            }
            PackingDetailContract.Event.ScanBarcode -> {
                if (state.packingRow!=null && state.loadingState == Loading.NONE)scanBarcode(state.packingRow!!.packingID,state.packingRow!!.customerID,state.barcode.text)
            }
            is PackingDetailContract.Event.SelectedItem ->{
                setState {
                    copy(selectedItem = event.id)
                }
            }

            is PackingDetailContract.Event.OnShowSubmit -> {
                setState {
                    copy(showSubmitDialog = event.show)
                }
            }
            PackingDetailContract.Event.OnSubmit -> {
                finishPacking()
            }
            PackingDetailContract.Event.OnSubmitAndNew -> {
                finishPacking(isNew = true)
            }

            PackingDetailContract.Event.OnReachEnd -> {
                if (10*state.page<=state.packingDetails.size) {
                    setState {
                        copy(page = page+1, loadingState = Loading.LOADING)
                    }
                    getScanItems(state.packingRow!!.packingID,state.page)
                }
            }

            PackingDetailContract.Event.OnRefresh -> {
                setState {
                    copy(loadingState = Loading.REFRESHING, page = 1, packingDetails = emptyList())
                }
                getScanItems(state.packingRow!!.packingID)
            }

            is PackingDetailContract.Event.OnShowSubmitAndNew -> {
                setState {
                    copy(showSubmitAndNewDialog = event.show)
                }
            }
        }
    }

    private fun getScanItems(packingId: Int,page : Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPackingDetails(packingId,"",page,10, order = Order.Asc.value, sort = "CreatedOn")
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
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(packedScanItems = it.data, packingRow = it.data?.packing, packingDetails = packingDetails +(it.data?.rows?: emptyList()))
                            }
                        }
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun scanBarcode(packingId: Int, customerId: Int,barcode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            setSuspendedState {
                copy(isScanning = true, loadingState = Loading.LOADING)
            }
            repository.addPackingDetail(packingId, customerId, barcode)
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString(), isScanning = false)
                    }
                }
                .collect {
                    when(it){
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(toast = "Item Added Successfully", packingDetails = emptyList(), page = 1, isScanning = false, barcode = TextFieldValue(), loadingState = Loading.LOADING)
                            }
                            getScanItems(packingId,state.page)
                        }
                        is BaseResult.Error -> {
                            val data = if(it.message.isNotEmpty()){
                                try {
                                    Gson().fromJson(it.message, ScanModel::class.java).message
                                }catch (e:Exception){
                                    it.message
                                }
                            } else it.data?.message
                            setSuspendedState {
                                copy(error = data?:"", isScanning = false, loadingState = Loading.NONE)
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
    }

    private fun removePackingDetail(packingDetailId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            setSuspendedState {
                copy(loadingState = Loading.LOADING)
            }
            repository.removePackingDetail(packingDetailId)
                .catch {
                    setSuspendedState {
                        copy(error = "", selectedItem = null, loadingState = Loading.NONE)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(selectedItem = null)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            val data = if(it.message.isNotEmpty()){
                                try {
                                    Gson().fromJson(it.message, ScanModel::class.java).message
                                }catch (e:Exception){
                                    it.message
                                }
                            } else it.data?.message
                            setSuspendedState {
                                copy(error = data?:"", loadingState = Loading.NONE)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(toast = "Item Successfully Removed", page = 1, packingDetails = emptyList(), loadingState = Loading.LOADING)
                            }
                            getScanItems(state.packingRow?.packingID?:0,state.page)
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

    private fun finishPacking(isNew: Boolean = false){
        viewModelScope.launch(Dispatchers.IO) {
            repository.finishPacking(state.packingRow!!.packingID,isNew)
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString(), showSubmitDialog = false, showSubmitAndNewDialog = false)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(showSubmitAndNewDialog = false, showSubmitDialog = false)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            val data = if(it.message.isNotEmpty()){
                                try {
                                    Gson().fromJson(it.message, ScanModel::class.java).message
                                }catch (e:Exception){
                                    it.message
                                }
                            } else it.data?.message
                            setSuspendedState {
                                copy(error = data?:"")
                            }
                        }
                        is BaseResult.Success -> {
                            if (it.data?.entityID!=null && isNew){
                                setSuspendedState {
                                    copy(
                                        toast = "Packing Successfully Finished",
                                        page = 1,
                                        packingDetails = emptyList(),
                                        loadingState = Loading.LOADING
                                    )
                                }
                                getScanItems(it.data.entityID)
                            } else {
                                setSuspendedState {
                                    copy(
                                        toast = "Packing Successfully Finished",
                                    )
                                }
                                setEffect {
                                    PackingDetailContract.Effect.NavBack
                                }
                            }
                        }
                        else -> {}
                    }
                }
        }
    }
}
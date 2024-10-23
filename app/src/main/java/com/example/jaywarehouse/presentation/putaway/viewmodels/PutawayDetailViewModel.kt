package com.example.jaywarehouse.presentation.putaway.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.putaway.PutawayRepository
import com.example.jaywarehouse.data.putaway.model.PutRemoveModel
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.example.jaywarehouse.data.putaway.model.ReadyToPutRow
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanModel
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.putaway.contracts.PutawayDetailContract
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PutawayDetailViewModel(
    private val repository: PutawayRepository,
    private val prefs: Prefs,
    putRow: ReadyToPutRow,
    private val fillLocation: Boolean
) : BaseViewModel<PutawayDetailContract.Event,PutawayDetailContract.State,PutawayDetailContract.Effect>(){
    init {
        setState {
            copy(
                putRow = putRow,boxNumber = TextFieldValue(putRow.boxNumber?:""),
                enableBoxNumber = putRow.boxNumber?.isEmpty() ?: true
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
        getPutaways(putRow.receivingDetailID)
    }

    private fun getPutaways(receivingDetailId: Int, page: Int = 1){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPutaways(receivingDetailId,"",page,10,"asc","CreatedOn")
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
                        val enableLocation = it.data?.rows?.isEmpty()==true && !fillLocation
                        val showFinishDialog = it.data?.putRow != null && it.data.putRow.quantity == it.data.putRow.putCount
                        setSuspendedState {
                            copy(
                                details = it.data,
                                putaways = putaways + (it.data?.rows ?: emptyList()),
                                putRow = it.data?.putRow,
                                enableLocation =  enableLocation,
                                location = if (!enableLocation)TextFieldValue(it.data?.putRow?.locationCode?:"") else TextFieldValue(),
                                showFinishAlertDialog = showFinishDialog
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
    override fun setInitState(): PutawayDetailContract.State {
        return PutawayDetailContract.State()
    }

    override fun onEvent(event: PutawayDetailContract.Event) {
        when(event){
            is PutawayDetailContract.Event.OnChangeBarcode -> {
                setState {
                    copy(barcode = event.barcode)
                }
                if ((event.barcode.text.endsWith('\n') || (event.barcode.text.endsWith('\r'))) && state.putRow!=null){
                    if (state.loadingState == Loading.NONE)scanBarcode(event.barcode.text,state.boxNumber.text)
                }
            }
            is PutawayDetailContract.Event.OnChangeLocation -> {
                setState {
                    copy(location = event.location)
                }
                if((event.location.text.endsWith('\n') || event.location.text.endsWith('\r'))&& state.putRow!=null){
                    setState {
                        copy(isScanning = true)
                    }
                    if (state.putRow?.locationCode == event.location.text){
                        setState {
                            copy(isScanning = false, enableLocation = false)
                        }
                        setEffect {
                            PutawayDetailContract.Effect.MoveFocus
                        }
                    } else {
                        setState {
                            copy(isScanning = false, error = "Invalid Location")
                        }
                    }
                }
            }
            PutawayDetailContract.Event.OnNavBack -> {
                setEffect {
                    PutawayDetailContract.Effect.NavBack
                }
            }

            is PutawayDetailContract.Event.OnRemovePut -> {
                viewModelScope.launch(Dispatchers.IO) {
                    setSuspendedState {
                        copy(loadingState = Loading.LOADING)
                    }
                    repository.putRemove(event.putawayScanId)
                        .catch {
                            setSuspendedState {
                                copy(error = it.message?:"", selectedPutaway = null, loadingState = Loading.NONE)
                            }
                        }
                        .collect{
                            when(it){
                                is BaseResult.Error -> {
                                    val data = if (it.message.isNotEmpty()) {
                                        try {
                                            Gson().fromJson(it.message, PutRemoveModel::class.java).message
                                        }catch (e:Exception){
                                            it.message
                                        }
                                    } else it.data?.message
                                    setSuspendedState {
                                        copy(error = data?:"", selectedPutaway = null, loadingState = Loading.NONE)
                                    }
                                }
                                is BaseResult.Success -> {
                                    if (it.data?.isSucceed == true && state.putRow!=null){
                                        setSuspendedState {
                                            copy(toast = "Item removed successfully.", selectedPutaway = null, page = 1, putaways = emptyList(), loadingState = Loading.LOADING)
                                        }
                                        getPutaways(state.putRow!!.receivingDetailID,state.page)
                                    } else {
                                        setSuspendedState {
                                            copy(error = it.data?.message?:"", selectedPutaway = null)
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
            }

            PutawayDetailContract.Event.CheckLocation -> {
                setState {
                    copy(isScanning = true)
                }
                if (state.putRow?.locationCode == state.location.text){
                    setState {
                        copy(isScanning = false, enableLocation = false)
                    }
                    setEffect {
                        PutawayDetailContract.Effect.MoveFocus
                    }
                } else {
                    setState {
                        copy(isScanning = false, error = "Invalid Location")
                    }
                }
            }
            PutawayDetailContract.Event.ScanBarcode -> {
                if (state.loadingState == Loading.NONE)scanBarcode(state.barcode.text,state.boxNumber.text)
            }

            PutawayDetailContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            is PutawayDetailContract.Event.OnSelectPut -> {
                setState {
                    copy(selectedPutaway = event.putawayScanId)
                }
            }

            PutawayDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            PutawayDetailContract.Event.HideFinishDialog -> {
                setState {
                    copy(showFinishAlertDialog = false)
                }
                setEffect {
                    PutawayDetailContract.Effect.NavBack
                }
            }

            PutawayDetailContract.Event.OnReachEnd -> {
                if (10*state.page<=state.putaways.size){
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getPutaways(state.putRow!!.receivingDetailID,state.page)
                }
            }

            PutawayDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, putaways = emptyList(), loadingState = Loading.REFRESHING)
                }
                getPutaways(state.putRow!!.receivingDetailID)
            }

            is PutawayDetailContract.Event.OnChangeBoxNumber -> {
                setState {
                    copy(boxNumber = event.boxNumber)
                }
            }

            PutawayDetailContract.Event.CheckBoxNumber -> {
                setState {
                    copy(enableBoxNumber = !state.enableBoxNumber)
                }
            }

            is PutawayDetailContract.Event.OnShowHeaderDetail -> {
                setState {
                    copy(showHeaderDetail = event.show)
                }
            }
        }
    }

    private fun scanBarcode(barcode: String, boxNumber: String){
        viewModelScope.launch(Dispatchers.IO) {
            setSuspendedState {
                copy(isScanning = true, loadingState = Loading.LOADING)
            }
            repository
                .put(state.putRow!!.receivingDetailID,state.location.text,barcode,boxNumber,1)
                .catch {
                    setSuspendedState {
                        copy(isScanning = false,error = it.message?:"", loadingState = Loading.NONE)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(isScanning = false)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            val data = if(it.message.isNotEmpty()){
                                try {
                                    Gson().fromJson(it.message, ScanModel::class.java).message
                                }catch (e:Exception){
                                    it.message
                                }
                            } else it.data?.message?:""
                            setSuspendedState {
                                copy(error = data, loadingState = Loading.NONE)
                            }
                        }
                        is BaseResult.Success -> {
                            if (it.data?.isSucceed == true) {
                                setSuspendedState {
                                    copy(barcode = TextFieldValue(), toast = "Scan successfully completed.", page = 1, putaways = emptyList(), loadingState = Loading.LOADING)
                                }
                                getPutaways(state.putRow!!.receivingDetailID,state.page)
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.message?:"")
                                }
                            }
                            if (it.data?.isNavigateToParent == true && prefs.getIsNavToParent()){
                                setEffect {
                                    PutawayDetailContract.Effect.NavToDashboard
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
    }
}
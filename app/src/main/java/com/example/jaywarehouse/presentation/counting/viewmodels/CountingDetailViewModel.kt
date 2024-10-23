package com.example.jaywarehouse.presentation.counting.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailRow
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanRemoveModel
import com.example.jaywarehouse.data.receiving.model.ReceivingRow
import com.example.jaywarehouse.data.receiving.repository.ReceivingRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.counting.contracts.CountingDetailContract
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CountingDetailViewModel(
    private val repository: ReceivingRepository,
    private val prefs: Prefs,
    private val receivingRow: ReceivingRow
) : BaseViewModel<CountingDetailContract.Event,CountingDetailContract.State,CountingDetailContract.Effect>() {
    init {
        setState {
            copy(countingRow = receivingRow,sort = prefs.getCountingDetailSort(),order = prefs.getCountingDetailOrder())
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
        getReceivingDetailList(receivingRow.receivingID,"",)
    }
    override fun setInitState(): CountingDetailContract.State {
        return CountingDetailContract.State()
    }

    override fun onEvent(event: CountingDetailContract.Event) {
        when(event){
            is CountingDetailContract.Event.OnChangeBarcode -> {
                setState {
                    copy(
                        barcode = event.barcode
                    )
                }
                if (event.barcode.text.endsWith('\n') || event.barcode.text.endsWith('\r')){
                    val countingDetail = state.countingDetailRow.find { it.barcode == event.barcode.text}

                    if (countingDetail!=null){
                        if (countingDetail.scanCount >= countingDetail.quantity){
                            setState {
                                copy(showConfirm = true)
                            }
                            return
                        }
                    }
                    if (state.loadingState == Loading.NONE)scanBarcode(event.barcode.text)
                }
            }

            is CountingDetailContract.Event.RemoveScanBarcode -> {
                viewModelScope.launch(Dispatchers.IO) {
                    setSuspendedState {
                        copy(loadingState = Loading.LOADING)
                    }
                    repository.removeReceivingDetailScan(receivingRow.receivingID,event.barcode)
                        .catch {
                            setSuspendedState {
                                copy(error = it.message?:"", selectedDetail = null, loadingState = Loading.NONE)
                            }
                        }
                        .collect {
                            when(it){
                                is BaseResult.Error -> {
                                    setSuspendedState {
                                        val data = if(it.message.isNotEmpty()){
                                            try {
                                                Gson().fromJson(it.message,ReceivingDetailScanRemoveModel::class.java).messages.firstOrNull()?:""
                                            }catch (e:Exception){
                                                it.message
                                            }
                                        } else it.data?.messages?.firstOrNull()?:""
                                        copy(error = data, selectedDetail = null, loadingState = Loading.NONE)
                                    }
                                }
                                is BaseResult.Success -> {
                                    if (it.data?.isSucceed == true){
                                        setSuspendedState {
                                            copy(toast = "Removed successfully", selectedDetail = null, page = 1, countingDetailRow = emptyList(), loadingState = Loading.LOADING)
                                        }
                                        getReceivingDetailList(state.countingRow!!.receivingID,state.keyword.text,state.page)
                                    } else {
                                        setSuspendedState {
                                            copy(error = it.data?.messages?.firstOrNull()?:"", selectedDetail = null)
                                        }
                                    }
                                }
                                else ->{
                                    setState {
                                        copy(selectedDetail = null, loadingState = Loading.NONE)
                                    }
                                }
                            }
                        }
                }
            }
            CountingDetailContract.Event.ScanBarcode -> {
                val countingDetail = state.countingDetailRow.find { it.barcode == state.barcode.text}

                if (countingDetail!=null){
                    if (countingDetail.scanCount >= countingDetail.quantity){
                        setState {
                            copy(showConfirm = true)
                        }
                        return
                    }
                }
                if (state.loadingState == Loading.NONE)scanBarcode(state.barcode.text)
            }

            CountingDetailContract.Event.OnNavBack -> setEffect {
                CountingDetailContract.Effect.NavBack
            }

            CountingDetailContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }

            CountingDetailContract.Event.OnClearBarcode -> {
                setState {
                    copy(barcode = TextFieldValue(), showClearIcon = false)
                }
            }

            is CountingDetailContract.Event.OnChangeKeyword -> {
                setState {
                    copy(keyword = event.keyword)
                }
            }

            is CountingDetailContract.Event.OnSelectDetail -> {
                setState {
                    copy(selectedDetail = event.barcode)
                }
            }

            CountingDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            is CountingDetailContract.Event.OnSelectOrder -> {
                prefs.setCountingDetailOrder(event.order)
                setState {
                    copy(order = event.order, countingDetailRow = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getReceivingDetailList(receivingRow.receivingID,state.keyword.text,state.page,state.sort,order = event.order)
            }
            is CountingDetailContract.Event.OnSelectSort -> {
                prefs.setCountingDetailSort(event.sort)
                setState {
                    copy(sort = event.sort, countingDetailRow = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getReceivingDetailList(receivingRow.receivingID,state.keyword.text,state.page,event.sort,order = state.order)
            }
            is CountingDetailContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }

            is CountingDetailContract.Event.OnShowConfirm -> {
                setState {
                    copy(showConfirm = event.show)
                }
            }

            CountingDetailContract.Event.OnReachedEnd -> {
                if (10*state.page <= state.countingDetailRow.size){
                    setState {
                        copy(page = page+1, loadingState = Loading.LOADING)
                    }
                    getReceivingDetailList(receivingRow.receivingID,state.keyword.text,state.page,state.sort,state.order)
                }
            }

            CountingDetailContract.Event.ConfirmScanBarcode -> {
                setState {
                    copy(showConfirm = false)
                }
                if (state.loadingState == Loading.NONE)scanBarcode(state.barcode.text)
            }

            CountingDetailContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, countingDetailRow = emptyList(), loadingState = Loading.SEARCHING)
                }
                getReceivingDetailList(receivingRow.receivingID,state.keyword.text,state.page,state.sort,state.order)
            }

            CountingDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, loadingState = Loading.REFRESHING, countingDetailRow = emptyList())
                }
                getReceivingDetailList(receivingRow.receivingID,state.keyword.text,state.page,state.sort,state.order)
            }
        }
    }

    private fun scanBarcode(barcode: String){

        if (barcode.isNotEmpty()) if (state.countingRow!=null)
            viewModelScope.launch(Dispatchers.IO) {
                setSuspendedState {
                    copy(isScanLoading = true, loadingState = Loading.LOADING)
                }
                repository.scanReceivingDetail(
                    state.countingRow!!.receivingID,
                    barcode,
                    1
                )
                    .catch {
                        setSuspendedState {
                            copy(isScanLoading = false, error = it.message?:"", loadingState = Loading.NONE)
                        }
                    }
                    .collect {
                    setSuspendedState {
                        copy(isScanLoading = false)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                val data = if (it.message.isNotEmpty()) {
                                    try {
                                        Gson().fromJson(it.message,ReceivingDetailScanModel::class.java).message
                                    }catch (e:Exception){
                                        it.message
                                    }
                                } else it.data?.message
                                copy(error = data?:"", showClearIcon = true, loadingState = Loading.NONE)
                            }
                        }
                        is BaseResult.Success ->{
                            if (it.data?.isSucceed == true && state.countingRow!=null) {
                                setSuspendedState {
                                    copy(barcode = TextFieldValue(), toast = "Scan completed successfully", page = 1, countingDetailRow = emptyList(), loadingState = Loading.LOADING)
                                }
                                getReceivingDetailList(state.countingRow!!.receivingID,state.keyword.text,state.page,state.sort,state.order)
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.message?:"", showClearIcon = true)
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


    private fun getReceivingDetailList(receivingID: Int,keyword: String,page: Int = 1,sort: String = "CreatedOn",order: String = Order.Desc.value) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getReceivingDetails(receivingID,keyword,page,10,sort,order)
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
                                copy(countingDetailModel = it.data, countingRow = it.data?.receiving, countingDetailRow = it.data?.rows?: emptyList())
                            }
                        }
                        else ->{}
                    }
                }
        }
    }
}
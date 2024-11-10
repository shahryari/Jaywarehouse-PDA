package com.example.jaywarehouse.presentation.putaway.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.putaway.PutawayRepository
import com.example.jaywarehouse.data.putaway.model.PutawayListGroupedRow
import com.example.jaywarehouse.data.putaway.model.PutawayListRow
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.putaway.contracts.PutawayDetailContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PutawayDetailViewModel(
    private val repository: PutawayRepository,
    private val prefs: Prefs,
    private val putRow: PutawayListGroupedRow,
) : BaseViewModel<PutawayDetailContract.Event,PutawayDetailContract.State,PutawayDetailContract.Effect>(){
    init {
        val selectedSort = state.sortList.find {
            it.sort == prefs.getPutawayDetailSort() && it.order == Order.getFromValue(prefs.getPutawayDetailOrder())
        }
        if (selectedSort!=null) {
            setState {
                copy(sort = selectedSort)
            }
        }
        setState {
            copy(
                putRow = putRow,
//                boxNumber = TextFieldValue(putRow.boxNumber?:""),
//                enableBoxNumber = putRow.boxNumber?.isEmpty() ?: true
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
        getPutaways(putRow.referenceNumber,sort = state.sort)
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
            }
            PutawayDetailContract.Event.OnNavBack -> {
                setEffect {
                    PutawayDetailContract.Effect.NavBack
                }
            }
            PutawayDetailContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            is PutawayDetailContract.Event.OnSelectPut -> {
                setState {
                    copy(selectedPutaway = event.put)
                }
            }

            PutawayDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            PutawayDetailContract.Event.OnReachEnd -> {
                if (10*state.page<=state.putaways.size){
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getPutaways(putRow.referenceNumber,keyword = state.keyword.text,page = state.page,sort = state.sort)
                }
            }

            PutawayDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, putaways = emptyList(), loadingState = Loading.REFRESHING)
                }
                getPutaways(putRow.referenceNumber,keyword = state.keyword.text,page = state.page,sort = state.sort)
            }
            is PutawayDetailContract.Event.OnChangeLocation -> {
                setState {
                    copy(location = event.location)
                }
            }
            is PutawayDetailContract.Event.OnSavePutaway -> {
                finishPutaway(event.putaway,state.location.text.trim(), state.barcode.text.trim())
            }
            is PutawayDetailContract.Event.OnChangeKeyword -> {
                setState {
                    copy(keyword = event.keyword)
                }
            }
            PutawayDetailContract.Event.OnSearch -> {
                setState {
                    copy(loadingState = Loading.SEARCHING, putaways = emptyList(), page = 1)
                }
                getPutaways(putRow.referenceNumber,keyword = state.keyword.text,page = state.page,sort = state.sort)
            }
            is PutawayDetailContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }
            is PutawayDetailContract.Event.OnSortChange -> {
                prefs.setPutawayDetailSort(event.sortItem.sort)
                prefs.setPutawayDetailOrder(event.sortItem.order.value)
                setState {
                    copy(sort = event.sortItem, page = 1, putaways = emptyList(), loadingState = Loading.LOADING)
                }
                getPutaways(putRow.referenceNumber,keyword = state.keyword.text,page = state.page,sort = event.sortItem)
            }
        }
    }


    private fun finishPutaway(
        selectedPutaway: PutawayListRow,
        locationCode: String,
        barcode: String
    ) {


        if (selectedPutaway.warehouseLocationCode != locationCode){
            setState {
                copy(toast = "Please select correct location")
            }
            return
        }

        if (selectedPutaway.productBarcodeNumber != barcode){
            setState {
                copy(toast = "Please select correct barcode")
            }
            return
        }
        setState {
            copy(onSaving = true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (state.selectedPutaway!=null)
            repository.finishPutaway(
                selectedPutaway.receiptDetailID.toString(),
                selectedPutaway.productLocationActivityID.toString(),
                selectedPutaway.receivingDetailID.toString())
                .catch {
                    setState {
                        copy(
                            error = it.message ?: "",
                            onSaving = false
                        )
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(onSaving = false)
                    }
                    when(it){
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(
                                    location = TextFieldValue(),
                                    barcode = TextFieldValue(),
                                    putaways = emptyList(),
                                    page = 1,
                                    selectedPutaway = null,
                                    toast = it.data?.messages?.first() ?: "",
                                    loadingState = Loading.LOADING
                                )
                            }
                            getPutaways(putRow.referenceNumber,keyword = state.keyword.text,page = state.page,sort = state.sort)
                        }
                        is BaseResult.Error -> {
                            setState {
                                copy(
                                    error = it.message,
                                )
                            }
                        }
                        else -> {}
                    }
                }
        }
    }


    private fun getPutaways(referenceNumber: String,keyword: String = "",page: Int = 1, sort: SortItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPutawayList(
                referenceNumber = referenceNumber,
                keyword = keyword,
                sort = sort.sort,
                page = page,
                order = sort.order.value
            )
                .catch {
                    setState {
                        copy(
                            error = it.message ?: "",
                            loadingState = Loading.NONE
                        )
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(loadingState = Loading.NONE)
                    }
                    when(it){
                        is BaseResult.Success -> {
                            setState {
                                copy(
                                    putaways = putaways + (it.data?.rows ?: emptyList()),
                                )
                            }
                        }
                        is BaseResult.Error -> {
                            setState {
                                copy(
                                    error = it.message,
                                )
                            }
                        }
                        else -> {}
                    }
                }
        }
    }
}
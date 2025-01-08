package com.example.jaywarehouse.presentation.picking.viewModels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.picking.PickingRepository
import com.example.jaywarehouse.data.picking.models.PickingListGroupedRow
import com.example.jaywarehouse.data.picking.models.PickingListRow
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.picking.contracts.PickingDetailContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PickingDetailViewModel(
    private val repository: PickingRepository,
    private val prefs: Prefs,
    private val row: PickingListGroupedRow,
) : BaseViewModel<PickingDetailContract.Event,PickingDetailContract.State,PickingDetailContract.Effect>(){
    init {
        val selectedSort = state.sortList.find {
            it.sort == prefs.getPickingSort() && it.order == Order.getFromValue(prefs.getPickingOrder())
        }
        if (selectedSort!=null) {
            setState {
                copy(sort = selectedSort)
            }
        }
        setState {
            copy(
                pickRow = row,
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
        getPickings(row.customerID,sort = state.sort)
    }

    override fun setInitState(): PickingDetailContract.State {
        return PickingDetailContract.State()
    }

    override fun onEvent(event: PickingDetailContract.Event) {
        when(event){
            is PickingDetailContract.Event.OnChangeBarcode -> {
                setState {
                    copy(barcode = event.barcode)
                }
            }
            PickingDetailContract.Event.OnNavBack -> {
                setEffect {
                    PickingDetailContract.Effect.NavBack
                }
            }
            PickingDetailContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            is PickingDetailContract.Event.OnSelectPick -> {
                setState {
                    copy(selectedPick = event.put)
                }
            }

            PickingDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            PickingDetailContract.Event.OnReachEnd -> {
                if (10*state.page<=state.pickingList.size){
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getPickings(row.customerID,keyword = state.keyword,page = state.page,sort = state.sort)
                }
            }

            PickingDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, pickingList = emptyList(), loadingState = Loading.REFRESHING)
                }
                getPickings(row.customerID,keyword = state.keyword,page = state.page,sort = state.sort)
            }
            is PickingDetailContract.Event.OnChangeLocation -> {
                setState {
                    copy(location = event.location)
                }
            }
            is PickingDetailContract.Event.OnCompletePick -> {
                completePicking(event.pick,state.location.text.trim(), state.barcode.text.trim())
            }
//            is PickingDetailContract.Event.OnChangeKeyword -> {
//                setState {
//                    copy(keyword = event.keyword)
//                }
//            }
            is PickingDetailContract.Event.OnSearch -> {
                setState {
                    copy(loadingState = Loading.SEARCHING, pickingList = emptyList(), page = 1, keyword = event.keyword)
                }
                getPickings(row.customerID,keyword = state.keyword,page = state.page,sort = state.sort)
            }
            is PickingDetailContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }
            is PickingDetailContract.Event.OnSortChange -> {
                prefs.setPickingSort(event.sortItem.sort)
                prefs.setPickingOrder(event.sortItem.order.value)
                setState {
                    copy(sort = event.sortItem, page = 1, pickingList = emptyList(), loadingState = Loading.LOADING)
                }
                getPickings(row.customerID,keyword = state.keyword,page = state.page,sort = event.sortItem)
            }
        }
    }


    private fun completePicking(
        pick: PickingListRow,
        locationCode: String,
        barcode: String
    ) {


        if (locationCode.isEmpty()){
            setState {
                copy(toast = "Please fill location")
            }
            return
        }

        if (barcode.isEmpty()){
            setState {
                copy(toast = "Please fill barcode")
            }
            return
        }
        setState {
            copy(onSaving = true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (state.selectedPick!=null)
            repository.completePicking(
                locationCode,
                barcode,
                pick.productLocationActivityID.toString())
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
                                    pickingList = emptyList(),
                                    page = 1,
                                    selectedPick = null,
                                    toast = it.data?.messages?.first() ?: "",
                                    loadingState = Loading.LOADING
                                )
                            }
                            getPickings(row.customerID,keyword = state.keyword,page = state.page,sort = state.sort)
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


    private fun getPickings(customerId: Int, keyword: String = "", page: Int = 1, sort: SortItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPickingList(
                customerId = customerId.toString(),
                keyword = keyword,
                sort = sort.sort,
                rows = 10,
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
                                    pickingList = pickingList + (it.data?.rows ?: emptyList()),
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
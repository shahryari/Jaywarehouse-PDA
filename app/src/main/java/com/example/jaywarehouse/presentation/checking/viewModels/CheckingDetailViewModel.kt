package com.example.jaywarehouse.presentation.checking.viewModels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.checking.CheckingRepository
import com.example.jaywarehouse.data.checking.models.CheckingListGroupedRow
import com.example.jaywarehouse.data.checking.models.CheckingListRow
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.presentation.checking.contracts.CheckingDetailContract
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CheckingDetailViewModel(
    private val repository: CheckingRepository,
    private val prefs: Prefs,
    private val row: CheckingListGroupedRow,
) : BaseViewModel<CheckingDetailContract.Event,CheckingDetailContract.State,CheckingDetailContract.Effect>(){
    init {
        val selectedSort = state.sortList.find {
            it.sort == prefs.getCheckingDetailSort() && it.order == Order.getFromValue(prefs.getCheckingDetailOrder())
        }
        if (selectedSort!=null) {
            setState {
                copy(sort = selectedSort)
            }
        }
        setState {
            copy(
                checkRow = row,
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
        getCheckings(row.customerID,sort = state.sort)
    }

    override fun setInitState(): CheckingDetailContract.State {
        return CheckingDetailContract.State()
    }

    override fun onEvent(event: CheckingDetailContract.Event) {
        when(event){
            is CheckingDetailContract.Event.OnChangeBarcode -> {
                setState {
                    copy(barcode = event.barcode)
                }
            }
            CheckingDetailContract.Event.OnNavBack -> {
                setEffect {
                    CheckingDetailContract.Effect.NavBack
                }
            }
            CheckingDetailContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            is CheckingDetailContract.Event.OnSelectCheck -> {
                setState {
                    copy(selectedChecking = event.checking)
                }
            }

            CheckingDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            CheckingDetailContract.Event.OnReachEnd -> {
                if (10*state.page<=state.checkingList.size){
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getCheckings(row.customerID,keyword = state.keyword,page = state.page,sort = state.sort)
                }
            }

            CheckingDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, checkingList = emptyList(), loadingState = Loading.REFRESHING)
                }
                getCheckings(row.customerID,keyword = state.keyword,page = state.page,sort = state.sort)
            }
            is CheckingDetailContract.Event.OnChangeLocation -> {
                setState {
                    copy(count = event.location)
                }
            }
            is CheckingDetailContract.Event.OnCompleteChecking -> {
                completeChecking(event.checking,state.count.text.trim().toInt(), state.barcode.text.trim())
            }
//            is CheckingDetailContract.Event.OnChangeKeyword -> {
//                setState {
//                    copy(keyword = event.keyword)
//                }
//            }
            is CheckingDetailContract.Event.OnSearch -> {
                setState {
                    copy(loadingState = Loading.SEARCHING, checkingList = emptyList(), page = 1, keyword = event.keyword)
                }
                getCheckings(row.customerID,keyword = state.keyword,page = state.page,sort = state.sort)
            }
            is CheckingDetailContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }
            is CheckingDetailContract.Event.OnSortChange -> {
                prefs.setCheckingDetailSort(event.sortItem.sort)
                prefs.setCheckingDetailOrder(event.sortItem.order.value)
                setState {
                    copy(sort = event.sortItem, page = 1, checkingList = emptyList(), loadingState = Loading.LOADING)
                }
                getCheckings(row.customerID,keyword = state.keyword,page = state.page,sort = event.sortItem)
            }
        }
    }


    private fun completeChecking(
        checking: CheckingListRow,
        count: Int,
        barcode: String
    ) {

        setState {
            copy(onSaving = true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (state.selectedChecking!=null)
            repository.checking(
                checking.isCrossDock,
                count,
                checking.customerID.toString(),
                checking.checkingWorkerTaskID.toString(),
                barcode)
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
                                    count = TextFieldValue(),
                                    barcode = TextFieldValue(),
                                    checkingList = emptyList(),
                                    page = 1,
                                    selectedChecking = null,
                                    toast = it.data?.messages?.first() ?: "",
                                    loadingState = Loading.LOADING
                                )
                            }
                            getCheckings(row.customerID,keyword = state.keyword,page = state.page,sort = state.sort)
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


    private fun getCheckings(customerId: Int, keyword: String = "", page: Int = 1, sort: SortItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCheckingList(
                customerId = customerId.toString(),
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
                                    checkingList = checkingList + (it.data?.rows ?: emptyList()),
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
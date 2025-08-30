package com.linari.presentation.return_receiving.viewmodels

import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.return_receiving.ReturnRepository
import com.linari.data.return_receiving.models.ReturnRow
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.return_receiving.contracts.ReturnDetailContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ReturnDetailViewModel(
    private val master: ReturnRow,
    private val prefs: Prefs,
    private val repository: ReturnRepository
) : BaseViewModel<ReturnDetailContract.Event, ReturnDetailContract.State, ReturnDetailContract.Effect>(){
    init {
        val selectedSort = state.sortList.find {
            it.sort == prefs.getReturnDetailSort() && it.order.value == prefs.getReturnDetailOrder()
        }
        if (selectedSort!=null){
            setState {
                copy(sortItem = selectedSort)
            }
        }
        setState {
            copy(warehouse = prefs.getWarehouse(), master = this@ReturnDetailViewModel.master )
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }
    override fun setInitState(): ReturnDetailContract.State {
        return ReturnDetailContract.State()
    }

    override fun onEvent(event: ReturnDetailContract.Event) {
        when(event) {
            ReturnDetailContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            ReturnDetailContract.Event.CloseToast -> {
                setState { copy(toast = "") }
            }
            ReturnDetailContract.Event.FetchData -> {
                getList()
            }
            ReturnDetailContract.Event.OnAdd -> {
                add()
            }
            ReturnDetailContract.Event.OnConfirmDelete -> {
                delete()
            }
            ReturnDetailContract.Event.OnNavBack -> {
                setEffect {
                    ReturnDetailContract.Effect.NavBack
                }
            }
            ReturnDetailContract.Event.OnReachEnd -> {
                if (state.page* ROW_COUNT<=state.list.size) {
                    getList(loadNext = true)
                }
            }
            ReturnDetailContract.Event.OnRefresh -> {
                getList(Loading.REFRESHING)
            }
            is ReturnDetailContract.Event.OnSearch -> {
                setState { copy(keyword = event.keyword) }
                getList(Loading.SEARCHING)
            }
            is ReturnDetailContract.Event.OnSelectForDelete -> {
                setState { copy(selectedForDelete = event.returnDetailRow) }
            }
            is ReturnDetailContract.Event.ShowAdd -> {
                setState { copy(showAdd = event.show) }
                if (event.show){
                    getStatusList()
                }
            }
            is ReturnDetailContract.Event.ShowSortList -> {
                setState { copy(showSortList = event.show) }
            }
            is ReturnDetailContract.Event.OnSortChange -> {
                prefs.setReturnDetailSort(event.sortItem.sort)
                prefs.setReturnDetailSort(event.sortItem.order.value)
                setState { copy(sortItem = event.sortItem) }
                getList()
            }

            is ReturnDetailContract.Event.ChangeBarcode -> {
                setState {
                    copy(barcode = event.barcode)
                }
            }
            is ReturnDetailContract.Event.ChangeQuantity -> {
                setState {
                    copy(quantity = event.quantity)
                }
            }
            is ReturnDetailContract.Event.OnSelectStatus -> {
                setState {
                    copy(productStatus = event.status)
                }
            }
            is ReturnDetailContract.Event.OnShowStatusList -> {
                setState {
                    copy(showProductStatusList = event.show)
                }
            }
        }
    }

    private fun getList(
        loading: Loading = Loading.LOADING,
        loadNext: Boolean = false,
    ) {
        if (state.loadingState == Loading.NONE) {
            setState {
                copy(loadingState = loading)
            }
            if (loadNext) {
                setState {
                    copy(page = page + 1)
                }
            } else {
                setState {
                    copy(page = 1, list = emptyList())
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.getReceivingDetails(
                state.keyword,
                master.receivingID,
                sort = state.sortItem?.sort?:"CreatedOn",
                order = state.sortItem?.order?.value?:"desc",
                page = state.page,
                rows=ROW_COUNT
            ).catch {
                setSuspendedState {
                    copy(error = it.message?:"", loadingState = Loading.NONE)
                }
            }.collect {
                setSuspendedState {
                    copy(loadingState = Loading.NONE)
                }
                when(it){
                    is BaseResult.Error<*> -> {
                        setSuspendedState {
                            copy(error = it.message)
                        }
                    }
                    is BaseResult.Success -> {
                        setSuspendedState {
                            copy(
                                list = list + (it.data?.rows?:emptyList()),
                                rowCount = it.data?.total?:0
                            )
                        }
                    }
                    BaseResult.UnAuthorized -> {}
                }
            }
        }
    }
    private fun delete() {
        if (state.selectedForDelete!=null){
            if (!state.isDeleting){
                setState {
                    copy(isDeleting = true)
                }
                viewModelScope.launch(Dispatchers.IO) {
                    repository.removeReturnReceivingDetail(
                        state.selectedForDelete!!.receivingWorkerTaskID
                    ).catch {
                        setSuspendedState {
                            copy(error = it.message?:"", isDeleting = false, selectedForDelete = null)
                        }
                    }.collect {
                        setSuspendedState {
                            copy(isDeleting = false, selectedForDelete = null)
                        }
                        when(it){
                            is BaseResult.Error -> {
                                setSuspendedState {
                                    copy(error = it.message)
                                }
                            }
                            is BaseResult.Success -> {
                                if (it.data?.isSucceed == true) {
                                    setSuspendedState {
                                        copy(
                                            toast = it.data.message?:it.data.messages?.firstOrNull()?:"Removed Successfully"
                                        )
                                    }
                                    getList()
                                } else {
                                    setSuspendedState {
                                        copy(error = it.data?.message?:it.data?.messages?.firstOrNull()?:"")
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

    private fun add() {
        val quantity = state.quantity.text.toDoubleOrNull()
        if (quantity == null) {
            setState {
                copy(error = "Wrong Quantity value")
            }
            return
        }
        if (quantity<=0) {
            setState {
                copy(error = "Quantity must be greater then zero")
            }
            return
        }
        if (state.productStatus==null){
            setState {
                copy(error = "Product Status not selected")
            }
            return
        }
        if (!state.isSaving) {
            setState {
                copy(isSaving = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.saveReturnReceivingDetail(
                    master.receivingID,
                    state.warehouse?.id?:0L,
                    quantity = quantity,
                    state.productStatus!!.quiddityTypeId,
                    barcode = state.barcode.text
                )
                    .catch {
                        setSuspendedState {
                            copy(error = it.message?:"", isSaving = false)
                        }
                    }
                    .collect {
                        setSuspendedState {
                            copy(isSaving = false)
                        }
                        when(it) {
                            is BaseResult.Error<*> -> {
                                setSuspendedState {
                                    copy(error = it.message)
                                }
                            }
                            is BaseResult.Success -> {
                                if (it.data?.isSucceed == true) {
                                    setSuspendedState {
                                        copy(
                                            toast = it.data.message?:it.data.messages?.firstOrNull()?:"Saved Successfully",
                                            showAdd = false
                                        )
                                    }
                                    getList()
                                } else {
                                    setSuspendedState {
                                        copy(error = it.data?.message?:it.data?.messages?.firstOrNull()?:"")
                                    }
                                }
                            }
                            BaseResult.UnAuthorized -> {}
                        }
                    }
            }
        }
    }

    private fun getStatusList() {
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
                                copy(productStatusList = it.data?.rows?:emptyList())
                            }
                        }
                        BaseResult.UnAuthorized -> TODO()
                    }
                }
        }
    }
}
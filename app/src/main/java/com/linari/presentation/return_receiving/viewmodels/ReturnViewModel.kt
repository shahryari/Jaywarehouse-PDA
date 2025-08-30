package com.linari.presentation.return_receiving.viewmodels

import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.return_receiving.ReturnRepository
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.getLabelOf
import com.linari.presentation.return_receiving.contracts.ReturnReceivingContract
import com.linari.presentation.return_receiving.contracts.ReturnReceivingContract.Effect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ReturnViewModel(
    private val prefs: Prefs,
    private val repository: ReturnRepository
) : BaseViewModel<ReturnReceivingContract.Event, ReturnReceivingContract.State, ReturnReceivingContract.Effect>(){

    init {
        val selectedSort = state.sortList.find {
            it.sort == prefs.getReturnSort() && it.order.value == prefs.getReturnOrder()
        }
        if (selectedSort!=null){
            setState {
                copy(sortItem = selectedSort)
            }
        }
        setState {
            copy(warehouse = prefs.getWarehouse() )
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }


    override fun setInitState(): ReturnReceivingContract.State {
        return ReturnReceivingContract.State()
    }

    override fun onEvent(event: ReturnReceivingContract.Event) {
        when(event) {
            ReturnReceivingContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            ReturnReceivingContract.Event.CloseToast -> {
                setState {
                    copy(toast = "")
                }
            }
            ReturnReceivingContract.Event.ConfirmDelete -> {
                delete()
            }
            ReturnReceivingContract.Event.FetchData -> {
                getList()
            }
            ReturnReceivingContract.Event.OnAdd -> {
                add()
            }
            ReturnReceivingContract.Event.OnNavBack -> {
                setEffect {
                    NavBack
                }
            }
            is ReturnReceivingContract.Event.OnNavToDetail -> {
                setEffect {
                    NavToDetail(event.model)
                }
            }
            ReturnReceivingContract.Event.OnRefresh -> {
                getList(Loading.REFRESHING)
            }
            is ReturnReceivingContract.Event.OnSearch -> {
                setState {
                    copy(keyword = event.keyword)
                }
                getList(Loading.SEARCHING)
            }
            ReturnReceivingContract.Event.ReachEnd -> {
                if (state.page* ROW_COUNT <= state.list.size) {
                    getList(loadNext = true)
                }
            }
            is ReturnReceivingContract.Event.SelectForDelete -> {
                setState { copy(selectedForDelete = event.returnRow) }
            }
            is ReturnReceivingContract.Event.ShowAdd ->{
                setState { copy(showAdd = event.show) }
                if (event.show){
                    getCustomerList()
                    getOwnerInfoList()
                }
            }
            is ReturnReceivingContract.Event.ShowSortList -> {
                setState { copy(showSortList = event.show) }
            }
            is ReturnReceivingContract.Event.OnSortChange -> {
                prefs.setReturnSort(event.sortItem.sort)
                prefs.setReturnOrder(event.sortItem.order.value)
                setState {
                    copy(sortItem = event.sortItem)
                }
                getList()
            }

            is ReturnReceivingContract.Event.OnShowCustomerList ->  {
                setState {
                    copy(showCustomerList = event.show)
                }
            }
            is ReturnReceivingContract.Event.ChangeReceivingDate -> {
                setState {
                    copy(receivingDate = event.receivingDate, receivingShowDate = TextFieldValue(event.showDate))
                }
            }
            is ReturnReceivingContract.Event.ChangeReferenceNumber -> {
                setState {
                    copy(referenceNumber = event.referenceNumber)
                }
            }
            is ReturnReceivingContract.Event.OnSelectCustomer -> {
                setState {
                    copy(customer = event.customer)
                }
            }
            is ReturnReceivingContract.Event.OnSelectOwner -> {
                setState {
                    copy(ownerInfo = event.owner)
                }
            }
            is ReturnReceivingContract.Event.OnShowOwnerList -> {
                setState {
                    copy(showOwnerList = event.show)
                }
            }
            is ReturnReceivingContract.Event.ShowDatePicker -> {
                setState {
                    copy(showDatePicker = event.show)
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
            repository.getReturnReceivingList(
                state.keyword,
                state.warehouse?.id?:0L,
                state.sortItem?.sort?:"CreatedOn",
                state.sortItem?.order?.value?:"desc",
                state.page,
                ROW_COUNT
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
            if (!state.isDeleting) {
                setState {
                    copy(isDeleting = true)
                }
                viewModelScope.launch(Dispatchers.IO) {
                    repository.removeReturnReceiving(state.selectedForDelete!!.receivingID)
                        .catch {
                            setSuspendedState {
                                copy(error = it.message?:"", isDeleting = false, selectedForDelete = null)
                            }
                        }
                        .collect {
                            setSuspendedState {
                                copy(isDeleting = false, selectedForDelete = null)
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
                                                toast = it.data.message?:it.data.messages?.firstOrNull()?:"Remove Successfully"
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
        if (state.customer==null){
            setState {
                copy(error = "Customer Not Selected")
            }
            return
        }
        if (state.ownerInfo==null) {
            setState {
                copy(error = "Owner Not Selected")
            }
            return
        }
        if (!state.isSaving){
            setState {
                copy(isSaving = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.saveReturnReceiving(
                    state.referenceNumber.text,
                    state.receivingDate,
                    state.warehouse?.id?:0,
                    state.customer!!.partnerID,
                    state.ownerInfo!!.ownerInfoID
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isSaving = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isSaving = false)
                    }
                    when(it){
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

    private fun getCustomerList() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCustomerList()
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"")
                    }
                }
                .collect {
                    when(it){
                        is BaseResult.Error<*> -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(customerList = it.data?.rows?:emptyList())
                            }
                        }
                        is BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }

    private fun getOwnerInfoList() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getOwnerInfoList()
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
                                copy(ownerInfoList = it.data?.rows?:emptyList())
                            }
                            if (it.data?.rows?.size == 1){
                                setSuspendedState {
                                    copy(ownerInfo = it.data.rows.firstOrNull())
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }
}
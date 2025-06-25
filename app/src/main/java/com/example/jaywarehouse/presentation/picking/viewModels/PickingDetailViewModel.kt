package com.example.jaywarehouse.presentation.picking.viewModels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.common.utils.ROW_COUNT
import com.example.jaywarehouse.data.picking.PickingRepository
import com.example.jaywarehouse.data.picking.models.PickingListBDRow
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
                hasModify = prefs.getHasModifyPick(),
                hasWaste = prefs.getHasWaste(),
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
        getPickings()
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
                if (ROW_COUNT*state.page<=state.pickingList.size){
                    setState {
                        copy(page = state.page+1)
                    }
                    getPickings()
                }
            }

            PickingDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, pickingList = emptyList())
                }
                getPickings(Loading.REFRESHING)
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
                    copy(pickingList = emptyList(), page = 1, keyword = event.keyword)
                }
                getPickings(Loading.SEARCHING)
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
                    copy(sort = event.sortItem, page = 1, pickingList = emptyList())
                }
                getPickings()
            }

            is PickingDetailContract.Event.OnModifyPick -> {
                modifyPicking(event.pick)
            }
            is PickingDetailContract.Event.OnShowModify -> {
                setState {
                    copy(showModify = event.pick, quantity = TextFieldValue())
                }
            }
            is PickingDetailContract.Event.OnShowWaste -> {
                setState {
                    copy(showWaste = event.pick, quantity = TextFieldValue())
                }
            }
            is PickingDetailContract.Event.OnWastePick -> {
                wasteOfPicking(event.pick)
            }

            is PickingDetailContract.Event.ChangeQuantity -> {
                setState {
                    copy(quantity = event.quantity)
                }
            }
        }
    }


    private fun completePicking(
        pick: PickingListRow,
        locationCode: String,
        barcode: String
    ) {



        if (pick.warehouseLocationCode!=null){
            if (locationCode.isEmpty() && barcode.isEmpty()){
                setState {
                    copy(error = "Please fill location and barcode")
                }
                return
            }
            if (locationCode.isEmpty()){
                setState {
                    copy(error = "Please fill location")
                }
                return
            }
            if (locationCode.trim().lowercase() != pick.warehouseLocationCode.lowercase()){
                setState {
                    copy(error = "Wrong Location")
                }
                return
            }
        }
        if (barcode.isEmpty()){
            setState {
                copy(error = "Please fill barcode")
            }
            return
        }
        if (pick.barcodeNumber!=null){
            if (barcode.trim() != pick.barcodeNumber){
                setState {
                    copy(error = "Wrong Barcode")
                }
                return
            }
        }
        if (!state.onSaving){
            setState {
                copy(onSaving = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.completePicking(
                    locationCode,
                    barcode,
                    pick.pickingID.toString())
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
                                if (it.data?.isSucceed == true){
                                    setSuspendedState {
                                        copy(
                                            location = TextFieldValue(),
                                            barcode = TextFieldValue(),
                                            pickingList = emptyList(),
                                            page = 1,
                                            selectedPick = null,
                                            toast = it.data?.messages?.first() ?: "Completed Successfully",
                                        )
                                    }

                                    getPickings()
                                } else {
                                    setSuspendedState {
                                        copy(error = it.data?.messages?.firstOrNull()?:"Failed")
                                    }
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


    private fun getPickings(
        loading: Loading = Loading.LOADING
    ) {
        if (state.loadingState == Loading.NONE){
            setState {
                copy(loadingState = loading)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.getPickingList(
                    customerId = row.customerID.toString(),
                    keyword = state.keyword,
                    sort = state.sort.sort,
                    rows = ROW_COUNT,
                    page = state.page,
                    order = state.sort.order.value
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
                                val list = state.pickingList + (it.data?.rows ?: emptyList())
                                setState {
                                    copy(
                                        pickingList = list,
                                    )
                                }
                                if (loading != Loading.SEARCHING && list.isEmpty()){
                                    setEffect {
                                        PickingDetailContract.Effect.NavBack
                                    }
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

    private fun modifyPicking(pick: PickingListRow){
        val quantity = state.quantity.text.toDoubleOrNull()

        if (quantity==null){
            setState {
                copy(error = "Please fill quantity")
            }
            return
        }
        if (!state.isModifying){
            setState {
                copy(isModifying = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.modifyPickQuantityBD(
                    pick.pickingID,
                    purchaseOrderDetailID = pick.purchaseOrderDetailID,
                    quantity = quantity
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isModifying = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isModifying = false)
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
                                    copy(showModify = null, toast = it.data.messages.firstOrNull()?:"Modified successfully", pickingList = emptyList(),page = 1)
                                }
                                getPickings()
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
            }
        }
    }


    private fun wasteOfPicking(
        row: PickingListRow
    ) {
        val quantity = state.quantity.text.toDoubleOrNull()
        if (state.quantity.text.isEmpty() || quantity == null){
            setState {
                copy(error = "please fill waste quantity")
            }
            return
        }
        if (quantity>row.quantity){
            setState {
                copy(error = "Waste quantity can't be greater then picked quantity")
            }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.wasteOnPicking(
                row.pickingID,
                quantity
            ).catch {
                setSuspendedState {
                    copy(isWasting = false, error = it.message?:"")
                }
            }.collect {
                setSuspendedState {
                    copy(isWasting = false)
                }
                when(it){
                    is BaseResult.Error -> {
                        setSuspendedState {
                            copy(error = it.message)
                        }
                    }
                    is BaseResult.Success ->{
                        if (it.data?.isSucceed == true){
                            setSuspendedState {
                                copy(toast = it.data.messages.firstOrNull()?:"Saved successfully", showWaste = null, pickingList = emptyList(),page = 1)
                            }
                            getPickings()
                        } else {
                            setSuspendedState {
                                copy(error = it.data?.messages?.firstOrNull()?:"")
                            }
                        }
                    }
                    BaseResult.UnAuthorized ->{}
                }
            }

        }
    }

}
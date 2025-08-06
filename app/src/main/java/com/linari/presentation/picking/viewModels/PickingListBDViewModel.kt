package com.linari.presentation.picking.viewModels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.picking.PickingRepository
import com.linari.data.picking.models.PurchaseOrderDetailListBDRow
import com.linari.data.picking.models.PurchaseOrderListBDRow
import com.linari.data.picking.models.PickingListBDRow
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.picking.contracts.PickingListBDContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PickingListBDViewModel(
    private val repository: PickingRepository,
    private val prefs: Prefs,
    private val purchase: PurchaseOrderListBDRow,
    private val purchaseDetail: PurchaseOrderDetailListBDRow
) : BaseViewModel<PickingListBDContract.Event, PickingListBDContract.State, PickingListBDContract.Effect>(){

    init {
        setState {
            copy(purchaseOrderRow = purchase, purchaseOrderDetailRow = purchaseDetail, hasWaste = prefs.getHasWaste(), hasModify = prefs.getHasModifyPick())
        }
        val sort = state.sortList.find {
            it.sort == prefs.getShippingOrderDetailSort() && it.order == Order.getFromValue(prefs.getShippingOrderDetailOrder())
        }
        if (sort!=null) setState {
            copy(sort = sort)
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }
    override fun setInitState(): PickingListBDContract.State {
        return PickingListBDContract.State()
    }

    override fun onEvent(event: PickingListBDContract.Event) {
        when(event){
            PickingListBDContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            PickingListBDContract.Event.OnBackPressed -> {
                setEffect {
                    PickingListBDContract.Effect.NavBack
                }
            }
            is PickingListBDContract.Event.OnChangeSort -> {
                prefs.setShippingOrderDetailSort(event.sort.sort)
                prefs.setShippingOrderDetailOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort)
                }
                getShippingOrderList()
            }
            PickingListBDContract.Event.OnFinish -> {
                finishPurchaseOrderDetail()
            }
            PickingListBDContract.Event.OnReachedEnd -> {
                if (state.page* ROW_COUNT<=state.shippingOrderDetailList.size){

                    getShippingOrderList(emptyList = false)
                }
            }
            PickingListBDContract.Event.OnRefresh -> {
                getShippingOrderList(loading = Loading.REFRESHING)
            }
            is PickingListBDContract.Event.OnSearch -> {
                setState {
                    copy(keyword = event.keyword)
                }
                getShippingOrderList(loading = Loading.SEARCHING)
            }
            is PickingListBDContract.Event.OnShowFinishConfirm -> {
                setState {
                    copy(showConfirmFinish = event.show)
                }
            }
            is PickingListBDContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }
            PickingListBDContract.Event.ReloadScreen -> {
                getShippingOrderList()
            }
            is PickingListBDContract.Event.OnModify -> {
                modifyPicking()
            }
            is PickingListBDContract.Event.OnSelectShippingDetail -> {
                setState {
                    copy(selectedPicking = event.picking, quantity = TextFieldValue())
                }
            }
            PickingListBDContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            is PickingListBDContract.Event.OnSelectForWaste -> {
                setState {
                    copy(selectedForWaste = event.picking, quantity = TextFieldValue())
                }
            }
            is PickingListBDContract.Event.OnWaste -> {
                wasteOfPicking(event.picking)
            }

            is PickingListBDContract.Event.OnQuantityChange -> {
                setState {
                    copy(quantity = event.quantity)
                }
            }
        }
    }

    private fun getShippingOrderList(
        loading: Loading = Loading.LOADING,
        emptyList: Boolean = true,
    ) {
        if (state.loadingState == Loading.NONE){
            if (emptyList){
                setState {
                    copy(shippingOrderDetailList = emptyList(),page = 1, loadingState = loading)
                }
            } else {
                setState {
                    copy(page = page + 1, loadingState = loading)
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.getShippingOrderDetailListBD(
                    state.keyword,
                    purchaseDetail.purchaseOrderDetailID,
                    state.page,
                    state.sort.sort,
                    state.sort.order.value
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", loadingState = Loading.NONE)
                    }
                }.collect {
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
                            val list = state.shippingOrderDetailList + (it.data?.rows?:emptyList())
                            setSuspendedState {
                                copy(
                                    shippingOrderDetailList = list,
                                    purchaseOrderDetailRow = it.data?.purchaseOrderDetail,
                                    rowCount = it.data?.total ?: 0,
                                    hasModify = if (list.size == 1) false else prefs.getHasModifyPick()
                                )
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
            }
        }
    }

    private fun finishPurchaseOrderDetail(){
        if (!state.isFinishing) {
            setState {
                copy(isFinishing = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.finishPurchaseOrderDetailBD(
                    purchaseDetail.purchaseOrderDetailID,
                    prefs.getWarehouse()!!.id
                )
                    .catch {
                        setSuspendedState {
                            copy(error = it.message ?: "", isFinishing = false, showConfirmFinish = false)
                        }
                    }
                    .collect {
                        setSuspendedState {
                            copy(isFinishing = false, showConfirmFinish = false)
                        }
                        when(it){
                            is BaseResult.Error -> {
                                setSuspendedState {
                                    copy(error = it.message)
                                }
                            }
                            is BaseResult.Success -> {
                                if (it.data?.isSucceed == true){
                                    setEffect {
                                        PickingListBDContract.Effect.NavBack
                                    }
                                } else {
                                    setSuspendedState {
                                        copy(error = it.data?.messages?.firstOrNull()?:"")
                                    }
                                }
                            }
                            BaseResult.UnAuthorized -> {}
                        }
                    }
            }
        }
    }


    private fun modifyPicking(){
        val quantity = state.quantity.text.toDoubleOrNull()

        if (quantity==null){
            setState {
                copy(error = "Please fill quantity")
            }
            return
        }
        if (quantity<0.0){
            setState {
                copy(error = "Modified quantity can't be less then zero")
            }
            return
        }
        if (!state.isModifying){
            if (state.selectedPicking!=null){
                setState {
                    copy(isModifying = true)
                }

                viewModelScope.launch(Dispatchers.IO) {
                    repository.modifyPickQuantityBD(
                        state.selectedPicking!!.pickingID,
                        purchaseOrderDetailID = state.selectedPicking!!.purchaseOrderDetailID,
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
                                        copy(selectedPicking = null, toast = it.data.messages.firstOrNull()?:"Modified successfully")
                                    }
                                    getShippingOrderList()
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
    }


    private fun wasteOfPicking(
        row: PickingListBDRow
    ) {
        val quantity = state.quantity.text.toDoubleOrNull()
        if (state.quantity.text.isEmpty() || quantity == null){
            setState {
                copy(error = "please fill waste quantity")
            }
            return
        }
        if (quantity>(row.splittedQuantity?:0.0)){
            setState {
                copy(error = "Waste quantity can't be greater then picked quantity")
            }
            return
        }

        if (quantity<0){
            setState {
                copy(error = "Waste quantity can't be less then zero")
            }
            return
        }

        if (!state.isWasting){
            setState {
                copy(isWasting = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.wasteOnPicking(
                    row.pickingID,
                    row.purchaseOrderDetailID,
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
                                    copy(toast = it.data.messages.firstOrNull()?:"Saved successfully", selectedForWaste = null)
                                }
                                getShippingOrderList()
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

}
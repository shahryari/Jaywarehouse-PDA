package com.linari.presentation.picking.viewModels

import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.picking.PickingRepository
import com.linari.data.picking.models.PurchaseOrderListBDRow
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.picking.contracts.PurchaseOrderContract
import com.linari.presentation.picking.contracts.PurchaseOrderDetailContract
import com.linari.presentation.picking.contracts.PurchaseOrderDetailContract.Effect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PurchaseOrderDetailViewModel(
    private val repository: PickingRepository,
    private val prefs: Prefs,
    private val purchaseRow: PurchaseOrderListBDRow
) : BaseViewModel<PurchaseOrderDetailContract.Event, PurchaseOrderDetailContract.State, PurchaseOrderDetailContract.Effect>(){
    init {
        setState {
            copy(purchaseOrderRow = purchaseRow)
        }
        val sort = state.sortList.find {
            it.sort == prefs.getPurchaseOrderDetailSort() && it.order == Order.getFromValue(prefs.getPurchaseOrderDetailOrder())
        }
        if (sort!=null) setState {
            copy(sort = sort)
        }
        setState {
            copy(warehouse = prefs.getWarehouse())
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }

    override fun setInitState(): PurchaseOrderDetailContract.State {
        return PurchaseOrderDetailContract.State()
    }

    override fun onEvent(event: PurchaseOrderDetailContract.Event) {
        when(event){
            PurchaseOrderDetailContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            PurchaseOrderDetailContract.Event.OnBackPressed -> {
                setEffect {
                    PurchaseOrderDetailContract.Effect.NavBack
                }
            }
            is PurchaseOrderDetailContract.Event.OnChangeSort -> {
                prefs.setPurchaseOrderDetailSort(event.sort.sort)
                prefs.setPurchaseOrderDetailOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort)
                }
                getPurchaseOrderDetailList()
            }
            is PurchaseOrderDetailContract.Event.OnPurchaseDetailClick -> {
                setEffect {
                    NavToShippingOrderDetail(event.purchase)
                }
            }
            PurchaseOrderDetailContract.Event.OnReachedEnd -> {
                if (state.page* ROW_COUNT<=state.purchaseOrderDetailList.size){
                    getPurchaseOrderDetailList(emptyList = false)
                }
            }
            PurchaseOrderDetailContract.Event.OnRefresh -> {
                getPurchaseOrderDetailList(loading = Loading.REFRESHING)
            }
            is PurchaseOrderDetailContract.Event.OnSearch -> {
                setState {
                    copy(keyword = event.keyword)
                }
                getPurchaseOrderDetailList(loading = Loading.SEARCHING)
            }
            is PurchaseOrderDetailContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }
            PurchaseOrderDetailContract.Event.ReloadScreen -> {
                getPurchaseOrderDetailList()
            }
        }

    }

    private fun getPurchaseOrderDetailList(
        loading: Loading = Loading.LOADING,
        emptyList: Boolean = true,
    ) {
        if (state.loadingState== Loading.NONE){
            if(emptyList){
                setState {
                    copy(purchaseOrderDetailList = emptyList(),page = 1, loadingState = loading)
                }
            } else {
                setState {
                    copy(page = page + 1, loadingState = loading)
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.getPurchaseOrderDetailListBD(
                    state.keyword,
                    purchaseRow.purchaseOrderID.toString(),
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
                            val list = state.purchaseOrderDetailList + (it.data?.rows?:emptyList())
                            setSuspendedState {
                                copy(
                                    purchaseOrderDetailList = list,
                                    rowCount = it.data?.total?:0
                                )
                            }
                            if (loading != Loading.SEARCHING && list.isEmpty()) {
                                setEffect {
                                    PurchaseOrderDetailContract.Effect.NavBack
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
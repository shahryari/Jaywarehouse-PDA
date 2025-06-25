package com.example.jaywarehouse.presentation.picking.viewModels

import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.common.utils.ROW_COUNT
import com.example.jaywarehouse.data.picking.PickingRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.picking.contracts.PurchaseOrderContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PurchaseOrderViewModel(
    private val repository: PickingRepository,
    private val prefs: Prefs
) : BaseViewModel<PurchaseOrderContract.Event, PurchaseOrderContract.State, PurchaseOrderContract.Effect>(){

    init {
        val sort = state.sortList.find {
            it.sort == prefs.getPurchaseOrderSort() && it.order == Order.getFromValue(prefs.getPurchaseOrderOrder())
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

    override fun setInitState(): PurchaseOrderContract.State {
        return PurchaseOrderContract.State()
    }

    override fun onEvent(event: PurchaseOrderContract.Event) {
        when(event){
            PurchaseOrderContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            PurchaseOrderContract.Event.OnBackPressed -> {
                setEffect {
                    PurchaseOrderContract.Effect.NavBack
                }
            }
            is PurchaseOrderContract.Event.OnChangeSort -> {
                prefs.setPurchaseOrderSort(event.sort.sort)
                prefs.setPurchaseOrderOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort)
                }
                getPurchaseOrderList()
            }
            is PurchaseOrderContract.Event.OnPurchaseClick -> {
                setEffect {
                    PurchaseOrderContract.Effect.NavToPurchaseOrderDetail(event.purchase)
                }
            }
            PurchaseOrderContract.Event.OnReachedEnd -> {
                if (state.page* ROW_COUNT<=state.purchaseOrderList.size){
                    getPurchaseOrderList(emptyList = false)
                }
            }
            PurchaseOrderContract.Event.OnRefresh -> {
                getPurchaseOrderList(loading = Loading.REFRESHING)
            }
            is PurchaseOrderContract.Event.OnSearch -> {
                setState {
                    copy(keyword = event.keyword)
                }
                getPurchaseOrderList(loading = Loading.SEARCHING)
            }
            is PurchaseOrderContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }
            PurchaseOrderContract.Event.ReloadScreen -> {
                getPurchaseOrderList()
            }
        }

    }

    private fun getPurchaseOrderList(
        loading: Loading = Loading.LOADING,
        emptyList: Boolean = true,
    ) {
        if (state.loadingState== Loading.NONE){
            if(emptyList){
                setState {
                    copy(purchaseOrderList = emptyList(),page = 1, loadingState = loading)
                }
            } else {
                setState {
                    copy(page = page + 1, loadingState = loading)
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.getPurchaseOrderListBD(
                    state.keyword,
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
                            val list = state.purchaseOrderList + (it.data?.rows?:emptyList())
                            setSuspendedState {
                                copy(
                                    purchaseOrderList = list
                                )
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
            }
        }
    }

}
package com.linari.presentation.loading.viewmodels

import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.loading.LoadingRepository
import com.linari.data.loading.models.LoadingListGroupedRow
import com.linari.data.pallet.model.PalletConfirmRow
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.loading.contracts.LoadingDetailContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LoadingDetailViewModel(
    private val repository: LoadingRepository,
    private val prefs: Prefs,
    private val row: LoadingListGroupedRow,
) : BaseViewModel<LoadingDetailContract.Event,LoadingDetailContract.State,LoadingDetailContract.Effect>(){
    init {
        val selectedSort = state.sortList.find {
            it.sort == prefs.getLoadingDetailSort() && it.order == Order.getFromValue(prefs.getLoadingDetailOrder())
        }
        if (selectedSort!=null) {
            setState {
                copy(sort = selectedSort)
            }
        }
        setState {
            copy(
                loadingRow = row,
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
        getDetails()
    }

    override fun setInitState(): LoadingDetailContract.State {
        return LoadingDetailContract.State()
    }

    override fun onEvent(event: LoadingDetailContract.Event) {
        when(event){
            LoadingDetailContract.Event.OnNavBack -> {
                setEffect {
                    LoadingDetailContract.Effect.NavBack
                }
            }
            LoadingDetailContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            is LoadingDetailContract.Event.OnSelectDetail -> {
                setState {
                    copy(selectedLoading = event.detail)
                }
            }

            LoadingDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            LoadingDetailContract.Event.OnReachEnd -> {
                if (ROW_COUNT*state.page<=state.details.size){
                    setState {
                        copy(page = state.page+1)
                    }
                    getDetails()
                }
            }

            LoadingDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, details = emptyList())
                }
                getDetails(Loading.REFRESHING)
            }
            is LoadingDetailContract.Event.OnConfirmLoading -> {
                completeChecking(event.item)
            }
//            is LoadingDetailContract.Event.OnChangeKeyword -> {
//                setState {
//                    copy(keyword = event.keyword)
//                }
//            }
            is LoadingDetailContract.Event.OnSearch -> {
                setState {
                    copy(details = emptyList(), page = 1, keyword = event.keyword)
                }
                getDetails(Loading.SEARCHING)
            }
            is LoadingDetailContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }
            is LoadingDetailContract.Event.OnSortChange -> {
                prefs.setLoadingDetailSort(event.sortItem.sort)
                prefs.setLoadingDetailOrder(event.sortItem.order.value)
                setState {
                    copy(sort = event.sortItem, page = 1, details = emptyList())
                }
                getDetails()
            }
        }
    }


    private fun completeChecking(
        loading: PalletConfirmRow,
    ) {

        if (!state.onSaving){
            setState {
                copy(onSaving = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.confirmLoading(
                    loading.palletManifestID
                )
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
                                            details = emptyList(),
                                            page = 1,
                                            selectedLoading = null,
                                            toast = it.data.messages.firstOrNull() ?: "Completed successfully.",
                                        )
                                    }
                                    getDetails()
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


    private fun getDetails(
        loading: Loading = Loading.LOADING
    ) {
        if (state.loadingState == Loading.NONE){
            setState {
                copy(loadingState = loading)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.getLoadingList(
                    customerCode = row.customerCode?:"",
                    keyword = state.keyword,
                    warehouseID = prefs.getWarehouse()?.id?:0,
                    sort = state.sort.sort,
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
                                val list = state.details + (it.data?.rows ?: emptyList())
                                setState {
                                    copy(
                                        details = list,
                                        rowCount = it.data?.total?:0
                                    )
                                }
                                if (loading != Loading.SEARCHING && list.isEmpty()){
                                    setEffect {
                                        LoadingDetailContract.Effect.NavBack
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
}
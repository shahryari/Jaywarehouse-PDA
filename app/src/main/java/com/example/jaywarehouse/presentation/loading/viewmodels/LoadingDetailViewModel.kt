package com.example.jaywarehouse.presentation.loading.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.loading.LoadingRepository
import com.example.jaywarehouse.data.loading.models.LoadingListGroupedRow
import com.example.jaywarehouse.data.pallet.model.PalletConfirmRow
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.loading.contracts.LoadingDetailContract
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
        getDetails(sort = state.sort)
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
                if (10*state.page<=state.details.size){
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getDetails(keyword = state.keyword,page = state.page,sort = state.sort)
                }
            }

            LoadingDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, details = emptyList(), loadingState = Loading.REFRESHING)
                }
                getDetails(keyword = state.keyword,page = state.page,sort = state.sort)
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
                    copy(loadingState = Loading.SEARCHING, details = emptyList(), page = 1, keyword = event.keyword)
                }
                getDetails(keyword = state.keyword,page = state.page,sort = state.sort)
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
                    copy(sort = event.sortItem, page = 1, details = emptyList(), loadingState = Loading.LOADING)
                }
                getDetails(keyword = state.keyword,page = state.page,sort = event.sortItem)
            }
        }
    }


    private fun completeChecking(
        loading: PalletConfirmRow,
    ) {

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
                            setSuspendedState {
                                copy(
                                    details = emptyList(),
                                    page = 1,
                                    selectedLoading = null,
                                    toast = it.data?.messages?.first() ?: "",
                                    loadingState = Loading.LOADING
                                )
                            }
                            getDetails(keyword = state.keyword,page = state.page,sort = state.sort)
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


    private fun getDetails(keyword: String = "", page: Int = 1, sort: SortItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getLoadingList(
                customerCode = row.customerCode?:"",
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
                                    details = details + (it.data?.rows ?: emptyList()),
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
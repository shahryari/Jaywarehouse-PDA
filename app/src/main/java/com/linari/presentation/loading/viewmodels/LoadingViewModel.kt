package com.linari.presentation.loading.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.loading.LoadingRepository
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.loading.contracts.LoadingContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LoadingViewModel(
    private val repository: LoadingRepository,
    private val prefs: Prefs
) : BaseViewModel<LoadingContract.Event,LoadingContract.State,LoadingContract.Effect>(){

    init {
        val sort = state.sortList.find {
            it.sort == prefs.getLoadingSort() && it.order == Order.getFromValue(prefs.getLoadingOrder())
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

    override fun setInitState(): LoadingContract.State {
        return LoadingContract.State()
    }

    override fun onEvent(event: LoadingContract.Event) {
        when(event){
//            is LoadingContract.Event.OnChangeKeyword ->{
//                setState {
//                    copy(keyword = event.keyword)
//                }
//            }
            is LoadingContract.Event.OnNavToLoadingDetail -> setEffect {
                LoadingContract.Effect.NavToLoadingDetail(event.item)
            }

            LoadingContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            is LoadingContract.Event.OnChangeSort -> {
                prefs.setLoadingSort(event.sort.sort)
                prefs.setLoadingOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort, loadingList = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getLoadingList(state.keyword,state.page,event.sort)
            }
            is LoadingContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }

            LoadingContract.Event.ReloadScreen -> {

                setState {
                    copy(page = 1, loadingList = emptyList(), loadingState = Loading.LOADING, keyword = "")
                }

                getLoadingList(state.keyword,state.page,state.sort)
            }

            LoadingContract.Event.OnReachedEnd -> {
                if (ROW_COUNT*state.page<=state.loadingList.size) {
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getLoadingList(state.keyword,state.page,state.sort)
                }
            }

            is LoadingContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, loadingList = emptyList(), loadingState = Loading.SEARCHING, keyword = event.keyword)
                }
                getLoadingList(state.keyword,state.page,state.sort)
            }

            LoadingContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, loadingList = emptyList(), loadingState = Loading.REFRESHING)
                }
                getLoadingList(state.keyword,state.page,state.sort)
            }

            LoadingContract.Event.OnBackPressed -> {
                setEffect {
                    LoadingContract.Effect.NavBack
                }
            }

        }
    }


    private fun getLoadingList(
        keyword: String,
        page: Int = 1,
        sort: SortItem
    ) {
        viewModelScope.launch {
            repository.getLoadingListGrouped(
                keyword = keyword, warehouseID = prefs.getWarehouse()!!.id,page,sort.sort,sort.order.value
            )
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"", loadingState = Loading.NONE)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(loadingState = Loading.NONE)
                    }
                    when(it){
                        is BaseResult.Error ->  {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(
                                    loadingList = loadingList + (it.data?.rows?: emptyList()),
                                    rowCount = it.data?.total?:0
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
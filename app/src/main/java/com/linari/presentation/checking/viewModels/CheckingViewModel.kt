package com.linari.presentation.checking.viewModels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.checking.CheckingRepository
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.presentation.checking.contracts.CheckingContract
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CheckingViewModel(
    private val repository: CheckingRepository,
    private val prefs: Prefs
) : BaseViewModel<CheckingContract.Event,CheckingContract.State,CheckingContract.Effect>(){

    init {
        val sort = state.sortList.find {
            it.sort == prefs.getCheckingSort() && it.order == Order.getFromValue(prefs.getCheckingOrder())
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

    override fun setInitState(): CheckingContract.State {
        return CheckingContract.State()
    }

    override fun onEvent(event: CheckingContract.Event) {
        when(event){
//            is CheckingContract.Event.OnChangeKeyword ->{
//                setState {
//                    copy(keyword = event.keyword)
//                }
//            }
            is CheckingContract.Event.OnNavToCheckingDetail -> setEffect {
                CheckingContract.Effect.NavToCheckingDetail(event.item)
            }

            CheckingContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            is CheckingContract.Event.OnChangeSort -> {
                prefs.setCheckingSort(event.sort.sort)
                prefs.setCheckingOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort, checkingList = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getCheckingList(state.keyword,state.page,event.sort)
            }
            is CheckingContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }

            CheckingContract.Event.ReloadScreen -> {

                setState {
                    copy(page = 1, checkingList = emptyList(), loadingState = Loading.LOADING, keyword = "")
                }

                getCheckingList(state.keyword,state.page,state.sort)
            }

            CheckingContract.Event.OnReachedEnd -> {
                if (ROW_COUNT*state.page<=state.checkingList.size) {
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getCheckingList(state.keyword,state.page,state.sort)
                }
            }

            is CheckingContract.Event.OnSearch -> {
                setState {
                    copy(keyword = event.keyword,page = 1, checkingList = emptyList(), loadingState = Loading.SEARCHING)
                }
                getCheckingList(state.keyword,state.page,state.sort)
            }

            CheckingContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, checkingList = emptyList(), loadingState = Loading.REFRESHING)
                }
                getCheckingList(state.keyword,state.page,state.sort)
            }

            CheckingContract.Event.OnBackPressed -> {
                setEffect {
                    CheckingContract.Effect.NavBack
                }
            }

        }
    }


    private fun getCheckingList(
        keyword: String,
        page: Int = 1,
        sort: SortItem
    ) {
        viewModelScope.launch {
            repository.getCheckingListGroupedModel(
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
                                copy(checkingList = checkingList + (it.data?.rows?: emptyList()), loadingState = Loading.NONE, rowCount = it.data?.total?:0)
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
        }
    }

}
package com.example.jaywarehouse.presentation.putaway.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.putaway.PutawayRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.putaway.contracts.PutawayContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PutawayViewModel(
    private val repository: PutawayRepository,
    private val prefs: Prefs
) : BaseViewModel<PutawayContract.Event,PutawayContract.State,PutawayContract.Effect>(){

    init {
        val sort = state.sortList.find {
            it.sort == prefs.getPutawaySort() && it.order == Order.getFromValue(prefs.getPutawayOrder())
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

    override fun setInitState(): PutawayContract.State {
        return PutawayContract.State()
    }

    override fun onEvent(event: PutawayContract.Event) {
        when(event){
            is PutawayContract.Event.OnNavToPutawayDetail -> setEffect {
                PutawayContract.Effect.NavToPutawayDetail(event.readyToPutRow,false)
            }

            PutawayContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            is PutawayContract.Event.OnChangeSort -> {
                prefs.setPutawaySort(event.sort.sort)
                prefs.setPutawayOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort, puts = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getPutawayList(state.keyword,state.page,event.sort)
            }
            is PutawayContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }

            PutawayContract.Event.ReloadScreen -> {

                setState {
                    copy(page = 1, puts = emptyList(), loadingState = Loading.LOADING, keyword = "")
                }

                getPutawayList(state.keyword,state.page,state.sort)
            }

            PutawayContract.Event.OnReachedEnd -> {
                if (10*state.page<=state.puts.size) {
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getPutawayList(state.keyword,state.page,state.sort)
                }
            }

            is PutawayContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, puts = emptyList(), loadingState = Loading.SEARCHING, keyword = event.keyword)
                }
                getPutawayList(state.keyword,state.page,state.sort)
            }

            PutawayContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, puts = emptyList(), loadingState = Loading.REFRESHING)
                }
                getPutawayList(state.keyword,state.page,state.sort)
            }

            PutawayContract.Event.OnBackPressed -> {
                setEffect {
                    PutawayContract.Effect.NavBack
                }
            }

        }
    }


    private fun getPutawayList(
        keyword: String,
        page: Int = 1,
        sort: SortItem
    ) {
        viewModelScope.launch {
            repository.getPutawayListGrouped(
                keyword = keyword,page,sort.sort,sort.order.value
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
                                copy(puts = puts + (it.data?.rows?: emptyList()), loadingState = Loading.NONE)
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
        }
    }

}
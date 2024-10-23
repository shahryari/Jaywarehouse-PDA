package com.example.jaywarehouse.presentation.putaway.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.putaway.PutawayRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.putaway.contracts.PutawayContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PutawayViewModel(
    private val repository: PutawayRepository,
    private val prefs: Prefs
) : BaseViewModel<PutawayContract.Event,PutawayContract.State,PutawayContract.Effect>(){

    init {
        setState {
            copy(sort = prefs.getPutawaySort(), order = prefs.getPutawayOrder())
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
            is PutawayContract.Event.OnChangeKeyword ->{
                setState {
                    copy(keyword = event.keyword)
                }
            }
            is PutawayContract.Event.OnNavToPutawayDetail -> setEffect {
                PutawayContract.Effect.NavToPutawayDetail(event.readyToPutRow,false)
            }

            PutawayContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }

            is PutawayContract.Event.OnChangeOrder -> {
                prefs.setPutawayOrder(event.order)
                setState {
                    copy(order = event.order, puts = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getReadyToPut("",state.page,state.sort,event.order)
            }
            is PutawayContract.Event.OnChangeSort -> {
                prefs.setPutawaySort(event.sort)
                setState {
                    copy(sort = event.sort, puts = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getReadyToPut("",state.page,event.sort,state.order)
            }
            is PutawayContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }

            PutawayContract.Event.ReloadScreen -> {

                setState {
                    copy(page = 1, puts = emptyList(), loadingState = Loading.LOADING, keyword = TextFieldValue())
                }
                getReadyToPut(state.keyword.text,state.page,state.sort,state.order)
            }

            PutawayContract.Event.OnReachedEnd -> {
                if (10*state.page<=state.puts.size) {
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getReadyToPut(state.keyword.text,state.page,state.sort,state.order)
                }
            }

            PutawayContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, puts = emptyList(), loadingState = Loading.SEARCHING)
                }
                getReadyToPut(
                    state.keyword.text,
                    page = state.page,
                    sort = state.sort,
                    order = state.order,
                    isSearching = true
                )
            }

            PutawayContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, puts = emptyList(), loadingState = Loading.REFRESHING)
                }
                getReadyToPut(state.keyword.text,state.page,state.sort,state.order)
            }
        }
    }

    private fun getReadyToPut(keyword: String, page: Int = 1, sort: String, order: String,isSearching: Boolean = false){
        viewModelScope.launch(Dispatchers.IO) {

            repository.getReadyToPut(keyword,page,10,sort, order)
                .catch {
                    setSuspendedState {
                        copy(loadingState = Loading.NONE, error = it.message?:"")
                    }
                }
                .collect{
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
                            setSuspendedState {
                                copy(readToPut = it.data, puts = puts+(it.data?.rows?: emptyList()))
                            }
                            if (state.puts.size == 1 && prefs.getIsNavToDetail() && isSearching){
                                val put = state.puts.first()
                                setEffect {
                                    PutawayContract.Effect.NavToPutawayDetail(put,state.keyword.text==put.locationCode)
                                }
                            }
                        }
                        else -> {}
                    }
                }
        }
    }
}
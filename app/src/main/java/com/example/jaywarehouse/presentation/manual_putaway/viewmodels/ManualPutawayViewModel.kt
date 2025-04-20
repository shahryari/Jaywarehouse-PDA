package com.example.jaywarehouse.presentation.manual_putaway.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.manual_putaway.ManualPutawayRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.manual_putaway.contracts.ManualPutawayContract
import com.example.jaywarehouse.presentation.manual_putaway.contracts.ManualPutawayContract.Effect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ManualPutawayViewModel(
    private val repository: ManualPutawayRepository,
    private val prefs: Prefs
)  : BaseViewModel<ManualPutawayContract.Event,ManualPutawayContract.State,ManualPutawayContract.Effect>(){
    init {
        val sort = state.sortList.find {
            it.sort == prefs.getManualPutawaySort() && it.order == Order.getFromValue(prefs.getManualPutawayOrder())
        }
        if (sort!=null) {
            setState {
                copy(selectedSort = sort)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }
    override fun setInitState(): ManualPutawayContract.State {
        return ManualPutawayContract.State()
    }

    override fun onEvent(event: ManualPutawayContract.Event) {
        when(event){
            ManualPutawayContract.Event.OnCloseError -> {
                setState {
                    copy(error = "")
                }
            }
//            is ManualPutawayContract.Event.OnKeywordChange -> {
//                setState {
//                    copy(keyword = event.keyword)
//                }
//            }
            ManualPutawayContract.Event.OnNavBack -> {
                setEffect {
                    ManualPutawayContract.Effect.NavBack
                }
            }
            is ManualPutawayContract.Event.OnPutawayClick ->  {
                setEffect {
                    NavToPutawayDetail(event.putaway)
                }
            }
            ManualPutawayContract.Event.OnReachEnd -> {
                if (10*state.page <= state.putaways.size){
                    setState {
                        copy(page = page+1, loadingState = Loading.LOADING)
                    }
                    getPutaways(state.keyword,state.page,state.selectedSort)
                }
            }
            ManualPutawayContract.Event.OnReloadScreen -> {
                setState {
                    copy(page = 1, loadingState = Loading.REFRESHING, putaways = emptyList())
                }
                getPutaways(state.keyword,state.page,state.selectedSort)
            }
            is ManualPutawayContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, loadingState = Loading.SEARCHING, putaways = emptyList(), keyword = event.keyword)
                }
                getPutaways(state.keyword,state.page,state.selectedSort)
            }
            is ManualPutawayContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }
            is ManualPutawayContract.Event.OnSortChange -> {
                prefs.setManualPutawaySort(event.sort.sort)
                prefs.setManualPutawayOrder(event.sort.order.value)
                setState {
                    copy(selectedSort = event.sort, loadingState = Loading.LOADING, page = 1, putaways = emptyList())
                }
                getPutaways(state.keyword,state.page,state.selectedSort)

            }

            ManualPutawayContract.Event.FetchData ->  {

                setState {
                    copy(page = 1, loadingState = Loading.LOADING, putaways = emptyList())
                }
                getPutaways(state.keyword,state.page,state.selectedSort)
            }
        }
    }

    private fun getPutaways(
        keyword: String = "",
        page: Int = 1,
        sortItem: SortItem
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getManualPutawayList(
                keyword,
                page,
                sort = sortItem.sort,
                sortItem.order.value
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
                        setState {
                            copy(error = it.message)
                        }
                    }
                    is BaseResult.Success -> {
                        setSuspendedState {
                            copy(
                                putaways = putaways + (it.data?.rows?: emptyList())
                            )
                        }
                    }
                    BaseResult.UnAuthorized -> {}
                }
            }
        }
    }
}
package com.linari.presentation.cycle_count.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.common.utils.addAll
import com.linari.data.cycle_count.CycleRepository
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.cycle_count.contracts.CycleCountContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CycleViewModel(
    private val repository: CycleRepository,
    private val prefs: Prefs
) : BaseViewModel<CycleCountContract.Event,CycleCountContract.State,CycleCountContract.Effect>(){

    init {
        val sort = state.sortList.find {
            it.sort == prefs.getCycleSort() && it.order == Order.getFromValue(prefs.getCycleOrder())
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

    override fun setInitState(): CycleCountContract.State {
        return CycleCountContract.State()
    }

    override fun onEvent(event: CycleCountContract.Event) {
        when(event){
//            is CycleCountContract.Event.OnChangeKeyword ->{
//                setState {
//                    copy(keyword = event.keyword)
//                }
//            }
            is CycleCountContract.Event.OnNavToCycleCountDetail -> setEffect {
                CycleCountContract.Effect.NavToCycleCountDetail(event.item)
            }

            CycleCountContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            is CycleCountContract.Event.OnChangeSort -> {
                prefs.setCycleSort(event.sort.sort)
                prefs.setCycleOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort, cycleList = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getCycleList()
            }
            is CycleCountContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }

            CycleCountContract.Event.ReloadScreen -> {

                setState {
                    copy(page = 1, cycleList = emptyList(), loadingState = Loading.LOADING, keyword = "")
                }

                getCycleList()
            }

            CycleCountContract.Event.OnReachedEnd -> {
                if (ROW_COUNT*state.page<=state.cycleList.size) {
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getCycleList()
                }
            }

            is CycleCountContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, cycleList = emptyList(), loadingState = Loading.SEARCHING, keyword = event.keyword)
                }
                getCycleList()
            }

            CycleCountContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, cycleList = emptyList(), loadingState = Loading.REFRESHING)
                }
                getCycleList()
            }

            CycleCountContract.Event.OnBackPressed -> {
                setEffect {
                    CycleCountContract.Effect.NavBack
                }
            }

        }
    }


    private fun getCycleList() {
        viewModelScope.launch {
            repository.getCycleCountLocations(
                keyword = state.keyword,state.page,state.sort.sort,state.sort.order.value
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
                            val list = state.cycleList.addAll(it.data?.rows)
                            setSuspendedState {
                                copy(
                                    cycleList = list,
                                    loadingState = Loading.NONE,
                                    cycleCount = it.data?.total?:0
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
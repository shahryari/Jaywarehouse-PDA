package com.example.jaywarehouse.presentation.picking.viewModels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.picking.PickingRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.picking.contracts.PickingContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PickingViewModel(
    private val repository: PickingRepository,
    private val prefs: Prefs
) : BaseViewModel<PickingContract.Event,PickingContract.State,PickingContract.Effect>(){

    init {
        val sort = state.sortList.find {
            it.sort == prefs.getPickingCustomerSort() && it.order == Order.getFromValue(prefs.getPickingCustomerOrder())
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

    override fun setInitState(): PickingContract.State {
        return PickingContract.State()
    }

    override fun onEvent(event: PickingContract.Event) {
        when(event){
//            is PickingContract.Event.OnChangeKeyword ->{
//                setState {
//                    copy(keyword = event.keyword)
//                }
//            }
            is PickingContract.Event.OnNavToPickingDetail -> setEffect {
                PickingContract.Effect.NavToPickingDetail(event.pick)
            }

            PickingContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            is PickingContract.Event.OnChangeSort -> {
                prefs.setPickingCustomerSort(event.sort.sort)
                prefs.setPickingCustomerOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort, pickings = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getPickings(state.keyword,state.page,event.sort)
            }
            is PickingContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }

            PickingContract.Event.ReloadScreen -> {

                setState {
                    copy(page = 1, pickings = emptyList(), loadingState = Loading.LOADING, keyword = "")
                }

                getPickings(state.keyword,state.page,state.sort)
            }

            PickingContract.Event.OnReachedEnd -> {
                if (10*state.page<=state.pickings.size) {
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getPickings(state.keyword,state.page,state.sort)
                }
            }

            is PickingContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, pickings = emptyList(), loadingState = Loading.SEARCHING, keyword = event.keyword)
                }
                getPickings(state.keyword,state.page,state.sort)
            }

            PickingContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, pickings = emptyList(), loadingState = Loading.REFRESHING)
                }
                getPickings(state.keyword,state.page,state.sort)
            }

            PickingContract.Event.OnBackPressed -> {
                setEffect {
                    PickingContract.Effect.NavBack
                }
            }

        }
    }


    private fun getPickings(
        keyword: String,
        page: Int = 1,
        sort: SortItem
    ) {
        viewModelScope.launch {
            repository.getPickingListGrouped(
                keyword = keyword,page,10,sort.sort,sort.order.value
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
                                copy(pickings = pickings + (it.data?.rows?: emptyList()), loadingState = Loading.NONE)
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
        }
    }

}
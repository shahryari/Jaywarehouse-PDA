package com.example.jaywarehouse.presentation.pallet

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.checking.CheckingRepository
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.pallet.PalletRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PalletConfirmViewModel(
    private val repository: PalletRepository,
    private val prefs: Prefs
) : BaseViewModel<PalletConfirmContract.Event,PalletConfirmContract.State,PalletConfirmContract.Effect>(){

    init {
        val sortValue = state.sortList.find {
            it.sort == prefs.getPalletSort() && it.order == Order.getFromValue(prefs.getPalletOrder())
        }
        if (sortValue!=null) setState {
            copy(sort = sortValue)
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }

    override fun setInitState(): PalletConfirmContract.State {
        return PalletConfirmContract.State()
    }

    override fun onEvent(event: PalletConfirmContract.Event) {
        when(event){
//            is PalletConfirmContract.Event.OnChangeKeyword ->{
//                setState {
//                    copy(keyword = event.keyword)
//                }
//            }

            PalletConfirmContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            is PalletConfirmContract.Event.OnChangeSort -> {
                prefs.setPalletSort(event.sort.sort)
                prefs.setPalletOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort, palletList = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getPalletList(state.keyword,state.page,event.sort)
            }
            is PalletConfirmContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }

            PalletConfirmContract.Event.ReloadScreen -> {

                setState {
                    copy(page = 1, palletList = emptyList(), loadingState = Loading.LOADING, keyword = "")
                }

                getPalletList(state.keyword,state.page,state.sort)
            }

            PalletConfirmContract.Event.OnReachedEnd -> {
                if (10*state.page<=state.palletList.size) {
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getPalletList(state.keyword,state.page,state.sort)
                }
            }

            is PalletConfirmContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, palletList = emptyList(), loadingState = Loading.SEARCHING, keyword = event.keyword)
                }
                getPalletList(state.keyword,state.page,state.sort)
            }

            PalletConfirmContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, palletList = emptyList(), loadingState = Loading.REFRESHING)
                }
                getPalletList(state.keyword,state.page,state.sort)
            }

            PalletConfirmContract.Event.OnBackPressed -> {
                setEffect {
                    PalletConfirmContract.Effect.NavBack
                }
            }

            is PalletConfirmContract.Event.ConfirmPallet -> {
                confirmPallet(event.pallet.palletManifestID)
            }
            is PalletConfirmContract.Event.OnSelectPallet -> {
                setState {
                    copy(selectedPallet = event.pallet)
                }
            }

            PalletConfirmContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }
        }
    }

    fun confirmPallet(palletManifestId: Int) {
        if (!state.isConfirming){
            setState {
                copy(isConfirming = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.completePalletManifest(
                    palletManifestId.toString()
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isConfirming = false, selectedPallet = null)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isConfirming = false,selectedPallet = null)
                    }
                    when(it) {
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            if (it.data?.isSucceed == true) {
                                setSuspendedState {
                                    copy(
                                        toast = it.data?.messages?.firstOrNull()?:"Confirmed Successfully",
                                        palletList = emptyList(),
                                        page = 1
                                    )
                                }
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"Failed")
                                }
                            }
                            getPalletList(state.keyword,state.page,state.sort)
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
            }
        }
    }


    private fun getPalletList(
        keyword: String,
        page: Int = 1,
        sort: SortItem
    ) {
        viewModelScope.launch {
            repository.getPalletList(
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
                                copy(palletList = palletList + (it.data?.rows?: emptyList()), loadingState = Loading.NONE)
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
        }
    }

}
package com.linari.presentation.pallet.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.pallet.PalletRepository
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.pallet.contracts.PalletConfirmContract
import com.linari.presentation.pallet.contracts.PalletConfirmContract.Effect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PalletConfirmViewModel(
    private val repository: PalletRepository,
    private val prefs: Prefs
) : BaseViewModel<PalletConfirmContract.Event, PalletConfirmContract.State, PalletConfirmContract.Effect>(){

    init {
        val sortValue = state.sortList.find {
            it.sort == prefs.getPalletSort() && it.order == Order.Companion.getFromValue(prefs.getPalletOrder())
        }
        setState {
            copy(hasBoxOnShipping = prefs.getWarehouse()?.hasBoxOnShipping == true)
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
                if (ROW_COUNT *state.page<=state.palletList.size) {
                    setState {
                        copy(page = page+1, loadingState = Loading.LOADING)
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
                    copy(selectedPallet = event.pallet, bigQuantity = TextFieldValue(), smallQuantity = TextFieldValue())
                }
            }

            PalletConfirmContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            is PalletConfirmContract.Event.OnNavToDetail -> {
                setEffect {
                    NavToDetail(event.pallet)
                }
            }

            is PalletConfirmContract.Event.ChangeBigQuantity -> {
                setState {
                    copy(bigQuantity = event.quantity)
                }
            }
            is PalletConfirmContract.Event.ChangeSmallQuantity -> {
                setState {
                    copy(smallQuantity = event.quantity)
                }
            }
            is PalletConfirmContract.Event.OnConfirmBox -> {
                palletManifestBox(event.pallet.palletManifestID)
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
                                getPalletList(state.keyword,state.page,state.sort)
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"Failed")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
            }
        }
    }

    fun palletManifestBox(palletManifestId: Int) {
        val bigQuantity = state.bigQuantity.text.toIntOrNull()
        val smallQuantity = state.smallQuantity.text.toIntOrNull()
        if ((bigQuantity?:0)<0){
            setState {
                copy(error = "Big box quantity can't be less then zero")
            }
            return
        }
        if ((smallQuantity?:0)<0){
            setState {
                copy(error = "Small box quantity can't be less then zero")
            }
            return
        }
        if (!state.isConfirming){
            setState {
                copy(isConfirming = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.palletManifestBox(
                    palletManifestId,
                    bigQuantity,
                    smallQuantity
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
                                getPalletList(state.keyword,state.page,state.sort)
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"Failed")
                                }
                            }
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
                keyword = keyword, warehousID = prefs.getWarehouse()!!.id,page,sort.sort,sort.order.value
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
                                copy(palletList = palletList + (it.data?.rows?: emptyList()), loadingState = Loading.NONE, rowCount = it.data?.total?:0)
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
        }
    }




}
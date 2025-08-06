package com.linari.presentation.pallet.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.pallet.PalletRepository
import com.linari.data.pallet.model.PalletConfirmRow
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.pallet.contracts.PalletProductContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PalletProductViewModel(
    private val palletRow: PalletConfirmRow,
    private val repository: PalletRepository,
    private val prefs: Prefs
) : BaseViewModel<PalletProductContract.Event, PalletProductContract.State, PalletProductContract.Effect>() {
    init {
        setState {
            copy(hasBoxOnShipping = prefs.getWarehouse()?.hasBoxOnShipping == true, pallet = palletRow)
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard()
                .collect {
                    setSuspendedState {
                        copy(lockKeyboard = it)
                    }
                }
        }
    }

    override fun setInitState(): PalletProductContract.State {
        return PalletProductContract.State()
    }

    override fun onEvent(event: PalletProductContract.Event) {
        when (event) {
            PalletProductContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }

            PalletProductContract.Event.CloseToast -> {
                setState {
                    copy(toast = "")
                }
            }
            PalletProductContract.Event.FetchData -> {
                getPalletProducts()
            }
            PalletProductContract.Event.OnNavBack -> {
                setEffect {
                    PalletProductContract.Effect.NavBack
                }
            }
            PalletProductContract.Event.OnReachEnd -> {
                if (state.page* ROW_COUNT<=state.productList.size){
                    getPalletProducts(loadNext = true)
                }
            }
            PalletProductContract.Event.OnRefresh -> {
                getPalletProducts(Loading.REFRESHING)
            }
            is PalletProductContract.Event.OnSearch -> {
                setState {
                    copy(keyword = event.keyword)
                }
                getPalletProducts(Loading.SEARCHING)
            }
            is PalletProductContract.Event.OnSelectSort -> {
                setState {
                    copy(sort = event.sort)
                }
                getPalletProducts()
            }
            is PalletProductContract.Event.OnShowConfirm -> {
                setState {
                    copy(showConfirm = event.show, bigQuantity = TextFieldValue(), smallQuantity = TextFieldValue())
                }
            }
            is PalletProductContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }

            PalletProductContract.Event.OnConfirm -> {
                confirmPallet()
            }

            is PalletProductContract.Event.ChangeBigQuantity -> {
                setState {
                    copy(bigQuantity = event.quantity)
                }
            }
            is PalletProductContract.Event.ChangeSmallQuantity -> {
                setState {
                    copy(smallQuantity = event.quantity)
                }
            }
            PalletProductContract.Event.OnConfirmBox -> {
                palletManifestBox()
            }
        }
    }

    private fun getPalletProducts(loading: Loading = Loading.LOADING, loadNext: Boolean = false,) {
        if (state.loadingState == Loading.NONE){
            if (loadNext){
                setState {
                    copy(page = page  + 1, loadingState = loading)
                }
            } else {
                setState {
                    copy(page = 1, productList = emptyList(), loadingState = loading)
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.getPalletProductList(
                    state.keyword,
                    palletManifestId = palletRow.palletManifestID.toString(),
                    state.page,
                    state.sort.sort,
                    state.sort.order.value
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
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(
                                    productList = if (loadNext) productList + (it.data?.rows?:emptyList()) else it.data?.rows?:emptyList(),
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

    fun confirmPallet() {
        if (!state.isConfirming){
            setState {
                copy(isConfirming = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.completePalletManifest(
                    palletRow.palletManifestID.toString()
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isConfirming = false, showConfirm = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isConfirming = false, showConfirm = false)
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
                                    )
                                }
                                setEffect {
                                    PalletProductContract.Effect.NavBack
                                }
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

    private fun palletManifestBox(){
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
                    palletRow.palletManifestID,
                    bigQuantity,
                    smallQuantity
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isConfirming = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isConfirming = false)
                    }
                    when(it){
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
                                        showConfirm = false
                                    )
                                }
                                setEffect {
                                    PalletProductContract.Effect.NavBack
                                }
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
}
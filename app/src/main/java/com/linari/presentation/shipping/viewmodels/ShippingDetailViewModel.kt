package com.linari.presentation.shipping.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.picking.models.PalletManifest
import com.linari.data.shipping.ShippingRepository
import com.linari.data.shipping.models.ShippingRow
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.shipping.contracts.ShippingDetailContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ShippingDetailViewModel(
    private val repository: ShippingRepository,
    private val shipping: ShippingRow,
    private val prefs: Prefs
) : BaseViewModel<ShippingDetailContract.Event, ShippingDetailContract.State, ShippingDetailContract.Effect>(){
    init {
        setState {
            copy(shipping = this@ShippingDetailViewModel.shipping)
        }
        val selectedSort = state.sortList.find {
            it.sort == prefs.getShippingDetailSort() && it.order.value == prefs.getShippingOrderDetailOrder()
        }
        if (selectedSort!=null){
            setState {
                copy(sort = selectedSort)
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
    override fun setInitState(): ShippingDetailContract.State {
        return ShippingDetailContract.State()
    }

    override fun onEvent(event: ShippingDetailContract.Event) {
        when(event){
            ShippingDetailContract.Event.FetchPalletData -> {
                getPallets()
            }
            is ShippingDetailContract.Event.FetchPalletProducts -> {
                getPalletProducts(palletManifest = event.pallet)
            }
            is ShippingDetailContract.Event.OnChangeBarcode -> {
                setState {
                    copy(barcode = event.barcode)
                }
            }
            is ShippingDetailContract.Event.OnChangeSort -> {
                prefs.setShippingDetailSort(event.sort.sort)
                prefs.setShippingDetailOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort)
                }
                getPallets()
            }
            ShippingDetailContract.Event.OnNavBack -> {
                setEffect {
                    ShippingDetailContract.Effect.NavBack
                }
            }
            ShippingDetailContract.Event.OnReachEnd -> {
                if (state.page* ROW_COUNT<=state.palletList.size){
                    getPallets(clearList = false)
                }
            }
            ShippingDetailContract.Event.OnRefresh -> {
                getPallets(loading = Loading.REFRESHING)
            }
            is ShippingDetailContract.Event.OnSearch -> {
                setState {
                    copy(keyword = event.keyword)
                }
                getPallets(loading = Loading.SEARCHING)
            }
            is ShippingDetailContract.Event.OnSelectPallet -> {
                setState {
                    copy(selectedPallet = event.pallet)
                }
            }
            ShippingDetailContract.Event.OnProductReachEnd -> {

            }

            ShippingDetailContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            ShippingDetailContract.Event.CloseToast -> {
                setState {
                    copy(toast = "")
                }
            }
            is ShippingDetailContract.Event.OnDelete -> {
                deletePallet(event.pallet)
            }
            ShippingDetailContract.Event.OnScan -> {
                addPalletToShipping()
            }
            is ShippingDetailContract.Event.OnSelectForDelete -> {
                setState {
                    copy(selectedForDelete = event.pallet)
                }
            }
        }
    }

    private fun getPallets(loading: Loading = Loading.LOADING,clearList: Boolean = true) {
        if (state.loadingState == Loading.NONE) {
            if (clearList){
                setState {
                    copy(page = 1, palletList = emptyList(), loadingState = loading)
                }
            } else {
                setState {
                    copy(page = page + 1, loadingState = loading)
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.getShipping(shipping.shippingID)
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
                            is BaseResult.Error -> {
                                setSuspendedState {
                                    copy(error = it.message)
                                }
                            }
                            is BaseResult.Success -> {
                                setSuspendedState {
                                    copy(palletList = it.data?.palletManifests?:emptyList(), shipping = this@ShippingDetailViewModel.shipping.copy(
                                        carNumber = it.data?.carNumber,
                                        shippingStatus = it.data?.shippingStatus,
                                        shippingNumber = it.data?.shippingNumber,
                                        driverFullName = it.data?.driverFullName,
                                        driverTin = it.data?.driverTin,
                                        trailerNumber = it.data?.trailerNumber,
                                        customerName = it.data?.customerName,
                                        referenceNumber = it.data?.referenceNumber,
                                        warehouseID = it.data?.warehouseID?.toString(),
                                        date = it.data?.date,
                                        time =  it.data?.time
                                    ))
                                }
                            }
                            BaseResult.UnAuthorized -> TODO()
                        }
                    }
            }
        }
    }

    private fun getPalletProducts(clearList: Boolean = true,palletManifest: PalletManifest) {
        if (!state.isProductLoading) {
            if (clearList){
                setState {
                    copy(productPage = 1, productList = emptyList(), isProductLoading = true)
                }
            } else {
                setState {
                    copy(productPage = productPage + 1, isProductLoading = true)
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.getPalletProductList(palletManifest.palletManifestID.toString())
                    .catch {
                        setSuspendedState {
                            copy(error = it.message?:"", isProductLoading = false)
                        }
                    }
                    .collect {
                        setSuspendedState {
                            copy(isProductLoading = false)
                        }
                        when(it){
                            is BaseResult.Error -> {
                                setSuspendedState {
                                    copy(error = it.message)
                                }
                            }
                            is BaseResult.Success -> {
                                setSuspendedState {
                                    copy(productList = it.data?.rows?:emptyList())
                                }
                            }
                            BaseResult.UnAuthorized -> {

                            }
                        }
                    }
            }
        }
    }


    private fun addPalletToShipping() {
        if (state.barcode.text.isEmpty()) {
            setState {
                copy(error = "Pallet Barcode can't be empty")
            }
            return
        }
        if (!state.isScanning){
            setState {
                copy(isScanning = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.addPalletManifestToShipping(
                    shipping.shippingID,
                    state.barcode.text.trim()
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isScanning = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isScanning = false)
                    }

                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            if (it.data?.isSucceed == true){
                                setSuspendedState {
                                    copy(toast = it.data.messages.firstOrNull()?:"Added Successfully", barcode = TextFieldValue())
                                }
                                getPallets()
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> TODO()
                    }
                }
            }
        }
    }

    private fun deletePallet(palletManifest: PalletManifest){
        if (!state.isDeleting){
            setState {
                copy(isDeleting = true)
            }
            viewModelScope.launch {
                repository.removePalletManifestToShipping(
                    shipping.shippingID,
                    palletManifest.palletBarcode?:""
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isDeleting = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isDeleting = false, selectedForDelete = null)
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
                                    copy(toast = it.data.messages.firstOrNull()?:"Deleted Successfully")
                                }
                                getPallets()
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> TODO()
                    }
                }
            }
        }
    }
}
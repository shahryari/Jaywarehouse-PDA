package com.example.jaywarehouse.presentation.shipping.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.shipping.ShippingRepository
import com.example.jaywarehouse.data.shipping.models.PalletInShippingRow
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.shipping.contracts.ShippingContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ShippingViewModel(
    private val repository: ShippingRepository,
    private val prefs: Prefs,
) : BaseViewModel<ShippingContract.Event, ShippingContract.State, ShippingContract.Effect>() {

    init {
        val selectedSort = state.sortList.find {
            it.sort == prefs.getShippingSort() && it.order == Order.getFromValue(prefs.getShippingOrder())
        }
        if (selectedSort != null) {
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

    override fun setInitState(): ShippingContract.State {
        return ShippingContract.State()
    }

    override fun onEvent(event: ShippingContract.Event) {
        when (event) {
            ShippingContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            ShippingContract.Event.OnClearError -> {
                setState {
                    copy(error = "")
                }
            }

            is ShippingContract.Event.OnKeywordChange -> {
                setState {
                    copy(keyword = event.keyword)
                }
            }



            is ShippingContract.Event.OnShowAddDialog -> {
                setState {
                    copy(
                        showAddDialog = event.showAddDialog,
                        driverId = TextFieldValue(),
                        driverName = TextFieldValue(),
                        carNumber = TextFieldValue(),
                        trailerNumber = TextFieldValue(),
                        palletNumber = TextFieldValue(),
                        selectedDriver = null,
                        isDriverIdScanned = false
                    )
                }
            }

            is ShippingContract.Event.OnShowFilterList -> {
                setState {
                    copy(showFilterList = event.showFilterList)
                }
            }

            is ShippingContract.Event.OnShowPopup -> {
                setState {
                    copy(showPopup = event.show)
                }
            }

            is ShippingContract.Event.OnSortChange -> {
                prefs.setShippingSort(event.sort.sort)
                prefs.setShippingOrder(event.sort.order.value)
                setState {
                    copy(
                        sort = event.sort,
                        page = 1,
                        shippingList = emptyList(),
                        loadingState = Loading.LOADING,
                        keyword = TextFieldValue()
                    )
                }
                getShipping(state.keyword.text, sort = event.sort)
            }

            ShippingContract.Event.OnReachEnd -> {
                if (10 * state.page <= state.shippingList.size) {
                    setState {
                        copy(page = page + 1, loadingState = Loading.LOADING)
                    }
                    getShipping(page = state.page, sort = state.sort)
                }
            }

            ShippingContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, shippingList = emptyList(), loadingState = Loading.SEARCHING)
                }
                getShipping(state.keyword.text, sort = state.sort)
            }

            ShippingContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, shippingList = emptyList(), loadingState = Loading.REFRESHING)
                }
                getShipping(state.keyword.text, state.page, sort = state.sort)
            }

            ShippingContract.Event.FetchData -> {
                setState {
                    copy(page = 1, shippingList = emptyList(), loadingState = Loading.LOADING)
                }
                getShipping(state.keyword.text, state.page, sort = state.sort)
            }

            is ShippingContract.Event.OnDriverNameChange -> {
                setState {
                    copy(driverName = event.name)
                }
            }

            ShippingContract.Event.OnAddPallet -> {
                if (state.quantityPallets.isNotEmpty()){
                    createPallet(
                        18
                    )
                } else {
                    setState {
                        copy(
                            error = "list can not be empty."
                        )
                    }
                }

            }
            ShippingContract.Event.OnAddShipping -> {
                createShipping()
            }
            is ShippingContract.Event.OnCarNumberChange -> {
                setState {
                    copy(carNumber = event.number)
                }
            }
            is ShippingContract.Event.OnConfirm -> {
                confirmShipping(event.shipping.shippingID)
            }
            is ShippingContract.Event.OnCreateInvoice -> {
                createInvoice(event.shipping.shippingID)
            }
            is ShippingContract.Event.OnCreateRS -> {
                createRSInterface(event.shipping.shippingID,event.shipping.shippingNumber)
            }
            is ShippingContract.Event.OnCustomerChange -> {
                setState {
                    copy(customer = event.customer)
                }
            }
            is ShippingContract.Event.OnDriverIdChange -> {
                setState {
                    copy(driverId = event.id)
                }
            }
            ShippingContract.Event.OnNavBack -> {
                setEffect {
                    ShippingContract.Effect.NavBack
                }
            }
            is ShippingContract.Event.OnPalletNumberChange -> {
                setState {
                    copy(palletNumber = event.number)
                }
            }
            is ShippingContract.Event.OnPalletTypeChange -> {
                setState {
                    copy(palletType = event.type)
                }
            }

            is ShippingContract.Event.OnQuantityChange -> {
                setState {
                    copy(quantity = event.quantity)
                }
            }
            is ShippingContract.Event.OnRemovePallet -> {
                setState {
                    copy(createPallets = createPallets.filterNot { it == event.pallet })
                }
            }
            is ShippingContract.Event.OnRemovePalletQuantity -> {
                setState {
                    copy(quantityPallets = quantityPallets.map {
                        if (it == event.pallet){
                            it.copy(entityState = "Removed")
                        } else {
                            it
                        }
                    })
                }
            }
            ShippingContract.Event.OnScanDriverId -> {
                scanDriverId(state.driverId.text)
            }
            ShippingContract.Event.OnScanPalletBarcode -> {
                if (state.palletNumber.text.isNotEmpty()){
                    checkPalletBarcode(state.palletNumber.text)
                }
            }
            ShippingContract.Event.OnScanPalletQuantity -> {
                if (state.selectedPalletType!=null && state.selectedCustomer!=null){
                    val type = state.quantityPallets.find {
                        it.palletTypeID == state.selectedPalletType?.palletTypeID && it.customerID == state.selectedCustomer?.customerID.toString()
                    }
                    if (type==null){
                        setState {
                            copy(
                                quantityPallets = quantityPallets + PalletInShippingRow(
                                    customerID = state.selectedCustomer!!.customerID.toString(),
                                    palletTypeID = state.selectedPalletType!!.palletTypeID,
                                    palletQuantity = state.quantity.text.toIntOrNull()?:0,
                                    palletTypeTitle = state.selectedPalletType!!.palletTypeTitle,
                                    customerName = state.selectedCustomer!!.customerName,
                                    shippingID = state.shippingForPallet!!.shippingID,
                                    entityState = "Added"
                                )
                            )
                        }
                    }

                }
            }
            is ShippingContract.Event.OnTrailerNumberChange -> {
                setState {
                    copy(trailerNumber = event.number)
                }
            }

            is ShippingContract.Event.OnSelectCustomer -> {
                setState {
                    copy(selectedCustomer = event.customer)
                }
            }
            is ShippingContract.Event.OnSelectPalletType -> {
                setState {
                    copy(selectedPalletType = event.type)
                }
            }

            is ShippingContract.Event.OnShowPalletQuantitySheet -> {
                setState {
                    copy(
                        shippingForPallet = event.shipping,
                        customers = emptyList(),
                        palletTypes = emptyList(),
                        quantityPallets = emptyList(),
                        customer = TextFieldValue(),
                        palletType = TextFieldValue(),
                        quantity = TextFieldValue()
                    )
                }
                if (event.shipping!=null){
                    getPalletList(event.shipping.shippingID)
                    getCustomers(event.shipping.shippingID)
                    getPalletTypes()
                }
            }
            is ShippingContract.Event.OnShowConfirm -> {
                setState {
                    copy(confirmShipping = event.shipping)
                }
            }
            is ShippingContract.Event.OnShowInvoice -> {
                setState {
                    copy(invoiceShipping = event.shipping)
                }
            }
            is ShippingContract.Event.OnShowRs -> {
                setState {
                    copy(rsShipping = event.shipping)
                }
            }
        }
    }

    private fun scanDriverId(driverId: String) {
        setState {
            copy(isDriverIdScanned = true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDriverInfo(driverId.trim())
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"")
                    }
                }
                .collect {
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(selectedDriver = it.data)
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }


    private fun getShipping(
        keyword: String = "",
        page: Int = 1,
        sort: SortItem
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            repository.getShipping(
                keyword, page, 10, sort.sort,sort.order.value
            ).catch {
                setSuspendedState {
                    copy(error = it.message.toString(), loadingState = Loading.NONE)
                }
            }.collect {
                setSuspendedState {
                    copy(loadingState = Loading.NONE)
                }
                when (it) {
                    is BaseResult.Error -> {
                        setSuspendedState {
                            copy(error = it.message, loadingState = Loading.NONE)
                        }
                    }

                    is BaseResult.Success -> {
                        setSuspendedState {
                            copy(
                                shippingModel = it.data,
                                shippingList = shippingList + (it.data?.rows ?: emptyList()),
                                loadingState = Loading.NONE
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getPalletList(shippingId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPalletListInShipping(
                shippingId
            ).catch {
                setSuspendedState {
                    copy(error = it.message?:"")
                }
            }.collect { result ->
                when(result){
                    is BaseResult.Error -> {
                        setSuspendedState {
                            copy(error = result.message)
                        }
                    }
                    is BaseResult.Success -> {
                        val list = result.data?.rows?.map {
                            if (it.entityState==null){
                                it.copy(entityState = "Added")
                            } else {
                                it
                            }
                        }
                        setSuspendedState {
                            copy(quantityPallets = list ?: emptyList())
                        }
                    }
                    BaseResult.UnAuthorized -> {}
                }
            }
        }
    }

    private fun checkPalletBarcode(
        barcode: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.palletBarcodeCheck(barcode)
                .catch { setSuspendedState {
                    copy(error = it.message?:"")
                } }
                .collect {
                    setSuspendedState {
                        copy(palletNumber = TextFieldValue())
                    }
                    when(it){

                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            if (it.data!=null)setSuspendedState {
                                copy(createPallets = createPallets+it.data)
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }

    private fun createShipping() {
        if (state.driverId.text.isEmpty() || state.driverName.text.isEmpty() || state.carNumber.text.isEmpty() || state.trailerNumber.text.isEmpty()) {
            setState {
                copy(error = "Please Fill Driver Info.")
            }
            return
        }
        if (state.createPallets.isEmpty()) {
            setState {
                copy(error = "Please add some pallet number.")
            }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.createShipping(
                state.createPallets,
                state.driverName.text,
                state.driverId.text,
                state.carNumber.text,
                state.trailerNumber.text
            ).catch {
                setSuspendedState {
                    copy(error = it.message?:"")
                }
            }.collect {
                when(it){
                    is BaseResult.Error -> {
                        setSuspendedState {
                            copy(error = it.message)
                        }
                    }
                    is BaseResult.Success -> {
                        setSuspendedState {
                            copy(showAddDialog = false, shippingList = emptyList(), page = 1, loadingState = Loading.LOADING)
                        }
                        getShipping(state.keyword.text,state.page,state.sort)
                    }
                    BaseResult.UnAuthorized -> {}
                }
            }
        }
    }

    private fun createPallet(
        warehouseId: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.submitPalletShipping(
                state.quantityPallets,
                warehouseId
            ).catch {
                setSuspendedState {
                    copy(error = it.message?:"")
                }
            }.collect {
                when(it){
                    is BaseResult.Error -> {
                        setSuspendedState {
                            copy(error = it.message)
                        }
                    }
                    is BaseResult.Success -> {
                        setSuspendedState {
                            copy(
                                shippingForPallet = null,
                                toast = it.data?.messages?.firstOrNull()?:"Added Successfully",
                                shippingList = emptyList(),
                                page = 1,
                                loadingState = Loading.LOADING
                            )
                        }
                        getShipping(state.keyword.text,state.page,state.sort)
                    }
                    BaseResult.UnAuthorized -> {

                    }
                }
            }
        }
    }

    private fun confirmShipping(shippingId: Int) {
        viewModelScope.launch {
            repository.confirmShipping(shippingId)
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"", confirmShipping = null)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(confirmShipping = null)
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
                                    confirmShipping = null,
                                    toast = it.data?.messages?.firstOrNull()?:"Confirmed Successfully",
                                    shippingList = emptyList(),
                                    page = 1,
                                    loadingState = Loading.LOADING
                                )
                            }
                            getShipping(state.keyword.text,state.page,state.sort)
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }

        }
    }

    private fun createInvoice(
        shippingId: Int
    ) {
       viewModelScope.launch(Dispatchers.IO) {
           repository.createInvoice(shippingId)
               .catch {
                   setSuspendedState { copy(error = it.message?:"", invoiceShipping = null) }
               }
               .collect {
                   setSuspendedState {
                       copy(invoiceShipping = null)
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
                                   invoiceShipping = null,
                                   toast = it.data?.messages?.firstOrNull()?:"Invoice Created Successfully",
                                   shippingList = emptyList(),
                                   page = 1,
                                   loadingState = Loading.LOADING
                               )
                           }
                           getShipping(state.keyword.text,state.page,state.sort)
                       }
                       BaseResult.UnAuthorized -> {}
                   }
               }
       }
    }

    private fun createRSInterface(
        shippingId: Int,
        shippingNumber: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createRSInterface(shippingId,shippingNumber)
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"", rsShipping = null)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(rsShipping = null)
                    }
                    when(it){
                        is BaseResult.Error ->  {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(
                                    rsShipping = null,
                                    toast = it.data?.messages?.firstOrNull()?:"RS Created Successfully",
                                    shippingList = emptyList(),
                                    page = 1,
                                    loadingState = Loading.LOADING
                                )
                            }
                            getShipping(state.keyword.text,state.page,state.sort)
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }

    private fun getCustomers(shippingId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getShippingCustomers(shippingId)
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"")
                    }
                }
                .collect {
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(customers = it.data?.rows?: emptyList())
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }

    private fun getPalletTypes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getShippingPalletTypes()
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"")
                    }
                }
                .collect {
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(palletTypes = it.data?.rows?: emptyList())
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }



}
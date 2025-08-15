package com.linari.presentation.shipping.viewmodels

import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.common.utils.validatePallet
import com.linari.data.shipping.ShippingRepository
import com.linari.data.shipping.models.ShippingDetailListOfPalletRow
import com.linari.data.shipping.models.ShippingPalletManifestRow
import com.linari.data.shipping.models.ShippingRow
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.shipping.contracts.ShippingContract
import com.linari.presentation.shipping.contracts.ShippingContract.Effect.*
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
        setState {
            copy(warehouse = prefs.getWarehouse())
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
                        isDriverIdScanned = false,
                        createPallets = emptyList()
                    )
                }
                getShippingPalletManifestList()
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
                        keyword = ""
                    )
                }
                getShipping(state.keyword, sort = event.sort)
            }

            ShippingContract.Event.OnReachEnd -> {
                if (ROW_COUNT * state.page <= state.shippingList.size) {
                    setState {
                        copy(page = page + 1, loadingState = Loading.LOADING)
                    }
                    getShipping(page = state.page, sort = state.sort)
                }
            }

            is ShippingContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, shippingList = emptyList(), loadingState = Loading.SEARCHING, keyword = event.keyword)
                }
                getShipping(state.keyword, sort = state.sort)
            }

            ShippingContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, shippingList = emptyList(), loadingState = Loading.REFRESHING)
                }
                getShipping(state.keyword, state.page, sort = state.sort)
            }

            ShippingContract.Event.FetchData -> {
                setState {
                    copy(page = 1, shippingList = emptyList(), loadingState = Loading.LOADING)
                }
                getShipping(state.keyword, state.page, sort = state.sort)
            }

            is ShippingContract.Event.OnDriverNameChange -> {
                setState {
                    copy(driverName = event.name)
                }
            }

            is ShippingContract.Event.OnConfirmPallet -> {
                createPallet(event.shipping.shippingID)
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
            is ShippingContract.Event.OnRollbackShipping -> {
                rollbackShipping(event.shipping.shippingID)
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
                if (event.id.text.isEmpty()){
                    setState {
                        copy(
                            driverName = TextFieldValue(),
                            carNumber = TextFieldValue(),
                            trailerNumber = TextFieldValue(),
                            selectedDriver = null,
                        )
                    }
                }
            }
            ShippingContract.Event.OnNavBack -> {
                setEffect {
                    NavBack
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
                deleteShippingPallet(event.pallet.shippingID,event.pallet.shippingPalletID)
            }
            ShippingContract.Event.OnScanDriverId -> {
                scanDriverId(state.driverId.text)
            }
            ShippingContract.Event.OnScanPalletBarcode -> {
                addPalletBarcode(state.palletNumber.text)
            }
            ShippingContract.Event.OnScanPalletQuantity -> {
                if(state.shippingForPallet!=null){
                    addShippingPallet(state.shippingForPallet!!.shippingID)
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
                        palletStatusList = emptyList(),
                        quantityPallets = emptyList(),
                        customer = TextFieldValue(),
                        palletType = TextFieldValue(),
                        palletStatus = TextFieldValue(),
                        quantity = TextFieldValue(),
                        selectedPalletType = null,
                        selectedPalletStatus = null
                    )
                }
                if (event.shipping!=null){
                    getPalletList(event.shipping.shippingID)
                    getCustomers(event.shipping.shippingID)
                    getPalletTypes()
                    getPalletStatuses()
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
            is ShippingContract.Event.OnShowRollbackConfirm -> {
                setState {
                    copy(showRollbackConfirm = event.shipping)
                }
            }

            is ShippingContract.Event.OnPalletStatusChange -> {
                setState {
                    copy(
                        palletStatus = event.status
                    )
                }
            }
            is ShippingContract.Event.OnSelectPalletStatus -> {
                setState {
                    copy(selectedPalletStatus = event.status)
                }
            }

            is ShippingContract.Event.OnSelectPallet -> {
                setState {
                    copy(
                        selectedPallet = event.pallet,
                        palletProducts = emptyList()
                    )
                }

            }

            is ShippingContract.Event.OnShowAddPallet -> {
                setState {
                    copy(
                        showAddPallet = event.show,
                        selectedCustomer = null,
                        customer = TextFieldValue(),
                        quantity = TextFieldValue(),
                    )
                }
                if (event.show && state.shippingForPallet!=null){
                    getCustomers(state.shippingForPallet!!.shippingID)
                    getPalletTypes()
                    getPalletStatuses()
                }
            }
            is ShippingContract.Event.OnShowUpdatePallet -> {
                val quantity = event.show?.palletQuantity?.toString()?:""
                setState {
                    copy(
                        showUpdatePallet = event.show,
                        quantity = TextFieldValue(quantity,TextRange(0,quantity.length))
                    )
                }
            }
            is ShippingContract.Event.OnUpdatePallet -> {
                updateShippingPallet(event.pallet.shippingID,event.pallet.shippingPalletID)
            }

            is ShippingContract.Event.OnShowConfirmDeletePallet -> {
                setState {
                    copy(showConfirmDeletePallet = event.show)
                }
            }

            is ShippingContract.Event.OnSelectShipping -> {
                setEffect {
                    NavToShippingDetail(event.shipping)
                }
            }

            is ShippingContract.Event.FetchPalletProducts -> {
                getPalletProducts(event.palletManifest)
            }

            is ShippingContract.Event.OnShowStatusList -> {
                setState {
                    copy(showStatusList = event.show)
                }
            }
            is ShippingContract.Event.OnShowTypeList -> {
                setState {
                    copy(showTypeList = event.show)
                }
            }

            is ShippingContract.Event.CheckHasPallet -> {
                getCustomerPalletIsNotInShipping(shipping = event.shipping)
            }

            is ShippingContract.Event.ShowConfirmOfPalletConfirm -> {
                setState {
                    copy(showConfirmOfPalletConfirm = event.shipping)
                }
            }

            is ShippingContract.Event.OnShowCustomerList -> {
                setState {
                    copy(showCustomerList = event.show)
                }
            }
        }
    }

    private fun scanDriverId(driverId: String) {
        if (driverId.isEmpty()) {
            setState {
                copy(error = "Driver Id can not be empty")
            }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDriverInfo(driverId.trim())
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isDriverIdScanned = true)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(isDriverIdScanned = true)
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
                                    selectedDriver = it.data,
                                    driverName = TextFieldValue(it.data?.fullName?:""),
                                    carNumber = TextFieldValue(it.data?.carNumber?:""),
                                    trailerNumber = TextFieldValue(it.data?.trailerNumber?:""),
                                )
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

            repository.getShippings(
                keyword, warehouseID = prefs.getWarehouse()!!.id,page, ROW_COUNT, sort.sort,sort.order.value
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
                                warehouseID = it.data?.warehouseID?:"",
                                rowCount = it.data?.total?:0
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
                        val list = result.data?.rows
                        setSuspendedState {
                            copy(quantityPallets = list ?: emptyList())
                        }
                    }
                    BaseResult.UnAuthorized -> {}
                }
            }
        }
    }


    private fun getShippingPalletManifestList(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getShippingPalletManifestList(
                prefs.getWarehouse()!!.id
            )
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
                                copy(shippingPalletManifestList = it.data?.rows ?: emptyList())
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }

    private fun getPalletProducts(palletManifest: ShippingPalletManifestRow) {
        if (!state.isProductLoading) {
            setState {
                copy(isProductLoading = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.getPalletProductList(palletManifest.palletManifestId.toString())
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
                                    copy(palletProducts = it.data?.rows?.map { p->
                                        ShippingDetailListOfPalletRow(
                                            isWeight = p.isWeight,
                                            quantity = p.quantity,
                                            productName = p.productName,
                                            productCode = p.productCode,
                                            productBarcodeNumber = p.barcode,
                                            referenceNumberPO = p.referenceNumberPO,
                                            referenceNumberLPO = p.referenceNumberLPO,
                                            shippingDetailID = 0,
                                            expireDate = p.expireDate,
                                            batchNumber = p.batchNumber
                                        )
                                    }?:emptyList())
                                }
                            }
                            BaseResult.UnAuthorized -> {

                            }
                        }
                    }
            }
        }
    }



    private fun addPalletBarcode(
        barcode: String
    ) {
        if (barcode.isEmpty()) {
            setState {
                copy(error = "Pallet Barcode can not be empty")
            }
            return
        }
        if (prefs.getValidatePallet()){
            if (!validatePallet(barcode,"")) {
                setState {
                    copy(error = "Invalid Pallet Barcode")
                }
                return
            }
        }
        if (!state.isChecking){
            setState {
                copy(isChecking = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.addPalletToShipping(barcode)
                    .catch { setSuspendedState {
                        copy(error = it.message?:"", isChecking = false)
                    } }
                    .collect { result ->
                        setSuspendedState {
                            copy(isChecking = false)
                        }
                        when(result){
                            is BaseResult.Error -> {
                                setSuspendedState {
                                    copy(error = result.message)
                                }
                            }
                            is BaseResult.Success -> {
                                if (result.data?.isSucceed == true){
                                    try {
                                        val pallet = result.data.returnValue as ShippingPalletManifestRow

                                        if (pallet!=null){
                                            if (state.createPallets.any { it.palletManifestId == pallet.palletManifestId }){
                                                setState {
                                                    copy(error = "pallet already added to the list")
                                                }
                                                return@collect
                                            }
                                            setSuspendedState {
                                                copy(createPallets =createPallets + pallet, palletNumber = TextFieldValue() )
                                            }

                                        } else {
                                            setSuspendedState {
                                                copy(error = "something is wrong")
                                            }
                                        }
                                    }catch (e: Exception) {
                                        Log.e("jaywarehouse","add pallet error",e)
                                    }

                                } else {
                                    setSuspendedState {
                                        copy(error = result.data?.messages?.firstOrNull()?:"")
                                    }
                                }
                            }
                            BaseResult.UnAuthorized -> {}
                        }
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
        if (!state.isShipping){
            setState {
                copy(isShipping = true)
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
                        copy(error = it.message?:"", isShipping = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isShipping = false)
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
                                    copy(
                                        showAddDialog = false,
                                        shippingList = emptyList(),
                                        page = 1,
                                        loadingState = Loading.LOADING,
                                        toast = it.data?.messages?.firstOrNull() ?: "Shipping created successfully"
                                    )
                                }
                                getShipping(state.keyword,state.page,state.sort)
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
            }
        }
    }

    private fun createPallet(
        shippingId: Int
    ) {
        if (!state.isCreatingPallet){
            setState {
                copy(isCreatingPallet = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.shippingPalletConfirm(
                    shippingId
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isCreatingPallet = false, showConfirmOfPalletConfirm = null)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isCreatingPallet = false, showConfirmOfPalletConfirm = null)
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
                                    copy(
                                        shippingForPallet = null,
                                        toast = it.data.messages.firstOrNull()?:"Added Successfully",
                                        shippingList = emptyList(),
                                        page = 1,
                                        loadingState = Loading.LOADING
                                    )
                                }
                                getShipping(state.keyword,state.page,state.sort)
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
            }
        }
    }

    private fun confirmShipping(shippingId: Int) {
        if (!state.isConfirming){
            setState {
                copy(isConfirming = true)
            }
            viewModelScope.launch {
                repository.confirmShipping(shippingId)
                    .catch {
                        setSuspendedState {
                            copy(error = it.message?:"", isConfirming = false, confirmShipping = null)
                        }
                    }
                    .collect {
                        setSuspendedState {
                            copy(isConfirming = false,confirmShipping = null)
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
                                            confirmShipping = null,
                                            toast = it.data?.messages?.firstOrNull()?:"Confirmed Successfully",
                                            shippingList = emptyList(),
                                            page = 1,
                                            loadingState = Loading.LOADING
                                        )
                                    }
                                    getShipping(state.keyword,state.page,state.sort)
                                } else {
                                    setSuspendedState {
                                        copy(error = it.data?.messages?.firstOrNull()?:"")
                                    }
                                }
                            }
                            BaseResult.UnAuthorized -> {}
                        }
                    }

            }
        }
    }

    private fun createInvoice(
        shippingId: Int
    ) {
        if (!state.isCreatingInvoice){
            setState {
                copy(isCreatingInvoice = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.createInvoice(shippingId)
                    .catch {
                        setSuspendedState { copy(error = it.message?:"", isCreatingInvoice = false, invoiceShipping = null) }
                    }
                    .collect {
                        setSuspendedState {
                            copy(isCreatingInvoice = false,invoiceShipping = null)
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
                                            invoiceShipping = null,
                                            toast = it.data?.messages?.firstOrNull()?:"Invoice Created Successfully",
                                            shippingList = emptyList(),
                                            page = 1,
                                            loadingState = Loading.LOADING
                                        )
                                    }
                                    getShipping(state.keyword,state.page,state.sort)
                                } else {
                                    setSuspendedState {
                                        copy(error = it.data?.messages?.firstOrNull()?:"")
                                    }
                                }
                            }
                            BaseResult.UnAuthorized -> {}
                        }
                    }
            }
        }
    }

    private fun createRSInterface(
        shippingId: Int,
        shippingNumber: String
    ) {
        if (!state.isCreatingRs){
            setState {
                copy(isCreatingRs = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.createRSInterface(shippingId,shippingNumber)
                    .catch {
                        setSuspendedState {
                            copy(error = it.message?:"", isCreatingRs = false, rsShipping = null)
                        }
                    }
                    .collect {
                        setSuspendedState {
                            copy(isCreatingRs = false, rsShipping = null)
                        }
                        when(it){
                            is BaseResult.Error ->  {
                                setSuspendedState {
                                    copy(error = it.message)
                                }
                            }
                            is BaseResult.Success -> {
                                if (it.data?.isSucceed == true){
                                    setSuspendedState {
                                        copy(
                                            rsShipping = null,
                                            toast = it.data?.messages?.firstOrNull()?:"RS Created Successfully",
                                            shippingList = emptyList(),
                                            page = 1,
                                            loadingState = Loading.LOADING
                                        )
                                    }
                                    getShipping(state.keyword,state.page,state.sort)
                                } else {
                                    setSuspendedState {
                                        copy(error = it.data?.messages?.firstOrNull()?:"")
                                    }
                                }
                            }
                            BaseResult.UnAuthorized -> {}
                        }
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
                                copy(
                                    palletTypes = it.data?.rows?: emptyList(),
                                    selectedPalletType = it.data?.rows?.find { type -> type.palletTypeID == 1 }
                                )
                            }
                        }

                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }

    private fun getPalletStatuses() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getShippingPalletStatus()
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
                                copy(
                                    palletStatusList = it.data?.rows?: emptyList(),
                                    selectedPalletStatus = it.data?.rows?.find { status -> status.palletStatusID == 1 }
                                )
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }


    private fun addShippingPallet(shippingId: Int){

        if (state.selectedCustomer == null) {
            setState {
                copy(
                    error = "Customer not selected."
                )
            }
            return
        }
        if (state.selectedPalletType == null){
            setState {
                copy(
                    error = "Pallet Type not selected."
                )
            }
            return
        }
        if (state.selectedPalletStatus == null){
            setState {
                copy(
                    error = "Pallet Status not selected."
                )
            }
            return
        }
        val quantity = state.quantity.text.toDoubleOrNull()

        if (state.quantityPallets.any {it.customerID == state.selectedCustomer?.customerID.toString() && it.palletTypeID == state.selectedPalletType?.palletTypeID && it.palletStatusID == state.selectedPalletStatus?.palletStatusID}){
            setState {
                copy(error = "Pallet already exist.")
            }
            return
        }
        if (state.quantity.text.isEmpty() || quantity == null){
            setState {
                copy(
                    error = "Quantity not entered."
                )
            }
            return
        }
        if (quantity<=0){
            setState {
                copy(
                    error = "Quantity must be greater then zero"
                )
            }
            return
        }

        if (!state.isAddingPallet){
            setState {
                copy(isAddingPallet = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.createShippingPallet(
                    shippingID = shippingId,
                    customerID = state.selectedCustomer!!.customerID,
                    palletTypeID = state.selectedPalletType!!.palletTypeID,
                    palletStatusID = state.selectedPalletStatus!!.palletStatusID,
                    palletQuantity = quantity
                ).catch {
                    setSuspendedState {
                        copy(isAddingPallet = false,error = it.message?:"")
                    }
                }.collect {
                    setSuspendedState {
                        copy(isAddingPallet = false)
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
                                    copy(
                                        toast = "Added Successfully",
                                        showAddPallet = false
                                    )
                                }
                                getPalletList(shippingId)
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
            }
        }
    }

    private fun updateShippingPallet(shippingId: Int,shippingPalletID: Int){
        val quantity = state.quantity.text.toDoubleOrNull()
        if (quantity == null){
            setState {
                copy(
                    error = "Quantity not entered."
                )
            }
            return
        }
        if (quantity<=0){
            setState {
                copy(
                    error = "Quantity must be greater then zero"
                )

            }
            return
        }
        if (!state.isUpdatingPallet){
            setState {
                copy(isUpdatingPallet = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateShippingPallet(
                    shippingPalletID = shippingPalletID,
                    quantity = quantity
                ).catch {
                    setSuspendedState {
                        copy(isUpdatingPallet = false,error = it.message?:"")
                    }
                }.collect {
                    setSuspendedState {
                        copy(isUpdatingPallet = false)
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
                                    copy(showUpdatePallet = null)
                                }
                                getPalletList(shippingId)
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
            }
        }
    }

    private fun deleteShippingPallet(shippingId: Int,shippingPalletID: Int){
        if (!state.isDeletingPallet){
            setState {
                copy(isDeletingPallet = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteShippingPallet(
                    shippingPalletID = shippingPalletID
                ).catch {
                    setSuspendedState {
                        copy(isDeletingPallet = false,error = it.message?:"", showConfirmDeletePallet = null)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isDeletingPallet = false, showConfirmDeletePallet = null)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            if (it.data?.isSucceed == true){
                                getPalletList(shippingId)
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
            }
        }
    }

    private fun rollbackShipping(shippingId: Int) {
        if (!state.isRollingBack){
            setState {
                copy(isRollingBack = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.rollbackShipping(
                    shippingId = shippingId
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isRollingBack = false, showRollbackConfirm = null)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isRollingBack = false, showRollbackConfirm = null)
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
                                    copy(toast = it.data.messages.firstOrNull()?:"UnConfirm completed successfully.", shippingList = emptyList(),page = 1, loadingState = Loading.LOADING)
                                }
                                getShipping(state.keyword,state.page,state.sort)
                            } else{
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
            }
        }
    }



    private fun getCustomerPalletIsNotInShipping(shipping: ShippingRow){
        setState {
            copy(palletNotInShipping = emptyList())
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCustomerPalletIsNotInShipping(shipping.shippingID)
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"")
                    }
                }.collect {
                    when(it){
                        is BaseResult.Error<*> -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(palletNotInShipping = it.data?.rows?:emptyList(), invoiceShipping = shipping)
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
        }
    }
    
}
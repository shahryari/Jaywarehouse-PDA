package com.linari.presentation.checking.viewModels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.checking.CheckingRepository
import com.linari.data.checking.models.CheckingListGroupedRow
import com.linari.data.checking.models.CheckingListRow
import com.linari.data.checking.models.PalletStatusModel
import com.linari.data.checking.models.PalletStatusRow
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.common.utils.validatePallet
import com.linari.presentation.checking.contracts.CheckingDetailContract
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CheckingDetailViewModel(
    private val repository: CheckingRepository,
    private val prefs: Prefs,
    private val row: CheckingListGroupedRow,
) : BaseViewModel<CheckingDetailContract.Event,CheckingDetailContract.State,CheckingDetailContract.Effect>(){
    init {
        val selectedSort = state.sortList.find {
            it.sort == prefs.getCheckingDetailSort() && it.order == Order.getFromValue(prefs.getCheckingDetailOrder())
        }
        if (selectedSort!=null) {
            setState {
                copy(sort = selectedSort)
            }
        }
        setState {
            copy(
                checkRow = row,
                locationBase = prefs.getWarehouse()?.locationBase == true,
                hasPickCancel = prefs.getHasPickCancel(),
                enableTransferOnPickCancel = prefs.getWarehouse()?.enableTransferOnPickCancel == true,
                onPickCancelLocationCode = prefs.getWarehouse()?.onPickCancelLocationCode ?:"",
                warehouse = prefs.getWarehouse()
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
        getCheckings()
    }

    override fun setInitState(): CheckingDetailContract.State {
        return CheckingDetailContract.State()
    }

    override fun onEvent(event: CheckingDetailContract.Event) {
        when(event){
            is CheckingDetailContract.Event.OnChangeBarcode -> {
                setState {
                    copy(barcode = event.barcode)
                }
                getPalletInfo(event.barcode.text)
            }
            CheckingDetailContract.Event.OnNavBack -> {
                setEffect {
                    CheckingDetailContract.Effect.NavBack
                }
            }
            CheckingDetailContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            is CheckingDetailContract.Event.OnSelectCheck -> {
                setState {
                    copy(
                        selectedChecking = event.checking,
                        count = TextFieldValue(),
                        barcode = TextFieldValue(),
                        selectedPalletType = null,
                        selectedPalletStatus = null
                    )
                }
                if (event.checking!=null){
                    getPalletTypeList()
                    getPalletStatusList()
                    getPalletMask(event.checking.warehouseID.toString())
                }
            }

            CheckingDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            CheckingDetailContract.Event.OnReachEnd -> {
                if (ROW_COUNT *state.page<=state.checkingList.size){
                    setState {
                        copy(page = state.page+1)
                    }
                    getCheckings()
                }
            }

            CheckingDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, checkingList = emptyList())
                }
                getCheckings(loading = Loading.REFRESHING)
            }
            is CheckingDetailContract.Event.OnChangeLocation -> {
                setState {
                    copy(count = event.location)
                }
            }
            is CheckingDetailContract.Event.OnCompleteChecking -> {
                completeChecking(event.checking,state.count.text.trim().toDoubleOrNull(), state.barcode.text.trim())
            }
//            is CheckingDetailContract.Event.OnChangeKeyword -> {
//                setState {
//                    copy(keyword = event.keyword)
//                }
//            }
            is CheckingDetailContract.Event.OnSearch -> {
                setState {
                    copy(checkingList = emptyList(), page = 1, keyword = event.keyword)
                }
                getCheckings(loading = Loading.SEARCHING)
            }
            is CheckingDetailContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }
            is CheckingDetailContract.Event.OnSortChange -> {
                prefs.setCheckingDetailSort(event.sortItem.sort)
                prefs.setCheckingDetailOrder(event.sortItem.order.value)
                setState {
                    copy(sort = event.sortItem, page = 1, checkingList = emptyList())
                }
                getCheckings()
            }

            is CheckingDetailContract.Event.OnSelectPalletStatus -> {
                setState {
                    copy(selectedPalletStatus = event.palletStatus)
                }
            }
            is CheckingDetailContract.Event.OnSelectPalletType -> {
                setState {
                    copy(selectedPalletType = event.palletType)
                }
            }
            is CheckingDetailContract.Event.ShowStatusList -> {
                setState {
                    copy(showStatusList = event.show)
                }
            }
            is CheckingDetailContract.Event.ShowTypeList -> {
                setState {
                    copy(showTypeList = event.show)
                }
            }

            is CheckingDetailContract.Event.OnCancelChecking -> {
                cancelPicking(event.checking)
            }
            is CheckingDetailContract.Event.OnChangeCancelLocation -> {
                setState {
                    copy(cancelLocation = event.value)
                }
            }
            is CheckingDetailContract.Event.OnChangeCancelQuantity -> {
                setState {
                    copy(cancelQuantity = event.value)
                }
            }
            is CheckingDetailContract.Event.OnChangeIsDamaged -> {
                setState {
                    copy(
                        isDamaged = event.isDamaged,
                    )
                }
                if (event.isDamaged && state.onPickCancelLocationCode.isNotEmpty() && state.enableTransferOnPickCancel) {
                    setState {
                        copy(cancelLocation = TextFieldValue(state.onPickCancelLocationCode))
                    }
                } else {
                    setState {
                        copy(cancelLocation = TextFieldValue(state.selectedForCancel?.locationCode?:""))
                    }
                }
            }
            is CheckingDetailContract.Event.SelectForCancel -> {
                setState {
                    copy(
                        selectedForCancel = event.checking,
                        cancelQuantity = TextFieldValue(),
                        cancelLocation = TextFieldValue(event.checking?.locationCode?:""),
                        isDamaged = false
                    )
                }
            }
        }
    }

    private fun getPalletInfo(barcode: String){
        val pallet = "${state.palletMask}-$barcode"
        if (validatePallet(pallet,state.palletMask)){
            viewModelScope.launch(Dispatchers.IO) {
                repository.getPalletManifestInfo(pallet)
                    .catch {
                        setSuspendedState {
                            copy(statusLock = false, typeLock = false)
                        }
                    }
                    .collect {result->
                        if (result is BaseResult.Success && result.data?.hasPallet == true){
                            setSuspendedState {
                                copy(
                                    selectedPalletStatus = palletStatusList.find { it.palletStatusID == result.data.palletStatusID},
                                    statusLock = result.data.palletStatusID !=null,
                                    selectedPalletType = palletTypeList.find { it.palletTypeID == result.data.palletTypeID },
                                    typeLock = result.data.palletTypeID !=null,
                                )
                            }
                        } else {
                            setSuspendedState {
                                copy(statusLock = false, typeLock = false)
                            }
                        }
                    }
            }
        } else {
            setState {
                copy(
                    statusLock = false,
                    typeLock =  false
                )
            }
        }
    }


    private fun completeChecking(
        checking: CheckingListRow,
        count: Double?,
        barcode: String
    ) {
        if (barcode.isEmpty()){
            setState {
                copy(error = "Please fill pallet barcode.")
            }
            return
        }
        val palletBarcode = "${state.palletMask}-$barcode"

        if (count == null){
            setState {
                copy(error = "Quantity can not be empty")
            }
            return
        }
        if (prefs.getValidatePallet()) {
            if (!validatePallet(palletBarcode,state.palletMask)) {
                setState {
                    copy(error = "The Pallet Number must match ${state.palletMask}-yyMMdd-xxx")
                }
                return
            }
        }

        if (state.selectedPalletStatus==null){
            setState {
                copy(error = "Pallet Status not selected.")
            }
            return
        }

        if (state.selectedPalletType==null){
            setState {
                copy(error = "Pallet type not selected")
            }
            return
        }

        if (state.selectedChecking!=null){
            setState {
                copy(onSaving = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.checking(
                    checking.isCrossDock,
                    count,
                    checking.customerID.toString(),
                    checking.checkingID.toString(),
                    palletBarcode,
                    state.selectedPalletStatus!!.palletStatusID,
                    state.selectedPalletType!!.palletTypeID
                ).catch {
                        setState {
                            copy(
                                error = it.message ?: "",
                                onSaving = false
                            )
                        }
                    }
                    .collect { result ->
                        setSuspendedState {
                            copy(onSaving = false)
                        }
                        when(result){
                            is BaseResult.Success -> {
                                if (result.data?.isSucceed == true) {
                                    setSuspendedState {
                                        copy(
                                            count = TextFieldValue(),
                                            barcode = TextFieldValue(),
                                            selectedPalletType = palletTypeList.find { it.palletTypeID == 1 },
                                            selectedPalletStatus = palletStatusList.find { it.palletStatusID == 1 },
                                            checkingList = emptyList(),
                                            page = 1,
                                            selectedChecking = null,
                                            toast = result.data.messages.firstOrNull() ?: "Completed successfully",
                                        )
                                    }
                                    getCheckings(checking = checking.copy(checkingID = result.data.entityID?.toIntOrNull()?:checking.checkingID))
                                } else {
                                    setSuspendedState {
                                        copy(error = result.data?.messages?.firstOrNull()?:"Failed")
                                    }
                                }
                            }
                            is BaseResult.Error -> {
                                setState {
                                    copy(
                                        error = result.message,
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
            }
        }
    }


    private fun getCheckings(loading: Loading = Loading.LOADING,checking: CheckingListRow? = null) {
        if (state.loadingState == Loading.NONE){
            setState {
                copy(loadingState = loading)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.getCheckingList(
                    customerId = row.customerID.toString(),
                    warehouseID = prefs.getWarehouse()!!.id,
                    keyword = state.keyword,
                    sort = state.sort.sort,
                    page = state.page,
                    order = state.sort.order.value
                )
                    .catch {
                        setState {
                            copy(
                                error = it.message ?: "",
                                loadingState = Loading.NONE
                            )
                        }
                    }
                    .collect {
                        setSuspendedState {
                            copy(loadingState = Loading.NONE)
                        }
                        when(it){
                            is BaseResult.Success -> {
                                val list = state.checkingList + (it.data?.rows ?: emptyList())
                                setState {
                                    copy(
                                        checkingList = list,
                                        rowCount = it.data?.total?:0
                                    )
                                }
                                val selected = list.find { l -> l.checkingID == checking?.checkingID}
                                if (checking!=null && selected != null && prefs.getEnableAutoOpenChecking()){
                                    setState {
                                        copy(selectedChecking = selected)
                                    }
                                }
                                if (loading != Loading.SEARCHING && list.isEmpty()){
                                    setEffect {
                                        CheckingDetailContract.Effect.NavBack
                                    }
                                }
                            }
                            is BaseResult.Error -> {
                                setState {
                                    copy(
                                        error = it.message,
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
            }
        }
    }

    fun getPalletTypeList() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPalletTypes()
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"")
                    }
                }
                .collect { result ->
                    when(result){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = result.message)
                            }
                        }
                        is BaseResult.Success -> {
                            val list = result.data?.rows?:emptyList()
                            val selected = list.find { it.palletTypeID == 1 }
                            setSuspendedState {
                                copy(
                                    palletTypeList = list,
                                    selectedPalletType = selected
                                )
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
        }
    }

    fun getPalletStatusList() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPalletStatuses()
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"")
                    }
                }
                .collect {result ->
                    when(result){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = result.message)
                            }
                        }
                        is BaseResult.Success -> {
                            val list = result.data?.rows?:emptyList()
                            val selected = list.find { it.palletStatusID == 1 }
                            setSuspendedState {
                                copy(
                                    palletStatusList = list,
                                    selectedPalletStatus = selected
                                )
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
        }
    }

    private fun getPalletMask(
        warehouseID: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPalletMask(warehouseID)
                .catch {  }
                .collect {
                    when(it){
                        is BaseResult.Error -> {}
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(palletMask = it.data?.palletMaskAbbreviation ?:"")
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }

    private fun cancelPicking(checking: CheckingListRow) {
        if (state.cancelQuantity.text.isEmpty()){
            setState {
                copy(error = "Please fill quantity")
            }
            return
        }
        val quantity = state.cancelQuantity.text.toDoubleOrNull()?:0.0
        if (quantity<=0){
            setState {
                copy(error = "Quantity must be greater than 0")
            }
            return
        }
        if (state.locationBase) {
            if (state.cancelLocation.text.isEmpty()) {
                setState {
                    copy(error = "Please fill location")
                }
                return
            }
        }
        if (!state.isCanceling){
            setState {
                copy(isCanceling = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.cancelPicking(
                    checking.checkingID,
                    quantity,
                    if (state.isDamaged && state.enableTransferOnPickCancel) state.onPickCancelLocationCode else state.cancelLocation.text,
                    state.isDamaged
                )
                    .catch {
                        setSuspendedState {
                            copy(error = it.message?:"", isCanceling = false)
                        }
                    }
                    .collect {
                        setSuspendedState {
                            copy(isCanceling = false)
                        }
                        when(it){
                            is BaseResult.Success -> {
                                if (it.data?.isSucceed == true){
                                    setState {
                                        copy(
                                            cancelQuantity = TextFieldValue(),
                                            cancelLocation = TextFieldValue(),
                                            isDamaged = false,
                                            selectedForCancel = null,
                                            toast = it.data.message?:"Cancel completed successfully.",
                                            checkingList = emptyList(),
                                            page = 1
                                        )
                                    }
                                    getCheckings()
                                } else {
                                    setState {
                                        copy(error = it.data?.message?:"something went wrong")
                                    }
                                }
                            }
                            is BaseResult.Error -> {
                                setState {
                                    copy(error = it.message)
                                }
                            }
                            else -> {}
                        }
                    }
            }
        }
    }
}
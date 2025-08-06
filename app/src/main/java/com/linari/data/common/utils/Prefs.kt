package com.linari.data.common.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.linari.data.auth.models.AccessPermissionModel
import com.linari.data.auth.models.WarehouseModel
import com.linari.data.common.modules.dataStore
import com.linari.presentation.common.utils.Order
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class Prefs(private val context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE)

    val lockKeyboardKey = booleanPreferencesKey("lockKeyboardKey")
    val trackingKey = booleanPreferencesKey("trackingKey")
    val labelsKey = stringPreferencesKey("labels")
    //
    suspend fun setLabels(labels: String) {
        context.dataStore.edit {
            it[labelsKey] = labels.ifEmpty { "{}" }
        }
    }

    fun getLabels() : Flow<String> {
        return context.dataStore.data.map {
            it[labelsKey] ?: "{}"
        }
    }

    //token
    fun setToken(token: String) {
        with(preferences.edit()){
            putString("token",if(token.isNotEmpty())".wmsAuth=$token" else "")
            apply()
        }
    }

    fun getToken() : String {
        return preferences.getString("token","")?:""
    }

    //access permissions
    fun setAccessPermission(accessPermissionModel: AccessPermissionModel){
        with(preferences.edit()){
            putString("access",Gson().toJson(accessPermissionModel))
            apply()
        }
    }

    fun getAccessPermission() : AccessPermissionModel? {
        return try {
            Gson().fromJson(preferences.getString("access",""), AccessPermissionModel::class.java)
        } catch (e: Exception){
            null
        }
    }

    //profile
    fun setProfile(profile: String) {
        with(preferences.edit()){
            putString("profile",profile)
            apply()
        }
    }

    fun getProfile() : String {
        return preferences.getString("profile","")?:""
    }
    //modify and waste
    fun setHasModifyPick(has: Boolean){
        with(preferences.edit()) {
            putBoolean("hasModify",has)
            apply()
        }
    }

    fun getHasModifyPick() : Boolean {
        return preferences.getBoolean("hasModify",true)
    }

    fun setHasWaste(has: Boolean) {
        with(preferences.edit()) {
            putBoolean("hasWaste",has)
            apply()
        }
    }

    fun getHasWaste() : Boolean {
        return preferences.getBoolean("hasWaste",true)
    }
    //pick cancel
    fun setHasPickCancel(has: Boolean) {
        with(preferences.edit()){
            putBoolean("hasPickCancel",has)
            apply()
        }
    }

    fun getHasPickCancel() : Boolean {
        return preferences.getBoolean("hasPickCancel",false)
    }
    //warehouse
    fun setWarehouse(warehouse: WarehouseModel?) {
        with(preferences.edit()){
            putString("warehouse", Gson().toJson(warehouse?:""))
            apply()
        }
    }

    fun getWarehouse() : WarehouseModel? {
        return try {
            Gson().fromJson(preferences.getString("warehouse","")?:"", WarehouseModel::class.java)
        }catch (e: Exception) {
            null
        }
    }
    //extra cycle count

    fun setAddExtraCycleCount(add: Boolean) {
        with(preferences.edit()){
            putBoolean("extraCycleCount",add)
            apply()
        }
    }

    fun getAddExtraCycleCount() : Boolean {
        return preferences.getBoolean("extraCycleCount",false)
    }

    //full name
    fun setFullName(fullName: String) {
        with(preferences.edit()){
            putString("fullName",fullName)
            apply()
        }
    }

    fun getFullName() : String{
        return preferences.getString("fullName","")?:""
    }


    //user name
    fun setUserName(userName: String) {
        with(preferences.edit()){
            putString("userName",userName)
            apply()
        }
    }
    fun getUserName() : String {
        return preferences.getString("userName","")?:""
    }


    //password
    fun setPassword(password: String) {
        with(preferences.edit()){
            putString("password",password)
            apply()
        }
    }
    fun getPassword() : String {
        return preferences.getString("password","")?:""
    }


    //address
    fun setAddress(address: String) {
        with(preferences.edit()){
            putString("address",if (address.endsWith('/')) address else "$address/")
            apply()
        }
    }
    fun getAddress() : String {
        return preferences.getString("address", BASE_URL)?: BASE_URL
    }


    //isNavToDetail
    fun setIsNavToDetail(isNavToDetail: Boolean) {
        with(preferences.edit()){
            putBoolean("isNavToDetail",isNavToDetail)
            apply()
        }
    }
    fun getIsNavToDetail() : Boolean {
        return preferences.getBoolean("isNavToDetail",false)
    }


    //isNavToParent
    fun setIsNavToParent(isNavToParent: Boolean) {
        with(preferences.edit()){
            putBoolean("isNavToParent",isNavToParent)
            apply()
        }
    }
    fun getIsNavToParent() : Boolean {
        return preferences.getBoolean("isNavToParent",false)
    }

    //sort and order preferences

    //counting
    fun setCountingSort(sort: String) {
        with(preferences.edit()){
            putString("countingSort",sort)
            apply()
        }
    }

    fun getCountingSort() : String {
        return preferences.getString("countingSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setCountingOrder(order: String) {
        with(preferences.edit()){
            putString("countingOrder",order)
            apply()
        }
    }

    fun getCountingOrder() : String {
        return preferences.getString("countingOrder", Order.Desc.value)?: Order.Desc.value
    }

    //counting detail
    fun setCountingDetailSort(sort: String){
        with(preferences.edit()){
            putString("countingDetailSort",sort)
            apply()
        }
    }

    fun getCountingDetailSort() : String {
        return preferences.getString("countingDetailSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setCountingDetailOrder(order: String){
        with(preferences.edit()){
            putString("countingDetailOrder",order)
            apply()
        }
    }

    fun getCountingDetailOrder() : String {
        return preferences.getString("countingDetailOrder", Order.Desc.value)?: Order.Desc.value
    }


    //putaway detail
    fun setPutawayDetailSort(sort: String) {
        with(preferences.edit()){
            putString("putawayDetailSort",sort)
            apply()
        }
    }

    fun getPutawayDetailSort() : String {
        return preferences.getString("putawayDetailSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setPutawayDetailOrder(order: String) {
        with(preferences.edit()){
            putString("putawayDetailOrder",order)
            apply()
        }
    }

    fun getPutawayDetailOrder() : String {
        return preferences.getString("putawayDetailOrder", Order.Desc.value)?: Order.Desc.value
    }

    //manual putaway
    fun setManualPutawaySort(sort: String) {
        with(preferences.edit()) {
            putString("manualPutawaySort",sort)
            apply()
        }
    }

    fun getManualPutawaySort() : String {
        return preferences.getString("manualPutawaySort", DEFAULT_SORT) ?: DEFAULT_SORT
    }

    fun setManualPutawayOrder(order: String) {
        with(preferences.edit()) {
            putString("manualPutawayOrder",order)
            apply()
        }
    }

    fun getManualPutawayOrder() : String {
        return preferences.getString("manualPutawayOrder",Order.Desc.value) ?: Order.Desc.value
    }

    //manual putaway detail
    fun setManualPutawayDetailSort(sort: String) {
        with(preferences.edit()) {
            putString("manualPutawayDetailSort",sort)
            apply()
        }
    }

    fun getManualPutawayDetailSort() : String {
        return preferences.getString("manualPutawayDetailSort", DEFAULT_SORT) ?: DEFAULT_SORT
    }

    fun setManualPutawayDetailOrder(order: String) {
        with(preferences.edit()) {
            putString("manualPutawayDetailOrder",order)
            apply()
        }
    }

    fun getManualPutawayDetailOrder() : String {
        return preferences.getString("manualPutawayDetailOrder",Order.Desc.value) ?: Order.Desc.value
    }

    //picking
    fun setPickingSort(sort: String) {
        with(preferences.edit()){
            putString("pickingSort",sort)
            apply()
        }
    }

    fun getPickingSort() : String {
        return preferences.getString("pickingSort", "ProductName")?: "ProductName"
    }

    fun setPickingOrder(order: String) {
        with(preferences.edit()){
            putString("pickingOrder",order)
            apply()
        }
    }

    fun getPickingOrder() : String {
        return preferences.getString("pickingOrder", Order.Desc.value)?: Order.Desc.value
    }

    //picking customer
    fun setPickingCustomerSort(sort: String) {
        with(preferences.edit()){
            putString("pickingCustomerSort",sort)
            apply()
        }
    }

    fun getPickingCustomerSort() : String {
        return preferences.getString("pickingCustomerSort", "CustomerName")?: "CustomerName"
    }

    fun setPickingCustomerOrder(order: String) {
        with(preferences.edit()){
            putString("pickingCustomerOrder",order)
            apply()
        }
    }

    fun getPickingCustomerOrder() : String {
        return preferences.getString("pickingCustomerOrder", Order.Desc.value)?: Order.Desc.value
    }

    //purchase order
    fun setPurchaseOrderSort(sort: String) {
        with(preferences.edit()){
            putString("purchaseSort",sort)
            apply()
        }
    }

    fun getPurchaseOrderSort() : String {
        return preferences.getString("purchaseSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setPurchaseOrderOrder(order: String) {
        with(preferences.edit()){
            putString("purchaseOrder",order)
            apply()
        }
    }

    fun getPurchaseOrderOrder() : String {
        return preferences.getString("purchaseOrder", Order.Desc.value)?: Order.Desc.value
    }

    //purchase order detail
    fun setPurchaseOrderDetailSort(sort: String) {
        with(preferences.edit()){
            putString("purchaseDetailSort",sort)
            apply()
        }
    }

    fun getPurchaseOrderDetailSort() : String {
        return preferences.getString("purchaseDetailSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setPurchaseOrderDetailOrder(order: String) {
        with(preferences.edit()){
            putString("purchaseDetailOrder",order)
            apply()
        }
    }

    fun getPurchaseOrderDetailOrder() : String {
        return preferences.getString("purchaseDetailOrder", Order.Desc.value)?: Order.Desc.value
    }

    //shipping order detail
    fun setShippingOrderDetailSort(sort: String) {
        with(preferences.edit()){
            putString("shippingDetailSort",sort)
            apply()
        }
    }

    fun getShippingOrderDetailSort() : String {
        return preferences.getString("shippingDetailSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setShippingOrderDetailOrder(order: String) {
        with(preferences.edit()){
            putString("shippingDetailOrder",order)
            apply()
        }
    }

    fun getShippingOrderDetailOrder() : String {
        return preferences.getString("shippingDetailOrder", Order.Desc.value)?: Order.Desc.value
    }

    //putaway
    fun setPutawaySort(sort: String) {
        with(preferences.edit()){
            putString("putawaySort",sort)
            apply()
        }
    }

    fun getPutawaySort() : String {
        return preferences.getString("putawaySort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setPutawayOrder(order: String) {
        with(preferences.edit()){
            putString("putawayOrder",order)
            apply()
        }
    }

    fun getPutawayOrder() : String {
        return preferences.getString("putawayOrder", Order.Desc.value)?: Order.Desc.value
    }

    //shipping
    fun setShippingSort(sort: String) {
        with(preferences.edit()){
            putString("shippingSort",sort)
            apply()
        }
    }

    fun getShippingSort() : String {
        return preferences.getString("shippingSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setShippingOrder(order: String) {
        with(preferences.edit()){
            putString("shippingOrder",order)
            apply()
        }
    }

    fun getShippingOrder() : String {
        return preferences.getString("shippingOrder", Order.Desc.value)?: Order.Desc.value
    }

    //cycle
    fun setCycleSort(sort: String) {
        with(preferences.edit()){
            putString("cycleSort",sort)
            apply()
        }
    }

    fun getCycleSort() : String {
        return preferences.getString("cycleSort", "ProductCode")?: "ProductCode"
    }

    fun setCycleOrder(order: String) {
        with(preferences.edit()){
            putString("cycleOrder",order)
            apply()
        }
    }

    fun getCycleOrder() : String {
        return preferences.getString("cycleOrder", Order.Desc.value)?: Order.Desc.value
    }
    //transfer put
    fun setTransferSort(sort: String) {
        with(preferences.edit()){
            putString("transferPutSort",sort)
            apply()
        }
    }

    fun getTransferSort() : String {
        return preferences.getString("transferPutSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setTransferOrder(order: String) {
        with(preferences.edit()){
            putString("transferPutOrder",order)
            apply()
        }
    }

    fun getTransferOrder() : String {
        return preferences.getString("transferPutOrder", Order.Desc.value)?: Order.Desc.value
    }

    //cycle detail
    fun setCycleDetailSort(sort: String) {
        with(preferences.edit()){
            putString("cycleDetailSort",sort)
            apply()
        }
    }

    fun getCycleDetailSort() : String {
        return preferences.getString("cycleDetailSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setCycleDetailOrder(order: String) {
        with(preferences.edit()){
            putString("cycleDetailOrder",order)
            apply()
        }
    }

    fun getCycleDetailOrder() : String {
        return preferences.getString("cycleDetailOrder", Order.Desc.value)?: Order.Desc.value
    }


    //checking

    fun setCheckingSort(sort: String) {
        with(preferences.edit()){
            putString("checkingSort",sort)
            apply()
        }
    }

    fun getCheckingSort() : String {
        return preferences.getString("checkingSort", DEFAULT_SORT) ?: DEFAULT_SORT
    }

    fun setCheckingOrder(order: String) {
        with(preferences.edit()) {
            putString("checkingOrder",order)
            apply()
        }
    }

    fun getCheckingOrder() : String {
        return preferences.getString("checkingOrder", Order.Desc.value) ?: Order.Desc.value
    }

    //checking detail

    fun setCheckingDetailSort(sort: String) {
        with(preferences.edit()){
            putString("checkingDetailSort",sort)
            apply()
        }
    }

    fun getCheckingDetailSort() : String {
        return preferences.getString("checkingDetailSort", DEFAULT_SORT) ?: DEFAULT_SORT
    }

    fun setCheckingDetailOrder(order: String) {
        with(preferences.edit()) {
            putString("checkingDetailOrder",order)
            apply()
        }
    }

    fun getCheckingDetailOrder() : String {
        return preferences.getString("checkingDetailOrder", Order.Desc.value) ?: Order.Desc.value
    }


    //pallet confirm
    fun setPalletSort(sort: String) {
        with(preferences.edit()){
            putString("palletSort",sort)
            apply()
        }
    }

    fun getPalletSort() : String {
        return preferences.getString("palletSort", DEFAULT_SORT) ?: DEFAULT_SORT
    }

    fun setPalletOrder(order: String) {
        with(preferences.edit()) {
            putString("palletOrder", order)
            apply()
        }
    }

    fun getPalletOrder() : String {
        return preferences.getString("palletOrder",Order.Desc.value) ?: Order.Desc.value
    }

    //loading
    fun setLoadingSort(sort: String) {
        with(preferences.edit()){
            putString("loadingSort",sort)
            apply()
        }
    }

    fun getLoadingSort() : String {
        return preferences.getString("loadingSort", DEFAULT_SORT) ?: DEFAULT_SORT
    }

    fun setLoadingOrder(order: String) {
        with(preferences.edit()) {
            putString("loadingOrder",order)
            apply()
        }
    }

    fun getLoadingOrder() : String {
        return preferences.getString("loadingOrder", Order.Desc.value) ?: Order.Desc.value
    }

    //loading detail
    fun setLoadingDetailSort(sort: String) {
        with(preferences.edit()){
            putString("loadingDetailSort",sort)
            apply()
        }
    }

    fun getLoadingDetailSort() : String {
        return preferences.getString("loadingDetailSort", DEFAULT_SORT) ?: DEFAULT_SORT
    }

    fun setLoadingDetailOrder(order: String) {
        with(preferences.edit()) {
            putString("loadingDetailOrder",order)
            apply()
        }
    }

    fun getLoadingDetailOrder() : String {
        return preferences.getString("loadingDetailOrder", Order.Desc.value) ?: Order.Desc.value
    }


    //rs integration
    fun setRSSort(sort: String) {
        with(preferences.edit()){
            putString("rsSort",sort)
            apply()
        }
    }

    fun getRSSort() : String {
        return preferences.getString("rsSort", DEFAULT_SORT) ?: DEFAULT_SORT
    }

    fun setRSOrder(order: String) {
        with(preferences.edit()){
            putString("rsOrder",order)
            apply()
        }
    }

    fun getRSOrder() : String {
        return preferences.getString("rsOrder", Order.Desc.value) ?: Order.Desc.value
    }


    //waybill
    fun setWaybillSort(sort: String) {
        with(preferences.edit()){
            putString("waybillSort",sort)
            apply()
        }
    }

    fun getWaybillSort() : String {
        return preferences.getString("waybillSort", DEFAULT_SORT) ?: DEFAULT_SORT
    }

    fun setWaybillOrder(order: String) {
        with(preferences.edit()){
            putString("waybillOrder",order)
            apply()
        }
    }

    fun getWaybillOrder() : String {
        return preferences.getString("waybillOrder", Order.Desc.value) ?: Order.Desc.value
    }

    //shipping detail
    fun setShippingDetailSort(sort: String) {
        with(preferences.edit()){
            putString("shippingDetailSort",sort)
            apply()

        }
    }

    fun getShippingDetailSort() : String {
        return preferences.getString("shippingDetailSort","PalletBarcode") ?: "PalletBarcode"
    }

    fun setShippingDetailOrder(order: String) {
        with(preferences.edit()) {
            putString("shippingDetailOrder",order)
            apply()
        }
    }

    fun getShippingDetailOrder() : String {
        return preferences.getString("shippingDetailOrder", Order.Asc.value) ?: Order.Asc.value
    }

    //lock keyboard
    suspend fun setLockKeyboard(lock: Boolean) {
//        with(preferences.edit()){
//            putBoolean("lockKeyboard",lock)
//            apply()
//        }
        context.dataStore.edit {
            it[lockKeyboardKey] = lock
        }
    }

    fun getLockKeyboard() : Flow<Boolean> {
//        return preferences.getBoolean("lockKeyboard", true)
        return context.dataStore.data.map { it[lockKeyboardKey] ?: true }
    }


    //validate Pallet
    fun setValidatePallet(validate: Boolean) {
        with(preferences.edit()){
            putBoolean("validatePallet",validate)
            apply()
        }
    }

    fun getValidatePallet() : Boolean {
        return preferences.getBoolean("validatePallet", true)
    }



    //lat and long
    fun setLatitude(latitude: String) {
        with(preferences.edit()){
            putString("lat",latitude)
            apply()
        }
    }

    fun getLatitude() : String {
        return preferences.getString("lat","") ?:""
    }

    fun setLongitude(longitude: String) {
        with(preferences.edit()){
            putString("long",longitude)
            apply()
        }
    }

    fun getLongitude() : String {
        return preferences.getString("long","") ?: ""
    }
    // tracking
    suspend fun setTracking(tracking: Boolean) {
        context.dataStore.edit {
            it[trackingKey] = tracking
        }
    }

    fun getTracking() : Flow<Boolean> {
        return context.dataStore.data.map {
            it[trackingKey] ?: true
        }
    }

    //enable auto open checking for none completed checking
    fun setEnableAutoOpenChecking(enable: Boolean) {
        with(preferences.edit()){
            putBoolean("enableAutoOpenChecking",enable)
            apply()
        }
    }

    fun getEnableAutoOpenChecking() : Boolean {
        return preferences.getBoolean("enableAutoOpenChecking", true)
    }

}
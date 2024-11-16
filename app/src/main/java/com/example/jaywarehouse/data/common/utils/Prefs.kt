package com.example.jaywarehouse.data.common.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.jaywarehouse.data.common.modules.dataStore
import com.example.jaywarehouse.presentation.common.utils.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class Prefs(private val context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE)

    val lockKeyboardKey = booleanPreferencesKey("lockKeyboardKey")
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
            putString("address",address)
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
        return preferences.getString("pickingSort", DEFAULT_SORT)?: DEFAULT_SORT
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
        return preferences.getString("pickingCustomerSort", DEFAULT_SORT)?: DEFAULT_SORT
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

    //packing
    fun setPackingSort(sort: String) {
        with(preferences.edit()){
            putString("packingSort",sort)
            apply()
        }
    }

    fun getPackingSort() : String {
        return preferences.getString("packingSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setPackingOrder(order: String) {
        with(preferences.edit()){
            putString("packingOrder",order)
            apply()
        }
    }

    fun getPackingOrder() : String {
        return preferences.getString("packingOrder", Order.Desc.value)?: Order.Desc.value
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
            putString("shippingDetailSort",sort)
            apply()
        }
    }

    fun getCycleSort() : String {
        return preferences.getString("shippingDetailSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setCycleOrder(order: String) {
        with(preferences.edit()){
            putString("shippingDetailOrder",order)
            apply()
        }
    }

    fun getCycleOrder() : String {
        return preferences.getString("shippingDetailOrder", Order.Desc.value)?: Order.Desc.value
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
            putString("transferPickSort",sort)
            apply()
        }
    }

    fun getCycleDetailSort() : String {
        return preferences.getString("transferPickSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setCycleDetailOrder(order: String) {
        with(preferences.edit()){
            putString("transferPickOrder",order)
            apply()
        }
    }

    fun getCycleDetailOrder() : String {
        return preferences.getString("transferPickOrder", Order.Desc.value)?: Order.Desc.value
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




}
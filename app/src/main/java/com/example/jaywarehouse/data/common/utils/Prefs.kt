package com.example.jaywarehouse.data.common.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.jaywarehouse.data.common.modules.dataStore
import com.example.jaywarehouse.data.common.modules.networkModule
import com.example.jaywarehouse.presentation.common.utils.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules


class Prefs(private val context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE)

    val lockKeyboardKey = booleanPreferencesKey("lockKeyboardKey")
    //token
    fun setToken(token: String) {
        with(preferences.edit()){
            putString("token",if(token.isNotEmpty())".nByteAuth=$token" else "")
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

    //shipping detail
    fun setShippingDetailSort(sort: String) {
        with(preferences.edit()){
            putString("shippingDetailSort",sort)
            apply()
        }
    }

    fun getShippingDetailSort() : String {
        return preferences.getString("shippingDetailSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setShippingDetailOrder(order: String) {
        with(preferences.edit()){
            putString("shippingDetailOrder",order)
            apply()
        }
    }

    fun getShippingDetailOrder() : String {
        return preferences.getString("shippingDetailOrder", Order.Desc.value)?: Order.Desc.value
    }
    //transfer put
    fun setTransferPutSort(sort: String) {
        with(preferences.edit()){
            putString("transferPutSort",sort)
            apply()
        }
    }

    fun getTransferPutSort() : String {
        return preferences.getString("transferPutSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setTransferPutOrder(order: String) {
        with(preferences.edit()){
            putString("transferPutOrder",order)
            apply()
        }
    }

    fun getTransferPutOrder() : String {
        return preferences.getString("transferPutOrder", Order.Desc.value)?: Order.Desc.value
    }

    //transfer pick
    fun setTransferPickSort(sort: String) {
        with(preferences.edit()){
            putString("transferPickSort",sort)
            apply()
        }
    }

    fun getTransferPickSort() : String {
        return preferences.getString("transferPickSort", DEFAULT_SORT)?: DEFAULT_SORT
    }

    fun setTransferPickOrder(order: String) {
        with(preferences.edit()){
            putString("transferPickOrder",order)
            apply()
        }
    }

    fun getTransferPickOrder() : String {
        return preferences.getString("transferPickOrder", Order.Desc.value)?: Order.Desc.value
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
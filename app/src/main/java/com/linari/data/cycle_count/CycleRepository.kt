package com.linari.data.cycle_count

import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.getResult
import com.linari.data.cycle_count.models.CycleDetailModel
import com.linari.data.cycle_count.models.CycleModel
import com.linari.presentation.common.utils.SortItemDto
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class CycleRepository(
    private val api: CycleApi
){
    fun getCycleCountLocations(
        keyword: String,
        page: Int,
        sort: String,
        order: String
    ) = getResult(
        request = {
            val sortList = listOf(
//                SortItemDto(1,"IsEmpty","asc"),
//                SortItemDto(2,"Counting","desc"),
                SortItemDto(3,sort,order)
            )
            val jsonObject = JsonObject()
            jsonObject.addProperty("Keyword",keyword)
            api.getCycleCountLocations(
                jsonObject,
                page,
                ROW_COUNT,
                Gson().toJson(sortList.toTypedArray())
            )
        }
    )

    fun getCycleCountLocationDetail(
        keyword: String,
        cycleCountWorkerTaskID: String,
        page: Int,
        sort: String,
        order: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Keyword",keyword)
            jsonObject.addProperty("CycleCountWorkerTaskID",cycleCountWorkerTaskID)
            api.getCycleCountLocationDetail(
                jsonObject,
                page,
                ROW_COUNT,
                sort,
                order
            )
        }
    )

    fun insertTaskDetail(
        productBarcodeNumber: String,
        cycleCountWorkerTaskID: String,
        expireDate: String,
        quiddityTypeID: String,
        quantity: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ProductBarcodeNumber",productBarcodeNumber)
            jsonObject.addProperty("ExpireDate",expireDate)
            jsonObject.addProperty("CycleCountWorkerTaskID",cycleCountWorkerTaskID)
            jsonObject.addProperty("QuiddityTypeID",quiddityTypeID)
            jsonObject.addProperty("Quantity",quantity)
            api.insertTaskDetail(jsonObject)
        }
    )

    fun updateQuantity(
        cycleCountWorkerTaskDetailID: String,
        quantity: String
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("CycleCountWorkerTaskDetailID",cycleCountWorkerTaskDetailID)
            jsonObject.addProperty("Quantity",quantity)
            api.updateQuantity(jsonObject)
        }
    )

    fun finishCycleCount(cycleCountWorkerTaskID: String) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("CycleCountWorkerTaskID",cycleCountWorkerTaskID)
            api.locationTaskEnd(jsonObject)
        }
    )
}
package com.example.jaywarehouse.data.cycle_count

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.cycle_count.models.CycleDetailModel
import com.example.jaywarehouse.data.cycle_count.models.CycleModel
import com.example.jaywarehouse.presentation.common.utils.SortItemDto
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
                10,
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
                10,
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
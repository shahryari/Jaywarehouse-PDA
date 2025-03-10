package com.example.jaywarehouse.data.receiving.repository

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.receiving.ReceivingApi
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailCountModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailGetItemsModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanRemoveModel
import com.example.jaywarehouse.data.receiving.model.ReceivingModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class ReceivingRepository(
    private val api: ReceivingApi
) {



    suspend fun getReceivingList(
        keyword: String,
        page: Int,
        rows: Int,
        order: String,
        sort: String
    ) : Flow<BaseResult<ReceivingModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getReceivingList(jsonObject,page, rows, sort, order)
            }
        )
    }

    suspend fun getReceivingDetails(
        receivingID: Int,
        keyword: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<ReceivingDetailModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ReceivingID",receivingID)
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getReceivingDetails(jsonObject, page, rows, sort, order)
            }
        )
    }

    fun countReceivingDetail(
        receivingId: Int,
        quantity: Int,
        receivingTypeId: Int,
        counts: List<ReceivingDetailCountModel>
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonArray = JsonArray()
        counts.map {
            val countObject = JsonObject()
            countObject.addProperty("ExpireDateString",it.expireDate)
            countObject.addProperty("BatchNumber",it.batchNumber)
            countObject.addProperty("CountQuantity",it.quantity)
            countObject.addProperty("EntityState",it.entityState)
            if (it.receivingWorkerTaskId!=null)countObject.addProperty("ReceivingWorkerTaskID",it.receivingWorkerTaskId)
            if (it.receivingWorkerTaskCountId!=null)countObject.addProperty("ReceivingWorkerTaskCountID",it.receivingWorkerTaskCountId)
            countObject
        }.forEach {
            jsonArray.add(it)
        }
        val jsonObject = JsonObject()
        jsonObject.add("ReceivingWorkerTaskCounts",jsonArray)
        jsonObject.addProperty("ReceivingID",receivingId)
        jsonObject.addProperty("UOMQuantity",quantity)
        jsonObject.addProperty("ReceivingTypeID",receivingTypeId)


        return getResult(
            request = {
                api.countReceivingDetail(jsonObject)
            }
        )

    }

    suspend fun getReceivingDetailCountModel(
        receivingWorkerTaskId: Int
    ) : Flow<BaseResult<ReceivingDetailGetItemsModel>>{
        val jsonObject = JsonObject()
        jsonObject.addProperty("ReceivingWorkerTaskID",receivingWorkerTaskId)

        return getResult(
            request = {
                api.getReceivingDetailCountItems(jsonObject,1,1000,"CreatedOn","desc")
            }
        )
    }

}
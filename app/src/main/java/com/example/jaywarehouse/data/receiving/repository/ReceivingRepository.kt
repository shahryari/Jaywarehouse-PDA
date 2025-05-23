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
        isCrossDock: Boolean,
        page: Int,
        rows: Int,
        order: String,
        sort: String
    ) : Flow<BaseResult<ReceivingModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("ReceivingType",if (isCrossDock) 2 else 1)
        return getResult(
            request = {
                api.getReceivingList(jsonObject,page, rows, sort, order)
            }
        )
    }

    suspend fun getReceivingDetails(
        receivingID: Int,
        isCrossDock: Boolean,
        keyword: String,
        page: Int,
        rows: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<ReceivingDetailModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ReceivingID",receivingID)
        jsonObject.addProperty("ReceivingType",if (isCrossDock) 2 else 1)
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getReceivingDetails(jsonObject, page, rows, sort, order)
            }
        )
    }

    suspend fun getReceivingDetailCountModel(
        receivingWorkerTaskId: Int,
        page: Int,
        isCrossDock: Boolean
    ) : Flow<BaseResult<ReceivingDetailGetItemsModel>>{
        val jsonObject = JsonObject()
        jsonObject.addProperty("ReceivingWorkerTaskID",receivingWorkerTaskId)
        jsonObject.addProperty("ReceivingType",if (isCrossDock) 2 else 1)
        return getResult(
            request = {
                api.getReceivingDetailCountItems(jsonObject,page,10,"CreatedOn","desc")
            }
        )
    }

    fun receivingWorkerTaskCountInsert(
        receivingWorkerTaskId: Int,
        quantity: Double,
        quantityInPacket: Double?,
        pack: Int?,
        expireDate: String,
        batchNumber: String,
        isCrossDock: Boolean
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ReceivingType",if (isCrossDock) 2 else 1)
            jsonObject.addProperty("ReceivingWorkerTaskID",receivingWorkerTaskId)
            jsonObject.addProperty("CountQuantity",quantity)
            jsonObject.addProperty("PCB",quantityInPacket)
            jsonObject.addProperty("Pack",pack)
            jsonObject.addProperty("ExpireDate",expireDate)
            jsonObject.addProperty("BatchNumber",batchNumber)
            api.receivingWorkerTaskCountInsert(jsonObject)
        }
    )

    fun receivingWorkerTaskCountDelete(
        receivingWorkerTaskCountID: String,
        isCrossDock: Boolean
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ReceivingType",if (isCrossDock) 2 else 1)
            jsonObject.addProperty("ReceivingWorkerTaskCountID",receivingWorkerTaskCountID)
            api.receivingWorkerTaskCountDelete(jsonObject)
        }
    )

    fun receivingWorkerTaskDone(
        receivingWorkerTaskId: Int,
        isCrossDock: Boolean
    ) = getResult(
        request = {
            val jsonObject = JsonObject()
            jsonObject.addProperty("ReceivingWorkerTaskID",receivingWorkerTaskId)
            jsonObject.addProperty("ReceivingType",if (isCrossDock) 2 else 1)
            api.receivingWorkerTaskDone(jsonObject)
        }
    )

}
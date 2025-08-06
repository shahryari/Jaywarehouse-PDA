package com.linari.data.receiving.repository

import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.common.utils.getResult
import com.linari.data.receiving.ReceivingApi
import com.linari.data.receiving.model.ReceivingDetailGetItemsModel
import com.linari.data.receiving.model.ReceivingDetailModel
import com.linari.data.receiving.model.ReceivingModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class ReceivingRepository(
    private val api: ReceivingApi
) {



    suspend fun getReceivingList(
        keyword: String,
        isCrossDock: Boolean,
        warehouseID: Int,
        page: Int,
        rows: Int,
        order: String,
        sort: String
    ) : Flow<BaseResult<ReceivingModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("WarehouseID",warehouseID)
//        jsonObject.addProperty("ReceivingType",if (isCrossDock) 2 else 1)
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
//        jsonObject.addProperty("ReceivingType",if (isCrossDock) 2 else 1)
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
//        jsonObject.addProperty("ReceivingType",if (isCrossDock) 2 else 1)
        return getResult(
            request = {
                api.getReceivingDetailCountItems(jsonObject,page,ROW_COUNT,"CreatedOn","desc")
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
//            jsonObject.addProperty("ReceivingType",if (isCrossDock) 2 else 1)
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
//            jsonObject.addProperty("ReceivingType",if (isCrossDock) 2 else 1)
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
//            jsonObject.addProperty("ReceivingType",if (isCrossDock) 2 else 1)
            api.receivingWorkerTaskDone(jsonObject)
        }
    )

}
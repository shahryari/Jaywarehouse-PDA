package com.example.jaywarehouse.data.cycle_count

import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.getResult
import com.example.jaywarehouse.data.cycle_count.models.CycleDetailModel
import com.example.jaywarehouse.data.cycle_count.models.CycleModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class CycleRepository(
    private val api: CycleApi
){
    fun getStockTakingList(
        keyword: String,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<CycleModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        return getResult(
            request = {
                api.getStockTakingList(jsonObject,page,10,sort,order)
            }
        )
    }

    fun getStockTakingWorkerTaskList(
        keyword: String,
        stockTakingId: String,
        page: Int,
        sort: String,
        order: String
    ) : Flow<BaseResult<CycleDetailModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("Keyword",keyword)
        jsonObject.addProperty("StockTakingID",stockTakingId)
        return getResult(
            request = {
                api.getStockTakingWorkerTaskList(jsonObject,page,10,sort,order)
            }
        )
    }

    fun updateQuantity(
        stockTakingWorkerTaskID: Int,
        quantity: Int,
        quantityInPacket: Int,
        expireDate: String,
    ) : Flow<BaseResult<ResultMessageModel>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("StockTakingWorkerTaskID",stockTakingWorkerTaskID)
        jsonObject.addProperty("CountQuantity",quantity)
        jsonObject.addProperty("UOMQuantity",quantityInPacket)
        jsonObject.addProperty("ExpireDate",expireDate)
        return getResult(
            request = {
                api.updateQuantity(jsonObject)
            }
        )
    }
}